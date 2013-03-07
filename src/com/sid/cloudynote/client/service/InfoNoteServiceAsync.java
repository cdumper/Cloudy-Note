package com.sid.cloudynote.client.service;

import java.util.List;
import java.util.Map;

import com.google.appengine.api.datastore.Key;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sid.cloudynote.shared.InfoNote;
import com.sid.cloudynote.shared.Notebook;
import com.sid.cloudynote.shared.Tag;

public interface InfoNoteServiceAsync {

	void add(InfoNote entity, AsyncCallback<Void> callback);

	void delete(InfoNote entity, AsyncCallback<Void> callback);

	void getPaginationData(String filter, String ordering, long firstResult,
			long maxResult, AsyncCallback<List<InfoNote>> callback);

	void getPaginationData(String filter, String ordering,
			AsyncCallback<List<InfoNote>> callback);

	void getPaginationData(String filter, long firstResult, long maxResult,
			AsyncCallback<List<InfoNote>> callback);

	void getPaginationData(long firstResult, long maxResult,
			AsyncCallback<List<InfoNote>> callback);

	void getPaginationData(String filter, AsyncCallback<List<InfoNote>> callback);

	void getPaginationData(AsyncCallback<List<InfoNote>> callback);

	void modify(InfoNote entity, AsyncCallback<Void> callback);

	void getNotes(Notebook currentNotebook,
			AsyncCallback<List<InfoNote>> callback);

	void moveNoteTo(InfoNote note, Notebook notebook,
			AsyncCallback<Void> callback);

	void getPublicNotes(AsyncCallback<List<InfoNote>> callback);

	void getSharedNotes(String email, AsyncCallback<List<InfoNote>> callback);

	void makeNotesPublic(List<InfoNote> notes, AsyncCallback<Void> callback);

	void verifyEditAccess(InfoNote note, AsyncCallback<Boolean> callback);

	void addUserAccessEntry(List<InfoNote> notes, Map<String, Integer> access,
			AsyncCallback<Void> callback);

	void addGroupAccessEntry(List<InfoNote> notes, Map<Key, Integer> access,
			AsyncCallback<Void> callback);

	void getNotesInGroup(Key groupKey, AsyncCallback<List<InfoNote>> callback);

	void getNotesByTag(Tag tag, AsyncCallback<List<InfoNote>> callback);

}
