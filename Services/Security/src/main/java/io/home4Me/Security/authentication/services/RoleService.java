package io.home4Me.Security.authentication.services;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.home4Me.Security.RoleTypes;
import io.home4Me.Security.authentication.dao.RolesDao;
import io.home4Me.Security.authentication.entity.LoginDetails;
import io.home4Me.Security.authentication.entity.UserRoles;

@Service
public class RoleService {
	
	private static final Logger logger = LogManager.getLogger(RoleService.class);

	private final RolesDao rolesDao;
	
	@Autowired
	public RoleService(RolesDao rolesDao) {
		this.rolesDao = rolesDao;
	}
	
	@Transactional
	public void addUserRoles(LoginDetails loginDetails, Set<RoleTypes> desiredRoles) {
		if(!validateEntityAndRoles(loginDetails, desiredRoles)) throw new NullPointerException("Null value or empty list provided");
		
		updateUserFetchedRoles(loginDetails, desiredRoles);
		
		logger.debug(String.format("For user with the id of %s - roles has been provided %s", loginDetails.getId(), catRoles(desiredRoles)));
	}
	
	@Transactional
	public void overrideUserRoles(LoginDetails loginDetails, Set<RoleTypes> desiredRoles) {
		if(!validateEntityAndRoles(loginDetails, desiredRoles)) throw new NullPointerException("Null value or empty list provided");
	
		loginDetails.setUserRoles(new HashSet<>());
		updateUserFetchedRoles(loginDetails, desiredRoles);
		
		logger.debug(String.format("For user with the id of %s - roles has been overrided %s", loginDetails.getId(), catRoles(desiredRoles)));
	}
	
	private boolean validateEntityAndRoles(LoginDetails loginDetails, Set<RoleTypes> desiredRoles) {
		return (loginDetails != null && !desiredRoles.isEmpty());
	}
	
	private void updateUserFetchedRoles(LoginDetails loginDetails, Set<RoleTypes> desiredRoles) {
		
		List<UserRoles> allRolesInSystem = rolesDao.findAll();
		
		intersectWithDatabaseValues(allRolesInSystem, desiredRoles).forEach(desiredRole -> {
				Optional<UserRoles> userRoleOpt = allRolesInSystem
													 .stream()
													 .filter(allRolesRole -> allRolesRole.getRole().equals(desiredRole))
													 .findFirst();
				
				userRoleOpt.ifPresent(desiredRolePresent -> loginDetails.getUserRoles().add(desiredRolePresent));
			}
		);
	}
	
	private Set<RoleTypes> intersectWithDatabaseValues(List<UserRoles> allRoleTypes, Set<RoleTypes> roleTypes){
		return allRoleTypes.stream()
				.map(UserRoles::getRole)
				.distinct()
				.filter(roleTypes::contains)
				.collect(Collectors.toSet());
	}
	
	private String catRoles(Set<RoleTypes> roleTypes) {
		return StringUtils.join(roleTypes, "-");
	}
	
	public static Set<RoleTypes> getUserRoleTypes(Set<UserRoles> userRoles){
	    if(userRoles == null) throw new NullPointerException("Provided set is null");
		else if(userRoles.isEmpty()) return new HashSet<>();
		
		return userRoles.stream()
				.map(UserRoles::getRole)
				.collect(Collectors.toSet());
	}
}
