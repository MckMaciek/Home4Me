package io.home4Me.Security.authentication.identity.user;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration(proxyBeanMethods = false)
// using proxyBeanMethods false remember to pass additional dependencies via method parameters, 
// not using new, because otherwise spring will create multiple object instances
class LoginDetailsConfiguration {
		
	private final LoginDetailsDao loginDetailsDao;
	private final PasswordEncoder encoder;
	
	public LoginDetailsConfiguration(LoginDetailsDao loginDetailsDao, PasswordEncoder encoder) {
		this.loginDetailsDao = loginDetailsDao;
		this.encoder = encoder;
	}
	
	@Bean
	public LoginDetailsService loginDetailsService() {
		return new LoginDetailsService(loginDetailsDao, encoder);
	}
}
