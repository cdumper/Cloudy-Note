package com.sid.cloudynote.server.serviceImpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.sid.cloudynote.client.service.GroupService;
import com.sid.cloudynote.server.PMF;
import com.sid.cloudynote.shared.Group;
import com.sid.cloudynote.shared.NotLoggedInException;
import com.sid.cloudynote.shared.User;
import com.sid.cloudynote.shared.Visibility;

public class GroupServiceImpl extends RemoteServiceServlet implements
		GroupService {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7135759983155196260L;

	@Override
	public void createGroup(String name, String owner, Set<String> users) {
		List<User> userList = new ArrayList<User>();
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		// create the group with given name and members
		Group group = new Group(name, owner, users);
		// TODO add group privacy support
		// set the visibility of the group to PRIVATE by default
		group.setVisibility(Visibility.PRIVATE);
		pm.makePersistent(group);

		// register the created group in each member's group list
		for (String userEmail : users) {
			User user = getUser(userEmail);
			if (user.getGroups() != null)
				user.getGroups().add(group.getKey());
			userList.add(user);
		}
		pm.makePersistentAll(userList);
		pm.close();
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

	@Override
	@SuppressWarnings("unchecked")
	public List<Group> getGroups(String userEmail) throws NotLoggedInException {
		checkLoggedIn();
		User user = getUser(userEmail);
		List<Group> groups = new ArrayList<Group>();
		Set<Key> groupKeys;
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		Query groupQuery = pm.newQuery(Group.class);
		groupQuery.setFilter("key == keyParam");
		groupQuery.declareParameters(Key.class.getName() + " keyParam");
		groupQuery.setOrdering("name asc");
		try {
			if (user != null && user.getGroups() != null) {
				groupKeys = user.getGroups();
				for (Key key : groupKeys) {
					Object o = groupQuery.execute(key);
					if (o != null) {
						groups = (List<Group>) o;
						groups = new ArrayList<Group>(
								pm.detachCopyAll(groups));
						groups.size();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			groupQuery.closeAll();
			pm.close();
		}
		
		for(Group group : getMyGroups(userEmail)) {
			boolean exist = false;
			for (Group g : groups) {
				if (g.getKey()==group.getKey()) {
					exist = true;
				}
			}
			if(!exist) groups.add(group);
		}
		return groups;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Group> getMyGroups(String userEmail) throws NotLoggedInException {
		checkLoggedIn();
		List<Group> groups = new ArrayList<Group>();
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		Query groupQuery = pm.newQuery(Group.class);
		groupQuery.setFilter("owner == ownerParam");
		groupQuery.declareParameters("String ownerParam");
		groupQuery.setOrdering("name asc");
		try {
			Object o = groupQuery.execute(userEmail);
			if (o != null) {
				groups = (List<Group>) o;
				groups = new ArrayList<Group>(pm.detachCopyAll(groups));
				groups.size();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			groupQuery.closeAll();
			pm.close();
		}
		return groups;
	}

	@Override
	public void modifyGroup(Group group) throws NotLoggedInException {
		checkLoggedIn();
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		try {
			pm.currentTransaction().begin();
			if (!group.getOwner().equals(getUser().getEmail())) {
				GWT.log("You don't have the access to delete since you're not the ower of the group");
			} else {
				Group unchangedGroup = pm.getObjectById(Group.class,group.getKey());
				Set<String> originMembers = unchangedGroup.getMembers();
				Set<String> newMembers = group.getMembers();
				Set<String> temp = new HashSet<String>();
				temp.addAll(originMembers);
				
				//get the users have been removed
				temp.removeAll(newMembers);
				for(String userEmail : temp) {
					User user = getUser(userEmail);
					user.getGroups().remove(group.getKey());
					pm.makePersistent(user);
				}
				
				//get the newly added user
				temp.clear();
				temp.addAll(newMembers);
				temp.removeAll(originMembers);
				for(String userEmail : temp) {
					User user = getUser(userEmail);
					user.getGroups().add(group.getKey());
					pm.makePersistent(user);
				}
				
				pm.makePersistent(group);
			}
			pm.currentTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (pm.currentTransaction().isActive())
				pm.currentTransaction().rollback();
			pm.close();
		}
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

	@Override
	@SuppressWarnings("unchecked")
	public List<User> getUsersInGroup(Key key) throws NotLoggedInException {
		checkLoggedIn();
		Group group;
		List<User> users = new ArrayList<User>();
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		Query q = pm.newQuery(Group.class);
		q.setFilter("key == keyParam");
		q.declareParameters(Key.class.getName() + " keyParam");
		q.setRange(0, 1);
		try {
			Object o = q.execute(key);
			if (o != null) {
				List<Group> groups = (List<Group>) o;
				groups = new ArrayList<Group>(pm.detachCopyAll(groups));
				groups.size();
				group = groups.get(0);
				for (String email : group.getMembers()) {
					User user = this.getUser(email);
					users.add(user);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pm.close();
		}
		return users;
	}

	@Override
	// TODO At the current stage, only delete the group entity. May need to
	// implement reference delete in
	// the future. I.E. when group is deleted, also delete the group from all
	// its members' group list
	public void deleteGroup(Group group) throws NotLoggedInException {
		checkLoggedIn();
		if (!getUser().getEmail().equals(group.getOwner())) {
			GWT.log("You don't have the access to delete this group. Only the owner of groups has the delete access!");
			return;
		}
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		try {
			for(String email : group.getMembers()){
				User user = getUser(email);
				user.getGroups().remove(group.getKey());
				pm.makePersistent(user);
			}
			pm.deletePersistent(group);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pm.close();
		}
	}

	@Override
	public void addNoteAccessEntry(List<Key> groupKeys, Map<Key,Integer> access) throws NotLoggedInException {
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		List<Group> groups = new ArrayList<Group>();
		for (Key key : groupKeys) {
			Group group = pm.getObjectById(Group.class, key);
			groups.add(group);
			for (Entry<Key,Integer> entry : access.entrySet()) {
				group.getAccess().put(entry.getKey(), entry.getValue());
			}
		}
		
		if (groups != null && groups.size() != 0) {
			try {
				pm.currentTransaction().begin();
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
	}
}
