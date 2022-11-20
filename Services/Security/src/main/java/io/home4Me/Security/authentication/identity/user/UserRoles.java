package io.home4Me.Security.authentication.identity.user;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import io.home4Me.Security.RoleTypes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"id", "role"})
@Entity
@Table(name = "user_roles", schema = "public")
class UserRoles {
	
	@Id
    private Long id;
	
	@Enumerated(EnumType.STRING)
	private RoleTypes role;
}
