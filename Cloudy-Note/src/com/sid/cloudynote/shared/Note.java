package com.sid.cloudynote.shared;

import com.google.appengine.api.datastore.Key;

public class Note implements INote {
	private Key key;
	private String title;
	private Notebook notebook;
	private String content;

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

	public Key getKey() {
		return key;
	}

	public Notebook getNotebook() {
		return notebook;
	}

	public void setNotebook(Notebook notebook) {
		this.notebook = notebook;
	}
}
