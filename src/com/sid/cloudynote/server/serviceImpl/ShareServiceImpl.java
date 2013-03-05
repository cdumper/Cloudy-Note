package com.sid.cloudynote.server.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.sid.cloudynote.client.service.ShareService;
import com.sid.cloudynote.server.PMF;
import com.sid.cloudynote.shared.Group;
import com.sid.cloudynote.shared.InfoNote;
import com.sid.cloudynote.shared.NotLoggedInException;
import com.sid.cloudynote.shared.User;

public class ShareServiceImpl extends RemoteServiceServlet implements
		ShareService {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6939540644413407634L;

	@Override
	public void shareNoteToGroups(InfoNote note, Map<Key, Integer> groupAccess)
			throws NotLoggedInException {
		this.checkLoggedIn();
		List<Group> groups = new ArrayList<Group>();
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();

		if (!getUser().getEmail().equals(note.getUser().getEmail())) {
			GWT.log("Permission denied to add user access entry. Not owner of note:"
					+ note.getKey() + " Request from " + getUser().getEmail());
			return;
		}
		
		note.getGroupAccess().clear();
		for (Entry<Key, Integer> entry : groupAccess.entrySet()) {
			Group group = pm.getObjectById(Group.class,entry.getKey());
			group.getAccess().put(note.getKey(), entry.getValue());
			groups.add(group);
			note.getGroupAccess().put(entry.getKey(), entry.getValue());
		}
		
		try {
			pm.currentTransaction().begin();
			pm.makePersistentAll(note);
			pm.makePersistentAll(groups);
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

	@Override
	public void shareNoteToUsers(InfoNote note, Map<String, Integer> userAccess)
			throws NotLoggedInException {
		this.checkLoggedIn();
		List<User> users = new ArrayList<User>();
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();

		if (!getUser().getEmail().equals(note.getUser().getEmail())) {
			GWT.log("Permission denied to add user access entry. Not owner of note:"
					+ note.getKey() + " Request from " + getUser().getEmail());
			return;
		}
		
		note.getUserAccess().clear();
		for (Entry<String, Integer> entry : userAccess.entrySet()) {
			User user = this.getUser(entry.getKey());
			user.getAccess().put(note.getKey(), entry.getValue());
			users.add(user);
			note.getUserAccess().put(entry.getKey(), entry.getValue());
		}
		
		try {
			pm.currentTransaction().begin();
			pm.makePersistentAll(note);
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

	@Override
	public void shareNoteToUsersAndGroups(InfoNote note,
			Map<String, Integer> userAccess, Map<Key, Integer> groupAccess)
					throws NotLoggedInException {
		this.checkLoggedIn();
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		List<User> users = new ArrayList<User>();
		List<Group> groups = new ArrayList<Group>();

		if (!getUser().getEmail().equals(note.getUser().getEmail())) {
			GWT.log("Permission denied to add user access entry. Not owner of note:"
					+ note.getKey() + " Request from " + getUser().getEmail());
			return;
		}
		
		note.getUserAccess().clear();
		for (Entry<String, Integer> entry : userAccess.entrySet()) {
			User user = this.getUser(entry.getKey());
			user.getAccess().put(note.getKey(), entry.getValue());
			users.add(user);
			note.getUserAccess().put(entry.getKey(), entry.getValue());
		}
		
		note.getGroupAccess().clear();
		for (Entry<Key, Integer> entry : groupAccess.entrySet()) {
			Group group = pm.getObjectById(Group.class,entry.getKey());
			group.getAccess().put(note.getKey(), entry.getValue());
			groups.add(group);
			note.getGroupAccess().put(entry.getKey(), entry.getValue());
		}
		
		try {
			pm.currentTransaction().begin();
			pm.makePersistentAll(note);
			pm.makePersistentAll(users);
			pm.makePersistentAll(groups);
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
	
	private void checkLoggedIn() throws NotLoggedInException {
		if (getUser() == null) {
			throw new NotLoggedInException("Not logged in.");
		}
	}

	private com.google.appengine.api.users.User getUser() {
		UserService userService = UserServiceFactory.getUserService();
		return userService.getCurrentUser();
	}

}
