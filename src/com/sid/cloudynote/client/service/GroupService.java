package com.sid.cloudynote.client.service;

import java.util.Set;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.sid.cloudynote.shared.Group;

@RemoteServiceRelativePath("groupService")
public interface GroupService extends RemoteService {
	void createGroup(String name, String owner, Set<String> users);
	Set<Group> getGroups(String userEmail);
}
