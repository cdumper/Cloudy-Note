package com.sid.cloudynote.client.service;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.sid.cloudynote.client.IDAO;
import com.sid.cloudynote.shared.InfoNote;
import com.sid.cloudynote.shared.NotLoggedInException;
import com.sid.cloudynote.shared.Notebook;

@RemoteServiceRelativePath("noteService")
public interface InfoNoteService extends RemoteService, IDAO<InfoNote>{
	List<InfoNote> getNotes(Notebook currentNotebook) throws NotLoggedInException;
	void moveNoteTo(InfoNote note, Notebook notebook) throws NotLoggedInException;
	List<InfoNote> getPublicNotes() throws NotLoggedInException;
	List<InfoNote> getSharedNotes(String email) throws NotLoggedInException;
	void makeNotesPublic(List<InfoNote> notes) throws NotLoggedInException;
	boolean verifyEditAccess(InfoNote note) throws NotLoggedInException;
	void addAccessEntry(InfoNote note, List<String> users, int permission) throws NotLoggedInException;
}
