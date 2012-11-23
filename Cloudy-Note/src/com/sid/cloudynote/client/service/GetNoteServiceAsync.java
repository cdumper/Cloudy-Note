package com.sid.cloudynote.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sid.cloudynote.client.Note;

public interface GetNoteServiceAsync {

	void getNote(String title, AsyncCallback<Note> callback);

}
