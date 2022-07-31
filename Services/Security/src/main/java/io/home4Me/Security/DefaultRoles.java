package io.home4Me.Security;

import java.util.EnumSet;
import java.util.Set;

public class DefaultRoles {

	public static final Set<RoleTypes> DEFAULT_USER = EnumSet.of(RoleTypes.USER);
	public static final Set<RoleTypes> DEFAULT_ADMIN = EnumSet.of(
			RoleTypes.ADMIN, RoleTypes.LESSEE, RoleTypes.SUPERVISOR, RoleTypes.TENANT, RoleTypes.USER);
	
}
