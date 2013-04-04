package com.sid.cloudynote.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.sid.cloudynote.shared.InfoNote;

@RemoteServiceRelativePath("shareNoteService")
public interface ShareNoteService extends RemoteService{
	String sendByEmail(String email, String message, InfoNote note);
}
