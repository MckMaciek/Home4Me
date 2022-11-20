package io.home4Me.Security.authentication.identity.user;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.data.domain.AbstractAggregateRoot;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import io.home4Me.Security.RoleTypes;
import io.home4Me.Security.authentication.LogUserCreatedEvent;
import io.home4Me.Security.authentication.dto.RegisterRequest;
import io.home4Me.Security.authentication.identity.user.dto.LoginDetailsDto;
import io.home4Me.Security.commons.identity.CreationDate;
import io.home4Me.Security.commons.identity.EMail;
import io.home4Me.Security.commons.identity.Password;
import io.home4Me.Security.commons.identity.Username;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;


@Entity
@Getter
@AllArgsConstructor
@ToString(exclude = {"password"})
@EqualsAndHashCode(callSuper = false, of = {"username", "password", "email", "creationDate", "userRoles"})
@NamedQueries({
	@NamedQuery(name = LoginDetails.FIND_BY_USERNAME, query = "from LoginDetails ld where username = :inputUsername"),
})
@SuppressWarnings("serial")
@Table(name = "login_details", schema = "public")
class LoginDetails extends AbstractAggregateRoot<LoginDetails> implements UserDetails {
	
	public static final String FIND_BY_USERNAME = "LoginDetails.findByUsername";
	private static final String ROLE_PREFIX = "ROLE_";
	
	public static final boolean IS_ENABLED_BY_DEFAULT = true;
	public static final boolean IS_NON_LOCK_BY_DEFAULT = true;
	public static final boolean IS_NON_EXPIRED_BY_DEFAULT = true;
	public static final boolean IS_CREDS_NON_EXPIRED_BY_DEFAULT = true;
		
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@Embedded
	private Username username;

	@Embedded
	private Password password;
	
	@Embedded
	private EMail email;
	
	@Embedded
	private CreationDate creationDate;
	
	private boolean isUserEnabled = IS_ENABLED_BY_DEFAULT;
	private boolean isUserNonLocked = IS_NON_LOCK_BY_DEFAULT;
	private boolean isUserAccountNonExpired = IS_NON_EXPIRED_BY_DEFAULT;
	private boolean isUserCredentialsNonExpired = IS_CREDS_NON_EXPIRED_BY_DEFAULT;
	
	@OneToMany
	@JoinTable(joinColumns = { @JoinColumn(name = "login_details_id", table = "login_details_roles")},
				inverseJoinColumns = { @JoinColumn(name = "roles_id", table = "login_details_roles")}
	)
	private Set<UserRoles> userRoles = new HashSet<>();
	
	public static LoginDetails mapRequest(RegisterRequest registerRequest) {
		return LoginDetailsMapper.map(registerRequest);
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return userRoles.stream()
			 .map(role -> new SimpleGrantedAuthority(ROLE_PREFIX + role))
			 .collect(Collectors.toList());
	}
		
	public LoginDetails saveWithRoles(Set<RoleTypes> userRoles, PasswordEncoder encoder) {
		this.id = null;
		
		password = (new Password(encoder.encode(password.getPassword())));
		userRoles = new HashSet<>();
		
		LoginDetailsDto userDto = LoginDetailsMapper.map(this);
		
		registerEvent(new LogUserCreatedEvent(userDto, userDto));
		registerEvent(new PermissionGainEvent(this, this, userRoles));
		
		return this;
	}
	
	public String getEmail() {
		return email.getEmail();
	}
	
	@Override
	public String getUsername() {
		return username.getUsername();
	}
	
	@Override
	public String getPassword() {
		return password.getPassword();
	}

	@Override
	public boolean isAccountNonExpired() {
		return isUserAccountNonExpired;
	}

	@Override
	public boolean isAccountNonLocked() {
		return isUserNonLocked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return isUserCredentialsNonExpired;
	}

	@Override
	public boolean isEnabled() {
		return isUserEnabled;
	}
	
	public Long getId() {
		return id;
	}

}
