package com.sid.cloudynote.client.model;

import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable(identityType = IdentityType.APPLICATION,detachable="true")
public class InfoNote extends Note{
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;
	@Persistent
	private Notebook notebook;
	@Persistent
	private String title;
	@Persistent
	private String content;
	@Persistent
	private NoteProperty property;
	@Persistent
	private List<Attachment> attachments;

	public InfoNote() {
		super();
		this.title = "Untitled";
		this.content = "content...";
	}
	
	public InfoNote(Notebook notebook) {
		this.title = "Untitled";
		this.content = "content...";
		this.notebook = notebook;
	}
	
	public InfoNote(Notebook notebook, String title, String content) {
		this.notebook = notebook;
		this.title = title;
		this.content = content;
	}

	public InfoNote(Notebook notebook, String title, String content, NoteProperty property,
			List<Attachment> attachments) {
		this.notebook = notebook;
		this.title = title;
		this.content = content;
		this.property = property;
		this.attachments = attachments;
	}
	
	@Override
	public String getTitle() {
		return title;
	}
	@Override
	public String getContent() {
		return content;
	}
	@Override
	public Key getKey() {
		return key;
	}
	@Override
	public NoteProperty getProperty() {
		return this.property;
	}

	public List<Attachment> getAttachments() {
		return this.attachments;
	}
	@Override
	public Notebook getNotebook() {
		return notebook;
	}
	@Override
	public void setNotebook(Notebook notebook) {
		this.notebook = notebook;
	}
}
