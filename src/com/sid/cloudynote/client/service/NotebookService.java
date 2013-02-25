package com.sid.cloudynote.client.service;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.sid.cloudynote.client.IDAO;
import com.sid.cloudynote.shared.NotLoggedInException;
import com.sid.cloudynote.shared.Notebook;

@RemoteServiceRelativePath("notebookService")
public interface NotebookService extends RemoteService, IDAO<Notebook>{

	List<Notebook> getNotebooks() throws NotLoggedInException;

}
