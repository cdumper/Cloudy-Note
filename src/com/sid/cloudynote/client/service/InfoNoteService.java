package com.sid.cloudynote.client.service;

import java.util.List;
import java.util.Map;

import com.google.appengine.api.datastore.Key;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.sid.cloudynote.shared.InfoNote;
import com.sid.cloudynote.shared.NotLoggedInException;
import com.sid.cloudynote.shared.Notebook;
import com.sid.cloudynote.shared.Tag;

@RemoteServiceRelativePath("noteService")
public interface InfoNoteService extends RemoteService {
	List<InfoNote> getNotes(Notebook currentNotebook)
			throws NotLoggedInException;

	InfoNote add(InfoNote entity) throws NotLoggedInException;

	InfoNote modify(InfoNote entity) throws NotLoggedInException;

	void delete(InfoNote entity) throws NotLoggedInException;

	InfoNote moveNoteTo(InfoNote note, Notebook notebook)
			throws NotLoggedInException;

	List<InfoNote> getPublicNotes() throws NotLoggedInException;

	List<InfoNote> getSharedNotes(String email) throws NotLoggedInException;

	List<InfoNote> getNotesInGroup(Key groupKey) throws NotLoggedInException;

	void makeNotesPublic(List<InfoNote> notes) throws NotLoggedInException;

	boolean verifyEditAccess(InfoNote note) throws NotLoggedInException;

	void addUserAccessEntry(List<InfoNote> notes, Map<String, Integer> access)
			throws NotLoggedInException;

	void addGroupAccessEntry(List<InfoNote> notes, Map<Key, Integer> access)
			throws NotLoggedInException;

	List<InfoNote> getNotesByTag(Tag tag) throws NotLoggedInException;

	InfoNote getNoteByKey(Key key);

}
