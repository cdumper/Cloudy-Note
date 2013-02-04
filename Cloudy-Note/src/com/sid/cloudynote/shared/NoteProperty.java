package com.sid.cloudynote.shared;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable(identityType = IdentityType.APPLICATION,detachable="true")
public class NoteProperty implements Serializable{
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
	@Persistent
	InfoNote note;
//	@Persistent
//	private String path; //relative path to notebooks
//	@Persistent
//	private Set<Tag> tags;
//	@Persistent
//	private Visibility visibility;
//	@Persistent
	private Date createdTime;
	@Persistent
	private Date lastModifiedTime;
	
	public NoteProperty() {
		super();
	}
	
	public NoteProperty(Date createdTime, Date lastModifiedTime) {
		this.createdTime = createdTime;
		this.lastModifiedTime = lastModifiedTime;
	}

	public Key getKey() {
		return key;
	}
//
//	public Set<Tag> getTags() {
//		return tags;
//	}
//
//	public Visibility getVisibility() {
//		return visibility;
//	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public Date getLastModifiOedTime() {
		return lastModifiedTime;
	}
	
}
