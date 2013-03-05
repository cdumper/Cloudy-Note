package com.sid.cloudynote.shared;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class Group implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;
	@Persistent
	private String name;
	@Persistent
	private String owner;
	@Persistent
	private int visibility = Visibility.PRIVATE; 
	@Persistent
	private Set<String> members = new HashSet<String>();

	@Persistent(serialized = "true", defaultFetchGroup = "true") 
	private Map<Key, Integer> access = new HashMap<Key, Integer>();
	
	public Group() {
	}

	public Group(String name,String owner, Set<String> members) {
		super();
		this.name = name;
		this.owner = owner;
		this.members = members;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setMembers(Set<String> members) {
		this.members = members;
	}

	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public int getVisibility() {
		return visibility;
	}

	public void setVisibility(int visibility) {
		this.visibility = visibility;
	}

	public Set<String> getMembers() {
		return members;
	}

	public Map<Key, Integer> getAccess() {
		return access;
	}

	public void setAccess(Map<Key, Integer> access) {
		this.access = access;
	}
}
