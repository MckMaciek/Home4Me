package io.home4Me.Security.authentication.identity.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface LoginDetailsDao extends JpaRepository<LoginDetails, Long> {

	Optional<LoginDetails> findByUsername(String inputUsername);
	boolean existsByUsernameOrEmail(String username, String email);
}
