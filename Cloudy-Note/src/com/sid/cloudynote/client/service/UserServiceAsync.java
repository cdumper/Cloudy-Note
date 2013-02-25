package com.sid.cloudynote.client.service;

import java.util.List;

import com.google.appengine.api.datastore.Key;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sid.cloudynote.shared.User;

public interface UserServiceAsync {
	void getUser(String id, AsyncCallback<User> callback);

	void addAccessEntry(String id, List<Key> notes, int permission,
			AsyncCallback<Void> callback);
}
