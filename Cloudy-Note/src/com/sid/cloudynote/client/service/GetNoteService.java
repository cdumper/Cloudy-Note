package com.sid.cloudynote.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.sid.cloudynote.client.model.InfoNote;

@RemoteServiceRelativePath("getNote")
public interface GetNoteService extends RemoteService{
	InfoNote getNote(String title);
}
