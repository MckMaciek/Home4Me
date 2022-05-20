package io.home4Me.Security;

import java.util.Arrays;
import java.util.Optional;

public enum RoleTypes {
	USER(1), ADMIN(2), LESSEE(3), SUPERVISOR(4), TENANT(5);
	long id;
	
	private RoleTypes(long id) {
		this.id = id;
	}
	
	public long getId() {
        return id;
    }

    public static Optional<RoleTypes> getRoleType(Long id) {
        if (id == null)
            return null;
   
        return Arrays.asList(values())
        		.stream()
        		.filter(role -> role.getId() == id)
        		.findFirst();
    }
}
