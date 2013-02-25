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
import com.sid.cloudynote.shared.User;

public class LoginServiceImpl extends RemoteServiceServlet implements
		LoginService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2751853380762343814L;

	/**
	 * check if the user exist. If yes get the user from datastore and set logged in
	 * If not create and return a new user
	 */
	public User login(String requestUri) {
		UserService userService = UserServiceFactory.getUserService();
		com.google.appengine.api.users.User loginInfo = userService.getCurrentUser();
		User user;
		if (loginInfo != null) {
			user = getUser(loginInfo);
			user.setLoggedIn(true);
			user.setLogoutUrl(userService.createLogoutURL(requestUri));
		} else {
			user = new User();
			user.setLoggedIn(false);
			user.setLoginUrl(userService.createLoginURL(requestUri));
		}
		return user;
	}

	private User getUser(com.google.appengine.api.users.User loginInfo) {
		User user = null;
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		Query q = pm.newQuery(User.class);
		q.setFilter("id == idParam");
		q.declareParameters("String idParam");
		q.setRange(0,1);
		List<User> results;
		try {
			Object obj = q.execute(loginInfo.getUserId());
			if (obj!=null) {
				results = (List<User>) obj;
				results = new ArrayList<User>(pm.detachCopyAll(results));
				results.size();
				if (!results.isEmpty()) {
					user = results.get(0);
				} else {
					user = new User();
					user.setEmailAddress(loginInfo.getEmail());
					user.setId(loginInfo.getUserId());
					user.setNickname(loginInfo.getNickname());
					pm.currentTransaction().begin();
					pm.makePersistent(user);
					user=pm.detachCopy(user);
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
}