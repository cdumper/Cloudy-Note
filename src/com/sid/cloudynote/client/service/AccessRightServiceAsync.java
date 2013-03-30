package com.sid.cloudynote.client.service;

import java.util.Map;

import com.google.appengine.api.datastore.Key;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sid.cloudynote.shared.Group;
import com.sid.cloudynote.shared.InfoNote;
import com.sid.cloudynote.shared.Notebook;
import com.sid.cloudynote.shared.User;

public interface AccessRightServiceAsync {

	void saveGroupAndUserAccess(Group group, Map<Key, Integer> groupAccess,
			User user, Map<Key, Integer> userAccess,
			AsyncCallback<Void> callback);

	void saveNotebookAndNotePermission(Notebook notebook,
			Map<Key, Integer> notebookGroupPermission,
			Map<String, Integer> notebookUserPermission, InfoNote note,
			Map<Key, Integer> noteGroupPermission,
			Map<String, Integer> noteUserPermission,
			AsyncCallback<Void> callback);

	void saveGroupAccess(Group group, Map<Key, Integer> groupAccess,
			AsyncCallback<Void> callback);

	void saveUserAccess(User user, Map<Key, Integer> userAccess,
			AsyncCallback<Void> callback);

	void saveNotebookPermission(Notebook notebook,
			Map<Key, Integer> notebookGroupPermission,
			Map<String, Integer> notebookUserPermission,
			AsyncCallback<Void> callback);

	void saveNotePermission(InfoNote note,
			Map<Key, Integer> noteGroupPermission,
			Map<String, Integer> noteUserPermission,
			AsyncCallback<Void> callback);

}
