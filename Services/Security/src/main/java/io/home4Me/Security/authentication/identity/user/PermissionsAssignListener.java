package io.home4Me.Security.authentication.identity.user;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import io.home4Me.Security.RoleTypes;

@Component
class PermissionsAssignListener implements ApplicationListener<PermissionGainEvent> {

	private static final Logger logger = LogManager.getLogger(PermissionsAssignListener.class);
	
	@Autowired
	private RoleService rolesService;

	@Override
	public void onApplicationEvent(PermissionGainEvent event) {
		
		Set<RoleTypes> roles = event.getRoles();
		LoginDetails loginDetails = event.getLoginDetails();
		
		logger.debug("Assigning roles : {} to user with id {}", roles, loginDetails.getId());
		rolesService.overrideUserRoles(loginDetails, roles);
	}
}
