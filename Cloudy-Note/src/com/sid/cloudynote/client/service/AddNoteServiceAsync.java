package com.sid.cloudynote.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AddNoteServiceAsync {

	void addNote(String title, String content, AsyncCallback<Void> callback);

}
