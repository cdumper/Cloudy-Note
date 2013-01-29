package com.sid.cloudynote.shared;

import java.io.Serializable;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
// public class InfoNote extends Note{
public class InfoNote implements Serializable {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;
	@Persistent
	private String title;
	@Persistent
	private String content;
	@Persistent
	private Notebook notebook;
	@Persistent
	private NoteProperty property;
	@Persistent
	private List<Attachment> attachments;

	public InfoNote() {
		super();
	}

	public InfoNote(Notebook notebook, String title, String content) {
		this.notebook = notebook;
		this.title = title;
		this.content = content;
		this.property = null;
		this.attachments = null;
	}

	public InfoNote(Notebook notebook, String title, String content,
			NoteProperty property, List<Attachment> attachments) {
		this.notebook = notebook;
		this.title = title;
		this.content = content;
		this.property = property;
		this.attachments = attachments;
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public String getContent() {
		return content;
	}

	public Key getKey() {
		return key;
	}

	public Notebook getNotebook() {
		return notebook;
	}

	public NoteProperty getProperty() {
		return property;
	}

	public String getTitle() {
		return title;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public void setNotebook(Notebook notebook) {
		this.notebook = notebook;
	}

	public void setProperty(NoteProperty property) {
		this.property = property;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
