package io.home4Me.Security.Entity;

import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.Transactional;

@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest
@ActiveProfiles("test")
public class UserRolesDbUnitTest {

	@Autowired
	private EntityManager em;
	
	public void executeInContext(Runnable action) {
		
		em.getTransaction().begin();
		action.run();
		
		em.flush();
        em.clear();
	}
	
	@Test
	public void contextLoads() {
		assertThat(1).isEqualTo(1);
	}
	
	
}
