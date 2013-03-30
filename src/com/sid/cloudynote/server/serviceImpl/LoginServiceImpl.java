package com.sid.cloudynote.server.serviceImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.sid.cloudynote.client.service.LoginService;
import com.sid.cloudynote.server.PMF;
import com.sid.cloudynote.shared.Notebook;
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
					// user does not exist. Create a new user with default notebook
					user = new User();
					user.setEmail(email);
					user.setNickname(loginInfo.getNickname().split("@")[0]);
					Map<String, Date> friends = getFakeFriendsData();
					user.setFriends(friends);
					//create a default notebook for the user
					Notebook defaultNotebook = new Notebook(user.getNickname()+"'s notebook");
					defaultNotebook.setUser(getUser());
					pm.currentTransaction().begin();
					pm.makePersistent(user);
					pm.makePersistent(defaultNotebook);
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

	private Map<String, Date> getFakeFriendsData() {
		Map<String, Date> friends = new LinkedHashMap<String, Date>();
		Date currentDate = new Date();
		for (String email : FAKE_USER_DATA) {
			friends.put(email,currentDate);
		}
		return friends;
	}

	private void createFakeUsersIfNotExist() {
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		try {
			pm.currentTransaction().begin();
			for (String email : FAKE_USER_DATA) {
				 User user = new User();
				 user.setEmail(email);
				 user.setNickname(email.split("@")[0]);
				 pm.makePersistent(user);
			}
			pm.currentTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pm.close();
		}
	}

	private com.google.appengine.api.users.User getUser() {
		UserService userService = UserServiceFactory.getUserService();
		return userService.getCurrentUser();
	}
}