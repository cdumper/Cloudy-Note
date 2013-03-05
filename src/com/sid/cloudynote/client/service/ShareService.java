package com.sid.cloudynote.client.service;

import java.util.Map;

import com.google.appengine.api.datastore.Key;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.sid.cloudynote.shared.InfoNote;
import com.sid.cloudynote.shared.NotLoggedInException;

@RemoteServiceRelativePath("shareService")
public interface ShareService extends RemoteService{
	void shareNoteToGroups(InfoNote note, Map<Key,Integer> groupAccess) throws NotLoggedInException;
	void shareNoteToUsers(InfoNote note, Map<String,Integer> userAccess) throws NotLoggedInException;
	void shareNoteToUsersAndGroups(InfoNote note, Map<String,Integer> userAccess, Map<Key,Integer> groupAccess) throws NotLoggedInException;
}
