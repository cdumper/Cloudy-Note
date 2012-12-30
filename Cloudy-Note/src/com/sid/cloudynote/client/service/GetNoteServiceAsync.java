package com.sid.cloudynote.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sid.cloudynote.client.model.InfoNote;

public interface GetNoteServiceAsync {

	void getNote(String title, AsyncCallback<InfoNote> callback);

}
