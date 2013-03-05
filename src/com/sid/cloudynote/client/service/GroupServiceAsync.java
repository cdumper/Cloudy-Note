package com.sid.cloudynote.client.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.appengine.api.datastore.Key;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sid.cloudynote.shared.Group;
import com.sid.cloudynote.shared.User;

public interface GroupServiceAsync {

	void createGroup(String name, String owner, Set<String> users,
			AsyncCallback<Void> callback);

	void getGroups(String userEmail, AsyncCallback<Set<Group>> callback);

	void modifyGroup(Group group, AsyncCallback<Void> callback);

	void getMyGroups(String userEmail, AsyncCallback<Set<Group>> callback);

	void getUsersInGroup(Key key, AsyncCallback<List<User>> callback);

	void deleteGroup(Group group, AsyncCallback<Void> callback);

	void addNoteAccessEntry(List<Key> groupKeys, Map<Key, Integer> access,
			AsyncCallback<Void> callback);

}
