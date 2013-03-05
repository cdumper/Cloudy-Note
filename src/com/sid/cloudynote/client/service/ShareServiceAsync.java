package com.sid.cloudynote.client.service;

import java.util.Map;

import com.google.appengine.api.datastore.Key;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sid.cloudynote.shared.InfoNote;

public interface ShareServiceAsync {

	void shareNoteToGroups(InfoNote note, Map<Key, Integer> groupAccess,
			AsyncCallback<Void> callback);

	void shareNoteToUsers(InfoNote note, Map<String, Integer> userAccess,
			AsyncCallback<Void> callback);

	void shareNoteToUsersAndGroups(InfoNote note,
			Map<String, Integer> userAccess, Map<Key, Integer> groupAccess,
			AsyncCallback<Void> callback);

}
