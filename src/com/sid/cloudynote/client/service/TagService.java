package com.sid.cloudynote.client.service;

import java.util.List;

import com.google.appengine.api.datastore.Key;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.sid.cloudynote.shared.NotLoggedInException;
import com.sid.cloudynote.shared.Tag;

@RemoteServiceRelativePath("tagService")
public interface TagService extends RemoteService {
	List<Tag> getTags(String email) throws NotLoggedInException;

	List<Tag> getTags(List<Key> tagsKey) throws NotLoggedInException;

	List<Tag> createTags(List<Tag> tags) throws NotLoggedInException;

	void add(Tag entity) throws NotLoggedInException;

	void delete(Tag entity) throws NotLoggedInException;

	void modify(Tag entity) throws NotLoggedInException;
}
