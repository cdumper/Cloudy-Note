package com.sid.cloudynote.client.service;

import java.util.Set;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sid.cloudynote.shared.Group;

public interface GroupServiceAsync {

	void createGroup(String name, Set<String> users, AsyncCallback<Void> callback);

	void getGroups(String userEmail, AsyncCallback<Set<Group>> callback);

}
