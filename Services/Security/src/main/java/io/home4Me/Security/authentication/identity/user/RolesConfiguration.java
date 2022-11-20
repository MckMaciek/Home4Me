package io.home4Me.Security.authentication.identity.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
//using proxyBeanMethods false remember to pass additional dependencies via method parameters, 
//not using new, because otherwise spring will create multiple object instances
class RolesConfiguration {
	
	private final RolesDao rolesDao;
	
	@Autowired
	public RolesConfiguration(RolesDao rolesDao) {
		this.rolesDao = rolesDao;
	}
	
	@Bean
	public RoleService roleService() {
		return new RoleService(rolesDao);
	}
}
