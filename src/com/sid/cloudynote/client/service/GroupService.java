package com.sid.cloudynote.client.service;

import java.util.Set;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.sid.cloudynote.shared.Group;
import com.sid.cloudynote.shared.NotLoggedInException;

@RemoteServiceRelativePath("groupService")
public interface GroupService extends RemoteService {
	void createGroup(String name, String owner, Set<String> users) ;
	/**
	 * The function to get groups the user is in, including the ones user owns
	 * @param userEmail
	 * @return
	 * @throws NotLoggedInException
	 */
	Set<Group> getGroups(String userEmail) throws NotLoggedInException;
	
	/**
	 * The function to get groups the user owns, not including the ones user is in
	 * @param userEmail
	 * @return
	 * @throws NotLoggedInException
	 */
	Set<Group> getMyGroups(String userEmail) throws NotLoggedInException;
	void modifyGroup(Group group) throws NotLoggedInException;
}
