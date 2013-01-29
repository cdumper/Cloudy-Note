package com.sid.cloudynote.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.Element;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable(identityType = IdentityType.APPLICATION,detachable="true")
public class Notebook implements Serializable{
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;
	@Persistent
	private String name;
	@Persistent(mappedBy="notebook")
    @Element(dependent = "true")
	private List<InfoNote> notes;
	
	public void setKey(Key key) {
		this.key = key;
	}

	public Notebook() {
		super();
		this.notes = new ArrayList<InfoNote>();
	}

	public Notebook(String name) {
		this.name = name;
		this.notes = new ArrayList<InfoNote>();
	}

	public Key getKey() {
		return key;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<InfoNote> getNotes() {
		return notes;
	}

	public void setNotes(List<InfoNote> notes) {
		this.notes = notes;
	}
}
