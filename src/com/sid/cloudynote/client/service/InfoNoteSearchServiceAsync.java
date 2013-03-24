package com.sid.cloudynote.client.service;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sid.cloudynote.shared.InfoNote;

public interface InfoNoteSearchServiceAsync {

	void searchNotes(String text, AsyncCallback<List<InfoNote>> callback);

}
