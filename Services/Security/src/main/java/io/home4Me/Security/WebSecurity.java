package io.home4Me.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import io.home4Me.Security.authentication.filters.JwtFilter;
import io.home4Me.Security.authentication.services.LoginDetailsService;

public class WebSecurity extends WebSecurityConfigurerAdapter  {

	private final LoginDetailsService loginDetailsService;
	
	@Autowired
	public WebSecurity(LoginDetailsService loginDetailsService) {
		this.loginDetailsService = loginDetailsService;
	}
	
	
	@Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception{
	        auth.userDetailsService(loginDetailsService).passwordEncoder(encoder());
    }

	@Bean
    public JwtFilter authenticationJwtTokenFilter() {
        return new JwtFilter();
    }
	
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
	       return super.authenticationManagerBean();
    }
	
	@Bean
	public PasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}
	
}
