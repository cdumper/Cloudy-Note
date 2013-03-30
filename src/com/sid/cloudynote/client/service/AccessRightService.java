package com.sid.cloudynote.client.service;

import java.util.Map;

import com.google.appengine.api.datastore.Key;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.sid.cloudynote.shared.Group;
import com.sid.cloudynote.shared.InfoNote;
import com.sid.cloudynote.shared.Notebook;
import com.sid.cloudynote.shared.User;

@RemoteServiceRelativePath("accessRightService")
public interface AccessRightService extends RemoteService {
	void saveGroupAndUserAccess(Group group, Map<Key, Integer> groupAccess,
			User user, Map<Key, Integer> userAccess);

	void saveNotebookAndNotePermission(Notebook notebook,
			Map<Key, Integer> notebookGroupPermission,
			Map<String, Integer> notebookUserPermission, InfoNote note,
			Map<Key, Integer> noteGroupPermission,
			Map<String, Integer> noteUserPermission);

	void saveGroupAccess(Group group, Map<Key, Integer> groupAccess);

	void saveUserAccess(User user, Map<Key, Integer> userAccess);
	
	void saveNotebookPermission(Notebook notebook,
			Map<Key, Integer> notebookGroupPermission,
			Map<String, Integer> notebookUserPermission);
	
	void saveNotePermission(InfoNote note,
			Map<Key, Integer> noteGroupPermission,
			Map<String, Integer> noteUserPermission);
}
