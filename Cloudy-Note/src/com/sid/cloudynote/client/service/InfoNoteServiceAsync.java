package com.sid.cloudynote.client.service;


import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sid.cloudynote.shared.InfoNote;
import com.sid.cloudynote.shared.Notebook;

public interface InfoNoteServiceAsync {

	void add(InfoNote entity, AsyncCallback<Void> callback);

	void delete(InfoNote entity, AsyncCallback<Void> callback);

	void getPaginationData(String filter, String ordering, long firstResult,
			long maxResult, AsyncCallback<List<InfoNote>> callback);

	void getPaginationData(String filter, String ordering,
			AsyncCallback<List<InfoNote>> callback);

	void getPaginationData(String filter, long firstResult, long maxResult,
			AsyncCallback<List<InfoNote>> callback);

	void getPaginationData(long firstResult, long maxResult,
			AsyncCallback<List<InfoNote>> callback);

	void getPaginationData(String filter,
			AsyncCallback<List<InfoNote>> callback);

	void getPaginationData(AsyncCallback<List<InfoNote>> callback);

	void modify(InfoNote entity, AsyncCallback<Void> callback);

	void getNotes(Notebook currentNotebook,
			AsyncCallback<List<InfoNote>> callback);

}