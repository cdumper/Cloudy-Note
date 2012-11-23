package com.sid.cloudynote.client;

import java.io.Serializable;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION,detachable="true")
public class Note implements Serializable{
	private static final long serialVersionUID = 1L;
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;
	@Persistent
	private String title;
	@Persistent
	private String content;

	
	public Note() {
		super();
	}

	public Note(String title, String content) {
		this.title = title;
		this.content = content;

	}

	public String getTitle() {
		return title;
	}

	public String getContent() {
		return content;
	}

	public Long getKey() {
		return id;
	}
}
