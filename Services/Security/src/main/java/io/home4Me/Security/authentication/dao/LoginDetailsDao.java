package io.home4Me.Security.authentication.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import io.home4Me.Security.authentication.entity.LoginDetails;

@Repository
public class LoginDetailsDao {

	@PersistenceContext
	private EntityManager em;
	
	public LoginDetails getLoginDetailsByUsername(String username) {
		return em.createNamedQuery(LoginDetails.GET_BY_USERNAME, LoginDetails.class)
				.setParameter("inputUsername", username)
				.getSingleResult();
	}

	public void save(LoginDetails loginDetails) {
		em.persist(loginDetails);
	}
	
}
