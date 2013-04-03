package com.sid.cloudynote.shared;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;

import junit.framework.TestCase;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.sid.cloudynote.server.PMF;

public class UserModelTest extends TestCase {
	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
			new LocalDatastoreServiceTestConfig());
	PersistenceManager pm;
	String email;

	public void setUp() {
		helper.setUp();
		pm = PMF.getInstance().getPersistenceManager();
		email = "test@example.com";
	}

	public void tearDown() {
		pm.close();
		helper.tearDown();
	}

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
	
	public void testReadUser() {
		User user = new User();
		user.setEmail(email);
		pm.makePersistent(user);
		user = pm.getObjectById(User.class, email);
		assertEquals(email, user.getEmail());
	}
	
	public void testDeleteUser() {
		
	}
}
