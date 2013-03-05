package com.sid.cloudynote.server.serviceImpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.sid.cloudynote.client.service.LoginService;
import com.sid.cloudynote.server.PMF;
import com.sid.cloudynote.shared.User;

public class LoginServiceImpl extends RemoteServiceServlet implements
		LoginService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2751853380762343814L;
	private static final String[] FAKE_USER_DATA = { "jackie@jiang.com",
			"chris@xue.com", "elena@chen.com", "shane@sheng.com" };
	private com.google.appengine.api.users.User loginInfo;

	/**
	 * check if the user exist. If yes get the user from datastore and set
	 * logged in If not create and return a new user
	 */

	public User login(String requestUri) {
		UserService userService = UserServiceFactory.getUserService();
		this.loginInfo = userService.getCurrentUser();

		User user;
		if (loginInfo != null) {
			this.createFakeUsersIfNotExist();
			user = getUser(loginInfo.getEmail());
			user.setLoggedIn(true);
			user.setLogoutUrl(userService.createLogoutURL(requestUri));
		} else {
			user = new User();
			user.setLoggedIn(false);
			user.setLoginUrl(userService.createLoginURL(requestUri));
		}
		return user;
	}

	@SuppressWarnings("unchecked")
	private User getUser(String email) {
		User user = null;
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		Query q = pm.newQuery(User.class);
		q.setFilter("email == emailParam");
		q.declareParameters("String emailParam");
		q.setRange(0, 1);
		List<User> results;
		try {
			Object obj = q.execute(email);
			if (obj != null) {
				results = (List<User>) obj;
				results = new ArrayList<User>(pm.detachCopyAll(results));
				results.size();
				if (!results.isEmpty()) {
					user = results.get(0);
				} else {
					user = new User();
					user.setEmail(email);
					// user.setId(loginInfo.getUserId());
					user.setNickname(loginInfo.getNickname());
					Set<String> friends = getFakeFriendsData();
					user.setFriends(friends);
					pm.currentTransaction().begin();
					pm.makePersistent(user);
					user = pm.detachCopy(user);
					pm.currentTransaction().commit();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			q.closeAll();
			pm.close();
		}
		return user;
	}

	private Set<String> getFakeFriendsData() {
		Set<String> friends = new HashSet<String>();
		for (String email : FAKE_USER_DATA) {
			friends.add(email);
		}
		return friends;
	}

	private void createFakeUsersIfNotExist() {
//		List<User> users = new ArrayList<User>();
//		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		for (String email : FAKE_USER_DATA) {
			// User user = new User();
			// user.setEmailAddress(email);
			// users.add(user);
			this.getUser(email);
		}
//		try {
//			pm.currentTransaction().begin();
//			pm.makePersistentAll(users);
//			pm.currentTransaction().commit();
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			pm.close();
//		}
//		pm.close();
	}
}