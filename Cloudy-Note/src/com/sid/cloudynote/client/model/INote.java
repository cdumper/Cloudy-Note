package com.sid.cloudynote.client.model;

import com.google.appengine.api.datastore.Key;

public interface INote {
	NoteProperty getProperty();
	String getTitle();
	String getContent();
	Key getKey();
}
