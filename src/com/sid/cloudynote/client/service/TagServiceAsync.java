package com.sid.cloudynote.client.service;

import java.util.List;

import com.google.appengine.api.datastore.Key;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sid.cloudynote.shared.Tag;

public interface TagServiceAsync {

	void add(Tag entity, AsyncCallback<Void> callback);

	void delete(Tag entity, AsyncCallback<Void> callback);

	void getPaginationData(String filter, String ordering, long firstResult,
			long maxResult, AsyncCallback<List<Tag>> callback);

	void getPaginationData(String filter, String ordering,
			AsyncCallback<List<Tag>> callback);

	void getPaginationData(String filter, long firstResult, long maxResult,
			AsyncCallback<List<Tag>> callback);

	void getPaginationData(long firstResult, long maxResult,
			AsyncCallback<List<Tag>> callback);

	void getPaginationData(String filter, AsyncCallback<List<Tag>> callback);

	void getPaginationData(AsyncCallback<List<Tag>> callback);

	void modify(Tag entity, AsyncCallback<Void> callback);

	void getTags(String email, AsyncCallback<List<Tag>> callback);

	void getTags(List<Key> tagsKey, AsyncCallback<List<Tag>> callback);

}
