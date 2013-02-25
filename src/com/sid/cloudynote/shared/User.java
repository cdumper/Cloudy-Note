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
public class User implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6927452327804119956L;
	@PrimaryKey
	@Persistent
	private String id;
	@Persistent
	private String emailAddress;
	@Persistent
	private String nickname;
	@Persistent
	private boolean loggedIn = false;
	@Persistent
	private Set<Key> groups;
	@Persistent
	private Set<String> friends;
	@Persistent(serialized = "true", defaultFetchGroup = "true")
	private Map<Key, Integer> access = new HashMap<Key, Integer>();
	private String loginUrl;
	private String logoutUrl;

	public User(){
		super();
	}
	
	public Map<Key, Integer> getAccess() {
		return access;
	}

	public void setAccess(Map<Key, Integer> access) {
		this.access = access;
	}

	public Set<Key> getGroups() {
		if(groups==null) groups = new HashSet<Key>();
		return groups;
	}

	public void setGroups(Set<Key> groups) {
		this.groups = groups;
	}
	
	public Set<String> getFriends() {
		return friends;
	}

	public void setFriends(Set<String> friends) {
		this.friends = friends;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isLoggedIn() {
		return loggedIn;
	}

	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}

	public String getLoginUrl() {
		return loginUrl;
	}

	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}

	public String getLogoutUrl() {
		return logoutUrl;
	}

	public void setLogoutUrl(String logoutUrl) {
		this.logoutUrl = logoutUrl;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

}
