package com.sid.cloudynote.client.service;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.sid.cloudynote.client.model.IDAO;
import com.sid.cloudynote.client.model.InfoNote;
import com.sid.cloudynote.client.model.Notebook;

@RemoteServiceRelativePath("noteService")
public interface InfoNoteService extends RemoteService, IDAO<InfoNote>{
	List<InfoNote> getNotes(Notebook currentNotebook);
}
