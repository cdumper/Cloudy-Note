package com.sid.cloudynote.client.service;

import java.util.List;
import java.util.Map;

import com.google.appengine.api.datastore.Key;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.sid.cloudynote.shared.NotLoggedInException;
import com.sid.cloudynote.shared.User;

@RemoteServiceRelativePath("userService")
public interface UserService extends RemoteService{
	User getUser(String email);
	void modifyUser(User user) throws NotLoggedInException;
	List<User> getFriends(String email) throws NotLoggedInException;
	void addAccessEntry(List<String> emails, Map<Key,Integer> access) throws NotLoggedInException;
	String addFriend(String email) throws NotLoggedInException;
	void inviteUser(String email) throws NotLoggedInException;
	void addUserToGroups(String email, List<Key> groups) throws NotLoggedInException;
	String getUserProfile(String email) throws NotLoggedInException;
}
