package io.home4Me.Security.authentication.entity;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import io.home4Me.Security.RoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginDetails implements UserDetails {

	private static final String ROLE_PREFIX = "ROLE_";
	
	public static final boolean IS_ENABLED_BY_DEFAULT = true;
	public static final boolean IS_NON_LOCK_BY_DEFAULT = true;
	public static final boolean IS_NON_EXPIRED_BY_DEFAULT = true;
	public static final boolean IS_CREDS_NON_EXPIRED_BY_DEFAULT = true;
	
	@Id
    private Long id;
	
	@NotNull
	@Size(min=5, max=16)
	private String username;

	@NotNull
	@Min(value = 10)
	private String password;
	
	@NotNull
	@Email
	private String email;
	
	@NotNull
	private LocalDateTime creationDate;
	
	@Builder.Default
	private boolean isUserEnabled = IS_ENABLED_BY_DEFAULT;
	@Builder.Default
	private boolean isUserNonLocked = IS_NON_LOCK_BY_DEFAULT;
	@Builder.Default
	private boolean isUserAccountNonExpired = IS_NON_EXPIRED_BY_DEFAULT;
	@Builder.Default
	private boolean isUserCredentialsNonExpired = IS_CREDS_NON_EXPIRED_BY_DEFAULT;
	
	@ElementCollection(fetch = FetchType.LAZY, targetClass = RoleType.class)
    @Enumerated(EnumType.STRING)
	@CollectionTable
	private Set<RoleType> roles;
		
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return roles.stream()
			 .map(role -> new SimpleGrantedAuthority(ROLE_PREFIX + role))
			 .collect(Collectors.toList());
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
	
	public static LoginDetails.LoginDetailsBuilder getBasicUserBuilderInstance() {
		return LoginDetails.builder()
						.roles(Set.of(RoleType.USER))
						.creationDate(LocalDateTime.now());
	}
}
