package io.home4Me.Security.authentication.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import io.home4Me.Security.authentication.entity.LoginDetails;

@Repository
public class LoginDetailsDao {

	@PersistenceContext
	private final EntityManager em;
	
	public LoginDetailsDao(EntityManager em) {
		this.em = em;
	}
	
	public LoginDetails getLoginDetailsByUsername(String username) {
		return em.createNamedQuery(LoginDetails.GET_BY_USERNAME, LoginDetails.class)
				.setParameter("inputUsername", username)
				.getSingleResult();
	}
	
	public List<LoginDetails> findAll(){
		return em.createNamedQuery(LoginDetails.GET_ALL, LoginDetails.class)
				.getResultList();
	}

	public void save(LoginDetails loginDetails) {
		em.persist(loginDetails);
	}
	
}
