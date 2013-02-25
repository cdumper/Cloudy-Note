package com.sid.cloudynote.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sid.cloudynote.shared.InfoNote;

public interface AddNoteServiceAsync {

	void addNote(String title, String content, AsyncCallback<Void> callback);
	
	void addInfoNote(InfoNote note, AsyncCallback<Void> callback);

}
