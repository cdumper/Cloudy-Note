package com.sid.cloudynote.client.service;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.sid.cloudynote.shared.NotLoggedInException;
import com.sid.cloudynote.shared.Notebook;

@RemoteServiceRelativePath("notebookService")
public interface NotebookService extends RemoteService {

	List<Notebook> getNotebooks() throws NotLoggedInException;

	void add(Notebook entity) throws NotLoggedInException;

	void delete(Notebook entity) throws NotLoggedInException;

	void modify(Notebook entity) throws NotLoggedInException;

}
