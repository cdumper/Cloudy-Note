package com.sid.cloudynote.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.sid.cloudynote.client.model.IDAO;
import com.sid.cloudynote.client.model.INote;

@RemoteServiceRelativePath("noteService")
public interface NoteService extends RemoteService, IDAO<INote>{

}
