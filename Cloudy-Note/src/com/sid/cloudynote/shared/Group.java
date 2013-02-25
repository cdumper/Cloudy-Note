package com.sid.cloudynote.shared;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
	@Persistent
	private Key key;
	@Persistent
	private Set<String> members = new HashSet<String>();
	@Persistent(serialized = "true", defaultFetchGroup = "true") 
	private Map<String, Integer> access = new HashMap<String, Integer>();
	
	public Group() {
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
