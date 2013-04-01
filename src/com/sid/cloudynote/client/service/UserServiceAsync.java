package com.sid.cloudynote.client.service;

import java.util.List;
import java.util.Map;

import com.google.appengine.api.datastore.Key;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sid.cloudynote.shared.User;

public interface UserServiceAsync {
	void getUser(String email, AsyncCallback<User> callback);

	void addAccessEntry(List<String> emails, Map<Key, Integer> access,
			AsyncCallback<Void> callback);

	void getFriends(String email, AsyncCallback<List<User>> callback);

	void addFriend(String email, AsyncCallback<String> callback);

	void inviteUser(String email, AsyncCallback<Void> callback);

	void addUserToGroups(String email, List<Key> groups,
			AsyncCallback<Void> callback);

	void modifyUser(User user, AsyncCallback<User> callback);

	void modifyUserProfileImage(User user, AsyncCallback<User> asyncCallback);

}
