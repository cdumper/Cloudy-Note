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
	/**
	 * 
	 */
	private static final long serialVersionUID = 7312153645429635782L;
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
	@Persistent
	InfoNote note;
	@Persistent
	private int visibility;
	@Persistent
	private Date createdTime;
	@Persistent
	private Date lastModifiedTime;
	
	public NoteProperty() {
		this.visibility = Visibility.PRIVATE;
	}
	
	public NoteProperty(Date createdTime, Date lastModifiedTime) {
		this();
		this.createdTime = createdTime;
		this.lastModifiedTime = lastModifiedTime;
	}

	public Key getKey() {
		return key;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public Date getLastModifiOedTime() {
		return lastModifiedTime;
	}

	public int getVisibility() {
		return visibility;
	}

	public void setVisibility(int visibility) {
		this.visibility = visibility;
	}

	public InfoNote getNote() {
		return note;
	}

	public void setNote(InfoNote note) {
		this.note = note;
	}

	public Date getLastModifiedTime() {
		return lastModifiedTime;
	}

	public void setLastModifiedTime(Date lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}
	
}
