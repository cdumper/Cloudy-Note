package com.sid.cloudynote.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.annotations.Element;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.users.User;

@PersistenceCapable(identityType = IdentityType.APPLICATION,detachable="true")
public class Notebook implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3123511693757621679L;
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;
	@Persistent
	private User user;
	@Persistent
	private String name;
	@Persistent
	private int totalNotes = 0;
	@Persistent(mappedBy="notebook")
    @Element(dependent = "true")
	private List<InfoNote> notes;
	/**
	 * access contains the access information of the note
	 * String email
	 * int permission
	 */
	@Persistent(serialized = "true", defaultFetchGroup = "true") 
	private Map<String, Integer> access = new HashMap<String, Integer>();

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
	
	public void setKey(Key key) {
		this.key = key;
	}
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getTotalNotes() {
		return totalNotes;
	}

	public void setTotalNotes(int totalNotes) {
		this.totalNotes = totalNotes;
	}

	public List<InfoNote> getNotes() {
		return notes;
	}

	public void setNotes(List<InfoNote> notes) {
		this.notes = notes;
	}

	public Map<String, Integer> getAccess() {
		return access;
	}

	public void setAccess(Map<String, Integer> access) {
		this.access = access;
	}
}
