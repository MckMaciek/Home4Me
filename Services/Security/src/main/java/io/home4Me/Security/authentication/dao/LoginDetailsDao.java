package io.home4Me.Security.authentication.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.home4Me.Security.authentication.entity.LoginDetails;

@Repository
public interface LoginDetailsDao extends JpaRepository<LoginDetails, Long> {

	Optional<LoginDetails> findByUsername(String inputUsername);
	boolean existsByUsernameOrEmail(String username, String email);
}
