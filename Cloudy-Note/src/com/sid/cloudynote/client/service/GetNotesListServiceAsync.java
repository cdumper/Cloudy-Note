package com.sid.cloudynote.client.service;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sid.cloudynote.client.Note;

public interface GetNotesListServiceAsync {
	void getNoteList(AsyncCallback<List<Note>> callback);
}
