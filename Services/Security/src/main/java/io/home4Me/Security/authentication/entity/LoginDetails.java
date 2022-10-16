package io.home4Me.Security.authentication.entity;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.springframework.data.domain.AbstractAggregateRoot;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import io.home4Me.Security.RoleTypes;
import io.home4Me.Security.authentication.events.LogUserCreatedEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"password"})
@EqualsAndHashCode(callSuper = false, of = {"username", "password", "email", "creationDate", "userRoles"})
@NamedQueries({
	@NamedQuery(name = LoginDetails.FIND_BY_USERNAME, query = "from LoginDetails ld where username = :inputUsername"),
})
@SuppressWarnings("serial")
@Table(name = "login_details", schema = "public")
public class LoginDetails extends AbstractAggregateRoot<LoginDetails> implements UserDetails {

	public static final String FIND_BY_USERNAME = "LoginDetails.findByUsername";
	private static final String ROLE_PREFIX = "ROLE_";
	
	public static final boolean IS_ENABLED_BY_DEFAULT = true;
	public static final boolean IS_NON_LOCK_BY_DEFAULT = true;
	public static final boolean IS_NON_EXPIRED_BY_DEFAULT = true;
	public static final boolean IS_CREDS_NON_EXPIRED_BY_DEFAULT = true;
		
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@NotBlank
	@Size(min=5, max=16)
	private String username;

	@NotBlank
	@Min(value = 10)
	private String password;
	
	@Email
	private String email;
	
	@Generated(GenerationTime.INSERT)
	private LocalDateTime creationDate;
	
	@Builder.Default
	private boolean isUserEnabled = IS_ENABLED_BY_DEFAULT;
	@Builder.Default
	private boolean isUserNonLocked = IS_NON_LOCK_BY_DEFAULT;
	@Builder.Default
	private boolean isUserAccountNonExpired = IS_NON_EXPIRED_BY_DEFAULT;
	@Builder.Default
	private boolean isUserCredentialsNonExpired = IS_CREDS_NON_EXPIRED_BY_DEFAULT;
	
	@Builder.Default
	@OneToMany
	@JoinTable(joinColumns = { @JoinColumn(name = "login_details_id", table = "login_details_roles")},
				inverseJoinColumns = { @JoinColumn(name = "roles_id", table = "login_details_roles")}
			)
	private Set<UserRoles> userRoles = new HashSet<>();
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return userRoles.stream()
			 .map(role -> new SimpleGrantedAuthority(ROLE_PREFIX + role))
			 .collect(Collectors.toList());
	}
	
	public LoginDetails buildNewUser(Set<RoleTypes> userRoles) {
		this.id = null;
		
		registerEvent(new RolesAssigneEvent(this, this, userRoles));
		registerEvent(new LogUserCreatedEvent(this, this));
		
		return this;
	}
	
	@Override
	public String getUsername() {
		return username;
	}
	
	@Override
	public String getPassword() {
		return password;
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

	public void setId(Long id) {
		this.id = id;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setCreationDate(LocalDateTime creationDate) {
		this.creationDate = creationDate;
	}

	public void setUserEnabled(boolean isUserEnabled) {
		this.isUserEnabled = isUserEnabled;
	}

	public void setUserNonLocked(boolean isUserNonLocked) {
		this.isUserNonLocked = isUserNonLocked;
	}

	public void setUserAccountNonExpired(boolean isUserAccountNonExpired) {
		this.isUserAccountNonExpired = isUserAccountNonExpired;
	}

	public void setUserCredentialsNonExpired(boolean isUserCredentialsNonExpired) {
		this.isUserCredentialsNonExpired = isUserCredentialsNonExpired;
	}

	public void setUserRoles(Set<UserRoles> userRoles) {
		this.userRoles = userRoles;
	}
}
