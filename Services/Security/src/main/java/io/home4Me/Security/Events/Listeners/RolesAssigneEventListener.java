package io.home4Me.Security.Events.Listeners;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import io.home4Me.Security.RoleTypes;
import io.home4Me.Security.authentication.entity.LoginDetails;
import io.home4Me.Security.authentication.entity.RolesAssigneEvent;
import io.home4Me.Security.authentication.services.RoleService;

@Component
public class RolesAssigneEventListener implements ApplicationListener<RolesAssigneEvent> {
	
	private static final Logger logger = LogManager.getLogger(RolesAssigneEventListener.class);
	
	@Autowired
	private RoleService rolesService;

	@Override
	public void onApplicationEvent(RolesAssigneEvent event) {
		
		Set<RoleTypes> roles = event.getRoles();
		LoginDetails loginDetails = event.getLoginDetails();
		
		logger.debug("Assigning roles : {} to user with id {}", roles, loginDetails.getId());
		rolesService.overrideUserRoles(loginDetails, roles);
	}
}
