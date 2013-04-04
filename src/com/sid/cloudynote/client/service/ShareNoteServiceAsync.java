package com.sid.cloudynote.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sid.cloudynote.shared.InfoNote;

public interface ShareNoteServiceAsync {

	void sendByEmail(String email, String message, InfoNote note,
			AsyncCallback<String> callback);

}
