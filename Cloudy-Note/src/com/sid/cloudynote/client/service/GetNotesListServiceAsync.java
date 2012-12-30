package com.sid.cloudynote.client.service;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sid.cloudynote.client.model.INote;

public interface GetNotesListServiceAsync {
	void getNoteList(AsyncCallback<List<INote>> callback);
}
