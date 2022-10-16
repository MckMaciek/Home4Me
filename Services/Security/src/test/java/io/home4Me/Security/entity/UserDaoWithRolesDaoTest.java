package io.home4Me.Security.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import io.home4Me.Security.RoleTypes;
import io.home4Me.Security.authentication.dao.LoginDetailsDao;
import io.home4Me.Security.authentication.dao.RolesDao;
import io.home4Me.Security.authentication.entity.LoginDetails;
import io.home4Me.Security.authentication.entity.UserRoles;
import io.home4Me.Security.authentication.services.RoleService;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
public class UserDaoWithRolesDaoTest {

	@Autowired
	private LoginDetailsDao loginDetailsDao;
	
	@Autowired
	private RolesDao roleDao;
	
	private RoleService roleService;

	private static final String USER1_EMAIL = "test@gmail.com";
	private static final String USER1_USERNAME = "testUsername";
	private static final String USER1_PASSWORD = "testPassword";
	
	private static final String USER2_EMAIL = "test2@gmail.com";
	private static final String USER2_USERNAME = "tes2tUsername";
	private static final String USER2_PASSWORD = "test2Password";
	
	private static final LocalDateTime TIME_NOW = LocalDateTime.now();
	
	private LoginDetails firstUser;
	private Set<RoleTypes> firstUserRoles = Set.of(RoleTypes.ADMIN, RoleTypes.USER, RoleTypes.LESSEE);
	private RoleTypes firstUserOverridedRole = RoleTypes.TENANT;
	
	private LoginDetails secondUser;
	private Set<RoleTypes> secondUserRoles = Set.of(RoleTypes.TENANT, RoleTypes.SUPERVISOR);
	private RoleTypes secondUserOverridedRole = RoleTypes.ADMIN;
	
	public void setUpUsers() {
		this.firstUser = LoginDetails.builder()
				.id(null)
				.email(USER1_EMAIL)
				.creationDate(TIME_NOW)
				.username(USER1_USERNAME)
				.password(USER1_PASSWORD)
				.build();
		
		this.secondUser = LoginDetails.builder()
				.id(null)
				.email(USER2_EMAIL)
				.creationDate(TIME_NOW)
				.username(USER2_USERNAME)
				.password(USER2_PASSWORD)
				.build();
	}

	@Test
	public void testCrudOperationsUsersDaoWithRolesDao() {
		
		this.roleService = new RoleService(roleDao);
		
		//GIVEN
		setUpUsers();
		
		//WHEN
		addUsersToTheDb();
		
		//THEN
		canFindTheseUsers();
		
		//THEN
		canUpdateTheseUsersRoles();
		
		//THEN
		canDeleteTheseUsers();
		
		//THEN
		rolesShouldStayAndNotBeDeleted();
	}
	
	public void rolesShouldStayAndNotBeDeleted() {
		
		List<RoleTypes> userRoles = roleDao.findAll().stream().map(UserRoles::getRole).collect(Collectors.toList());
		assertThat(userRoles).isNotEmpty();
		
		assertThat(userRoles.containsAll(firstUserRoles));
		assertThat(userRoles.containsAll(secondUserRoles));
		assertThat(userRoles.contains(firstUserOverridedRole));
		assertThat(userRoles.contains(secondUserOverridedRole));
	}

	public void canDeleteTheseUsers() {
			
		loginDetailsDao.delete(firstUser);
		loginDetailsDao.delete(secondUser);
		
		List<LoginDetails> shouldBeEmpty = loginDetailsDao.findAll();
		assertThat(shouldBeEmpty).isEmpty();
	}

	public void canUpdateTheseUsersRoles() {
		
		roleService.overrideUserRoles(firstUser, Set.of(firstUserOverridedRole));
		roleService.overrideUserRoles(secondUser, Set.of(secondUserOverridedRole));
		
		Optional<LoginDetails> overridenUser1Opt = loginDetailsDao.findByUsername(USER1_USERNAME);
		Optional<LoginDetails> overridenUser2Opt = loginDetailsDao.findByUsername(USER2_USERNAME);
		
		assertThat(overridenUser1Opt).isPresent();
		assertThat(overridenUser2Opt).isPresent();
		
		LoginDetails overridenUser1 = overridenUser1Opt.get();
		LoginDetails overridenUser2 = overridenUser2Opt.get();

		assertUserRoles(overridenUser1.getUserRoles(), Set.of(firstUserOverridedRole));
		assertUserRoles(overridenUser2.getUserRoles(), Set.of(secondUserOverridedRole));
	}

	public void canFindTheseUsers() {
		
		List<LoginDetails> allUsers = loginDetailsDao.findAll();
		assertThat(allUsers).isNotEmpty();
		assertThat(allUsers).hasSize(2);
		
		Optional<LoginDetails> foundUser1Opt = loginDetailsDao.findByUsername(USER1_USERNAME);
		Optional<LoginDetails> foundUser2Opt = loginDetailsDao.findByUsername(USER2_USERNAME);
		
		assertThat(foundUser1Opt).isPresent();
		assertThat(foundUser2Opt).isPresent();
		
		LoginDetails overridenUser1 = foundUser1Opt.get();
		LoginDetails overridenUser2 = foundUser2Opt.get();
		
		assertUsers(overridenUser1, firstUser, firstUserRoles);
		assertUsers(overridenUser2, secondUser, secondUserRoles);
		
		this.firstUser = overridenUser1;
		this.secondUser = overridenUser2;
	}
	
	public void assertUsers(LoginDetails foundUser, LoginDetails actualUser, Set<RoleTypes> actualRoles) {
		
		assertThat(actualUser.getEmail()).isEqualTo(foundUser.getEmail());
		assertThat(actualUser.getPassword()).isEqualTo(foundUser.getPassword());
		assertThat(actualUser.getCreationDate()).isEqualTo(foundUser.getCreationDate());
		assertThat(actualUser.getUsername()).isEqualTo(foundUser.getUsername());
		
		Set<UserRoles> userRoles = foundUser.getUserRoles();
		assertUserRoles(userRoles, actualRoles);
	}
	
	public void assertUserRoles(Set<UserRoles> userRoles, Set<RoleTypes> rolesGiven) {
		
		assertThat(userRoles).isNotEmpty();
		assertThat(userRoles.size()).isEqualTo(rolesGiven.size());
		userRoles.forEach(userRole -> assertThat(rolesGiven).contains(userRole.getRole()));
	}
	
	public void addUsersToTheDb() {
			
		loginDetailsDao.save(firstUser);
		roleService.addUserRoles(firstUser, firstUserRoles);
		
		loginDetailsDao.save(secondUser);
		roleService.addUserRoles(secondUser, secondUserRoles);			
	}
	
}
