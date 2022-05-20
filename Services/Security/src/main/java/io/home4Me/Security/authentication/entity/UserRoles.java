package io.home4Me.Security.authentication.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import io.home4Me.Security.RoleTypes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@NamedQueries({
	@NamedQuery(name = UserRoles.GET_ALL, query = "from UserRoles ur")
})
@Entity
@Table(name = "user_roles", schema = "public")
public class UserRoles {
	
	public static final String GET_ALL = "UserRoles.GET_ALL";
	
	@Id
    private Long id;
	
	@Enumerated(EnumType.STRING)
	private RoleTypes role;
}
