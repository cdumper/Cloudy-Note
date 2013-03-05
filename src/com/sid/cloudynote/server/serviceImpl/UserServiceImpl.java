package com.sid.cloudynote.server.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.appengine.api.datastore.Key;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.sid.cloudynote.client.service.UserService;
import com.sid.cloudynote.server.PMF;
import com.sid.cloudynote.shared.User;

public class UserServiceImpl extends RemoteServiceServlet implements
		UserService {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7824317844070590676L;

	@Override
	@SuppressWarnings("unchecked")
	public User getUser(String email) {
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

	@Override
	public void addAccessEntry(final List<String> emails,final Map<Key,Integer> access) {
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		List<User> users = new ArrayList<User>();
		for (String email : emails) {
			users.add(this.getUser(email));
		}
		if (users != null && users.size() != 0) {
			for (User user : users) {
				for (Entry<Key,Integer> entry : access.entrySet()) {
					user.getAccess().put(entry.getKey(), entry.getValue());
				}
			}
			try {
				pm.currentTransaction().begin();
				pm.makePersistentAll(users);
				pm.currentTransaction().commit();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (pm.currentTransaction().isActive()) {
					pm.currentTransaction().rollback();
				}
				pm.close();
			}
		}
	}

	@Override
	public List<User> getFriends(String email) {
		List<User> friends = new ArrayList<User>();
		User user = getUser(email);
		if (user.getFriends() != null) {
			Set<String> friendsKeys = user.getFriends();
			for (String key : friendsKeys) {
				User friend = getUser(key);
				friends.add(friend);
			}
		}
		return friends;
	}
}
