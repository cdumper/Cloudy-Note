package com.sid.cloudynote.client.service;

import java.util.List;

import com.google.appengine.api.datastore.Key;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.sid.cloudynote.shared.User;

@RemoteServiceRelativePath("userService")
public interface UserService extends RemoteService{
	User getUser(String id);
	void addAccessEntry(String id,List<Key> notes, int permission);
}
