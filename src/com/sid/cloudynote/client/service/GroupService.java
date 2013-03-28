package com.sid.cloudynote.client.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.appengine.api.datastore.Key;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.sid.cloudynote.shared.Group;
import com.sid.cloudynote.shared.NotLoggedInException;
import com.sid.cloudynote.shared.User;

@RemoteServiceRelativePath("groupService")
public interface GroupService extends RemoteService {
	/**
	 * The service to create a new group and persist in datastore.
	 * @param name
	 * @param owner
	 * @param users
	 */
	void createGroup(String name, String owner, Set<String> users) ;
	/**
	 * The service to get groups the user is in, including BOTH the ones user owns AND the ones user is in
	 * @param userEmail
	 * @return
	 * @throws NotLoggedInException
	 */
	List<Group> getGroups(String userEmail) throws NotLoggedInException;
	
	/**
	 * The service to get ONLY groups the user owns, NOT including the ones user is in
	 * @param userEmail
	 * @return
	 * @throws NotLoggedInException
	 */
	List<Group> getMyGroups(String userEmail) throws NotLoggedInException;
	
	/**
	 * The service to get the user list in the given group
	 * @param groupKey
	 * @return
	 * @throws NotLoggedInException
	 */
	List<User> getUsersInGroup(Key key) throws NotLoggedInException;
	
	/**
	 * The service to modify an existing group
	 * @param group
	 * @throws NotLoggedInException
	 */
	void modifyGroup(Group group) throws NotLoggedInException;
	
	/**
	 * The service to delete an existing group
	 * @param group
	 * @throws NotLoggedInException
	 */
	void deleteGroup(Group group) throws NotLoggedInException;
	
	/**
	 * The service to add note access entry in a group
	 * @param groupKeys
	 * @param access
	 * @throws NotLoggedInException
	 */
	void addNoteAccessEntry(List<Key> groupKeys, Map<Key,Integer> access) throws NotLoggedInException;
}
