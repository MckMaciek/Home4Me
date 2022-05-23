package io.home4Me.Security.authentication.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import io.home4Me.Security.authentication.entity.UserRoles;

@Repository
public class RolesDao {

	@PersistenceContext
	private final EntityManager em;
	
	public RolesDao(EntityManager em) {
		this.em = em;
	}

	public List<UserRoles> findAll() {
		return em.createNamedQuery(UserRoles.GET_ALL, UserRoles.class)
		 		.getResultList();
	}
}
