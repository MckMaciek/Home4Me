package io.home4Me.Security.authentication.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.home4Me.Security.authentication.entity.UserRoles;

@Repository
public interface RolesDao extends JpaRepository<UserRoles, Long> {

}
