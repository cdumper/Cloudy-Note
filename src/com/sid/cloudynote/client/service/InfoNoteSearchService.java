package com.sid.cloudynote.client.service;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.sid.cloudynote.shared.InfoNote;
import com.sid.cloudynote.shared.NotLoggedInException;

@RemoteServiceRelativePath("noteSearchService")
public interface InfoNoteSearchService extends RemoteService{
	List<InfoNote> searchNotes(String text) throws NotLoggedInException;
}
