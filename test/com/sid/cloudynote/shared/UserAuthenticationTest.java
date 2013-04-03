package com.sid.cloudynote.shared;

import junit.framework.TestCase;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;

public class UserAuthenticationTest extends TestCase {
	private final String email = "test@example.com";
	private final String domain = "Cloudy Note";
	private LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalUserServiceTestConfig())
	.setEnvEmail(email).setEnvIsLoggedIn(false).setEnvAuthDomain(domain);
	
	public void setUp() {
		helper.setUp();
	}

	public void tearDown() {
		helper.tearDown();
	}

	public void testIsLoggedIn() {
		UserService userService = UserServiceFactory.getUserService();
		assertNull(userService.getCurrentUser());
	}
	
	public void testLoggedInUser() {
		helper = new LocalServiceTestHelper(new LocalUserServiceTestConfig())
		.setEnvEmail(email).setEnvIsLoggedIn(true).setEnvAuthDomain(domain);
		helper.setUp();
		UserService userService = UserServiceFactory.getUserService();
		assertNotNull(userService.getCurrentUser());
		assertEquals(email,userService.getCurrentUser().getEmail());
	}

}
