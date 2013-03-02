package com.sid.cloudynote.server.serviceImpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.appengine.api.datastore.Key;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.sid.cloudynote.client.service.GroupService;
import com.sid.cloudynote.server.PMF;
import com.sid.cloudynote.shared.Group;
import com.sid.cloudynote.shared.User;

public class GroupServiceImpl extends RemoteServiceServlet implements GroupService{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7135759983155196260L;

	@Override
	public void createGroup(String name, Set<String> users) {
		List<User> userList = new ArrayList<User>();
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		Group group = new Group(name,users);
		pm.makePersistent(group);
		
		for ( String userEmail : users ) {
			User user = getUser(userEmail);
			if(user.getGroups() != null) user.getGroups().add(group.getKey());
			userList.add(user);
		}
		pm.makePersistentAll(userList);
		pm.close();
	}
	
	public User getUser(String email) {
		User user = null;
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		Query q = pm.newQuery(User.class);
		q.setFilter("emailAddress == emailAddressParam");
		q.declareParameters("String emailAddressParam");
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
	public Set<Group> getGroups(String userEmail) {
		Set<Group> groups = new HashSet<Group>();
		User user = null;
		Set<Key> groupKeys;
		List<User> results;
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		Query userQuery = pm.newQuery(User.class);
		userQuery.setFilter("emailAddress == emailAddressParam");
		userQuery.declareParameters("String emailAddressParam");
		userQuery.setRange(0, 1);
		Query groupQuery = pm.newQuery(Group.class);
		groupQuery.setFilter("key == keyParam");
		groupQuery.declareParameters(Key.class.getName()+" keyParam");
		try {
			//get the user with userEmail
			Object obj = userQuery.execute(userEmail);
			if (obj != null) {
				results = (List<User>) obj;
				results = new ArrayList<User>(pm.detachCopyAll(results));
				results.size();
				if (!results.isEmpty()) {
					user = results.get(0);
				}
			}
			
			if (user != null && user.getGroups()!=null ) {
				List<Group> groupList;
				groupKeys = user.getGroups();
				for (Key key : groupKeys) {
					Object o = groupQuery.execute(key);
					if (o != null ) {
						groupList = (List<Group>) o;
						groupList = new ArrayList<Group>(pm.detachCopyAll(groupList));
						groupList.size();
						if (!groupList.isEmpty()) {
							groups.add(groupList.get(0));
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			userQuery.closeAll();
			groupQuery.closeAll();
			pm.close();
		}
		return groups;
	}
}
