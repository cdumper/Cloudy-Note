package com.sid.cloudynote.shared;

import java.io.Serializable;

import com.google.appengine.api.datastore.Key;

public interface INote extends Serializable {
	NoteProperty getProperty();
	String getTitle();
	String getContent();
	Key getKey();
	Notebook getNotebook();
}
