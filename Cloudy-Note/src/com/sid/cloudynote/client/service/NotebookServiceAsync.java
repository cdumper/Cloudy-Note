package com.sid.cloudynote.client.service;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sid.cloudynote.client.model.Notebook;

public interface NotebookServiceAsync {

	void add(Notebook entity, AsyncCallback<Void> callback);

	void delete(Notebook entity, AsyncCallback<Void> callback);

	void getPaginationData(String filter, String ordering, long firstResult,
			long maxResult, AsyncCallback<List<Notebook>> callback);

	void getPaginationData(String filter, String ordering,
			AsyncCallback<List<Notebook>> callback);

	void getPaginationData(String filter, long firstResult, long maxResult,
			AsyncCallback<List<Notebook>> callback);

	void getPaginationData(long firstResult, long maxResult,
			AsyncCallback<List<Notebook>> callback);

	void getPaginationData(String filter, AsyncCallback<List<Notebook>> callback);

	void getPaginationData(AsyncCallback<List<Notebook>> callback);

	void modify(Notebook entity, AsyncCallback<Void> callback);

}
