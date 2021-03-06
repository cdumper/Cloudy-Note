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
	User modifyUser(User user) throws NotLoggedInException;
	List<User> getFriends(String email) throws NotLoggedInException;
	void addAccessEntry(List<String> emails, Map<Key,Integer> access) throws NotLoggedInException;
	User addFriend(String email) throws NotLoggedInException;
	void inviteUser(String email) throws NotLoggedInException;
	void addUserToGroups(String email, List<Key> groups) throws NotLoggedInException;
	User modifyUserProfileImage(User user);
	List<User> findUser(String text, int i);
}
