package com.sid.cloudynote.client.service;

import java.util.List;

import com.google.appengine.api.datastore.Key;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.sid.cloudynote.client.IDAO;
import com.sid.cloudynote.shared.NotLoggedInException;
import com.sid.cloudynote.shared.Tag;

@RemoteServiceRelativePath("tagService")
public interface TagService extends RemoteService, IDAO<Tag>{
	List<Tag> getTags(String email) throws NotLoggedInException;

	List<Tag> getTags(List<Key> tagsKey);
}
