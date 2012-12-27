package com.sid.cloudynote.client.model;

import java.io.Serializable;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable(identityType = IdentityType.APPLICATION,detachable="true")
public class Note implements Serializable, INote{
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;
	@Persistent
	private String title;
	@Persistent
	private String content;
	@Persistent
	private NoteProperty property;
	@Persistent
	private List<Attachment> attachments;

	public Note() {
		super();
		this.title = "new note";
		this.content = "content";
	}

	public Note(String title, String content, NoteProperty property,
			List<Attachment> attachments) {
		this.title = title;
		this.content = content;
		this.property = property;
		this.attachments = attachments;
	}

	public String getTitle() {
		return title;
	}

	public String getContent() {
		return content;
	}

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
}
