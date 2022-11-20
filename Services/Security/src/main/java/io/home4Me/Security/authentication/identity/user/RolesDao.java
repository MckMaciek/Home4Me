package io.home4Me.Security.authentication.identity.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface RolesDao extends JpaRepository<UserRoles, Long> {

}
