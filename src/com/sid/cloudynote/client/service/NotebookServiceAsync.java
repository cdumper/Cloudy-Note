package com.sid.cloudynote.client.service;


import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sid.cloudynote.shared.Notebook;

public interface NotebookServiceAsync {

	void add(Notebook entity, AsyncCallback<Void> callback);

	void delete(Notebook entity, AsyncCallback<Void> callback);

	void modify(Notebook entity, AsyncCallback<Void> callback);

	void getNotebooks(AsyncCallback<List<Notebook>> callback);

}
