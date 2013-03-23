package com.sid.cloudynote.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface InfoNoteSearchServiceAsync {

	void searchNotes(String text, AsyncCallback<Void> callback);

}
