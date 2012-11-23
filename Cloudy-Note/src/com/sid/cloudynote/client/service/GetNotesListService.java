package com.sid.cloudynote.client.service;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.sid.cloudynote.client.model.Note;

@RemoteServiceRelativePath("getNotesList")
public interface GetNotesListService extends RemoteService{
	List<Note> getNoteList();
}
