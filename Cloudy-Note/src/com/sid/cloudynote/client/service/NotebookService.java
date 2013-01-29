package com.sid.cloudynote.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.sid.cloudynote.client.IDAO;
import com.sid.cloudynote.shared.Notebook;

@RemoteServiceRelativePath("notebookService")
public interface NotebookService extends RemoteService, IDAO<Notebook>{

}
