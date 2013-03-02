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
	private Set<String> members = new HashSet<String>();

	@Persistent(serialized = "true", defaultFetchGroup = "true") 
	private Map<String, Integer> access = new HashMap<String, Integer>();
	
	public Group() {
	}

	public Group(String name, Set<String> members) {
		super();
		this.name = name;
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

	public Set<String> getMembers() {
		return members;
	}

	public Map<String, Integer> getAccess() {
		return access;
	}

	public void setAccess(Map<String, Integer> access) {
		this.access = access;
	}
}
