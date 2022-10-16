package io.home4Me.Security;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import io.home4Me.Security.authentication.dto.RegisterRequest;
import io.home4Me.Security.authentication.entity.LoginDetails;
import io.home4Me.Security.authentication.services.LoginDetailsService;

@Component
@Profile("bootstrap")
public class BootstrapInit implements CommandLineRunner {

	@Autowired
	private LoginDetailsService loginDetailsService;

	@Override
	@Transactional
	public void run(String... args) throws Exception {

		LoginDetails anyUser = loginDetailsService
				.createUser(
						RegisterRequest.builder().email("test@gmail.com").username("anyUsername")
								.password("anyPassword").build()
		);

	}
}
