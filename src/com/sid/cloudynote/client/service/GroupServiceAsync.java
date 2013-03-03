package com.sid.cloudynote.client.service;

import java.util.Set;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sid.cloudynote.shared.Group;

public interface GroupServiceAsync {

	void createGroup(String name, String owner, Set<String> users,
			AsyncCallback<Void> callback);

	void getGroups(String userEmail, AsyncCallback<Set<Group>> callback);

	void modifyGroup(Group group, AsyncCallback<Void> callback);

	void getMyGroups(String userEmail, AsyncCallback<Set<Group>> callback);

}
