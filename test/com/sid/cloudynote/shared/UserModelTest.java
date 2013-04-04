package com.sid.cloudynote.shared;

import static org.junit.Assert.*;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.sid.cloudynote.server.PMF;

public class UserModelTest {
	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
			new LocalDatastoreServiceTestConfig());
	PersistenceManager pm;
	String email;

	@Before
	public void setUp() {
		helper.setUp();
		pm = PMF.getInstance().getPersistenceManager();
		email = "test@example.com";
	}

	@After
	public void tearDown() {
		pm.close();
		helper.tearDown();
	}
	
	@Test
	public void testCreateNewUser() {
		User user = new User();
		user.setEmail(email);

		try {
			pm.getObjectById(User.class, email);
			fail("should have raised not found");
		} catch (JDOObjectNotFoundException e) {
			assertTrue(true);
		}

		pm.makePersistent(user);

		user = pm.getObjectById(User.class, email);
		assertNotNull(user);
	}

	@Test
	public void testReadUser() {
		User user = new User();
		user.setEmail(email);
		pm.makePersistent(user);
		user = pm.getObjectById(User.class, email);
		assertEquals(email, user.getEmail());
	}

	@Test
	public void testDeleteUser() {

	}

	@Test
	public void testUserConstructor() {
		
	}
}
