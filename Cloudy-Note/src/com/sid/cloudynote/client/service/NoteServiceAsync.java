package com.sid.cloudynote.client.service;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sid.cloudynote.client.model.INote;

public interface NoteServiceAsync {

	void add(INote entity, AsyncCallback<Void> callback);

	void delete(INote entity, AsyncCallback<Void> callback);

	void getPaginationData(String filter, String ordering, long firstResult,
			long maxResult, AsyncCallback<List<INote>> callback);

	void getPaginationData(String filter, String ordering,
			AsyncCallback<List<INote>> callback);

	void getPaginationData(String filter, long firstResult, long maxResult,
			AsyncCallback<List<INote>> callback);

	void getPaginationData(long firstResult, long maxResult,
			AsyncCallback<List<INote>> callback);

	void getPaginationData(String filter,
			AsyncCallback<List<INote>> callback);

	void getPaginationData(AsyncCallback<List<INote>> callback);

	void modify(INote entity, AsyncCallback<Void> callback);

}
