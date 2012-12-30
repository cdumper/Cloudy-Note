package com.sid.cloudynote.client.model;

import com.google.appengine.api.datastore.Key;

public class Note implements INote {
	private Key key;
	private String title;
	private String content;
	private NoteProperty property;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public NoteProperty getProperty() {
		return property;
	}

	public void setProperty(NoteProperty property) {
		this.property = property;
	}

	public Key getKey() {
		return key;
	}
}
