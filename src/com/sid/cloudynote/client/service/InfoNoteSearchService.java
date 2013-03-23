package com.sid.cloudynote.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("noteSearchService")
public interface InfoNoteSearchService extends RemoteService{
	void searchNotes(String text);
}
