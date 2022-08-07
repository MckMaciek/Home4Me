package io.home4Me.Security.Token;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import io.home4Me.Security.RoleTypes;
import io.home4Me.Security.VerificationInfo;
import io.home4Me.Security.utils.AccessToken;
import io.home4Me.Security.utils.JwtUtils;
import io.home4Me.Security.utils.RefreshToken;
import io.home4Me.Security.utils.TokenSupplier;
import io.home4Me.Security.utils.TokenWrappee;

@RunWith(SpringJUnit4ClassRunner.class)
public class TokenSupplierTest {
	
	public static final String USERNAME = "ANY_USERNAME";
	public static final String PASSWORD = "ANY_PASSWORD";
	private static final String ROLE_PREFIX = "ROLE_";
	
	public static final String JWT_SECRET = "312321321321312aasdgfdgdfgfdasdsadsa";
	
	public static final Set<RoleTypes> ROLES = Set.of(RoleTypes.ADMIN, RoleTypes.USER);
	
	@Spy
	private JwtUtils jwtUtils;
	
	@InjectMocks
	private TokenSupplier tokenSupplier;
	
	@Test
	public void shouldBuildValidAccessAndRefreshToken() {
		
		setUp();
		UserDetails userDetails = setUpUserDetails();
		
		TokenWrappee generatedTokens = tokenSupplier
											.buildOperation()
											.generateBothTokens()
											.withUserDetails(userDetails)
											.build();
				
		Optional<VerificationInfo> verificationFailures = VerificationInfo.findAnyFailure(generatedTokens.getValidationInfo());
		assertThat(verificationFailures).isEmpty();
		
		String accessToken = generatedTokens.getAccessToken();
		String refreshToken = generatedTokens.getRefreshToken();
		
		AccessToken accessTokenWrapped = jwtUtils.wrapToken(accessToken);
		RefreshToken refreshTokenWrapped  = jwtUtils.wrapToken(refreshToken, accessToken);
		
		assertThat(accessTokenWrapped.getUsername()).isNotBlank();
		assertThat(refreshTokenWrapped.getUsername()).isNotBlank();
		
		assertThat(accessTokenWrapped.getUsername()).isEqualTo(accessTokenWrapped.getUsername());
		assertThat(accessTokenWrapped.getUsername()).isEqualTo(USERNAME);
	}

	
	private void setUp() {	
		ReflectionTestUtils.setField(this.jwtUtils, "jwtSecret", JWT_SECRET);
		ReflectionTestUtils.setField(this.jwtUtils, "refreshTokenExpirationTime", 3600l);
		ReflectionTestUtils.setField(this.jwtUtils, "accessTokenExpirationTime", 3600l);
	}


	@SuppressWarnings("serial")
	private UserDetails setUpUserDetails() {
		return new UserDetails() {
			
			@Override
			public boolean isEnabled() {
				return true;
			}
			
			@Override
			public boolean isCredentialsNonExpired() {
				return true;
			}
			
			@Override
			public boolean isAccountNonLocked() {
				return true;
			}
			
			@Override
			public boolean isAccountNonExpired() {
				return true;
			}
			
			@Override
			public String getUsername() {
				return USERNAME;
			}
			
			@Override
			public String getPassword() {
				return PASSWORD;
			}
			
			@Override
			public Collection<? extends GrantedAuthority> getAuthorities() {
				return ROLES.stream()
						 .map(role -> new SimpleGrantedAuthority(ROLE_PREFIX + role))
						 .collect(Collectors.toList());
			}
		};
	}
	
}
	

