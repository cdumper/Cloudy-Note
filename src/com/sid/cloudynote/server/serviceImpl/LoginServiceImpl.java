package com.sid.cloudynote.server.serviceImpl;

import java.util.ArrayList;
import java.util.List;

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
			user = getUser(loginInfo.getEmail());
			if (user == null) {
				user = createUser(getUser().getEmail());
			}
			user.setLoggedIn(true);
			user.setLogoutUrl(userService.createLogoutURL(requestUri));
		} else {
			user = new User();
			user.setLoggedIn(false);
			user.setLoginUrl(userService.createLoginURL(requestUri));
		}
		return user;
	}

	private User createUser(String email) {
		User user = new User();
		user.setEmail(email);
		user.setNickname(email.split("@")[0]);
		// create a default notebook for the user
		Notebook defaultNotebook = new Notebook(user.getNickname()
				+ "'s notebook");
		defaultNotebook.setUser(getUser());
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		try {
			pm.currentTransaction().begin();
			pm.makePersistent(user);
			pm.makePersistent(defaultNotebook);
			user = pm.detachCopy(user);
			pm.currentTransaction().commit();
		} catch (Exception e) {

		} finally {
			pm.close();
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

	private com.google.appengine.api.users.User getUser() {
		UserService userService = UserServiceFactory.getUserService();
		return userService.getCurrentUser();
	}
}