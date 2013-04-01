package com.sid.cloudynote.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class User implements Serializable, Comparable<User> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6927452327804119956L;
	@PrimaryKey
//	@Persistent(valueStrategy=IdGeneratorStrategy.IDENTITY)
//	@Extension(vendorName="datanucleus", key="gae.encoded-pk", value="true")
//	private String id;
	@Persistent
	private String email;
	@Persistent
	private String nickname;
	@Persistent
	private String fullName = "";
	@Persistent
	private String profileImageBlobKey;
	@Persistent
	private String profileImageUrl;
	public String getProfileImageUrl() {
		return profileImageUrl;
	}

	public void setProfileImageUrl(String profileImageUrl) {
		this.profileImageUrl = profileImageUrl;
	}

	@Persistent
	private int totalNotes = 0;
	@Persistent
	private boolean loggedIn = false;
	@Persistent
	private List<Key> groups = new ArrayList<Key>();;
	@Persistent(serialized = "true", defaultFetchGroup = "true")
	private Map<String, Date> friends = new LinkedHashMap<String, Date>();
	@Persistent(serialized = "true", defaultFetchGroup = "true")
	private Map<Key, Integer> access = new LinkedHashMap<Key, Integer>();
	@NotPersistent
	private String loginUrl;
	@NotPersistent
	private String logoutUrl;

	public User(){
		super();
	}
	
	public Map<Key, Integer> getAccess() {
		return access;
	}

	public void setAccess(Map<Key, Integer> access) {
		this.access.clear();
		this.access.putAll(access);
	}

	public List<Key> getGroups() {
		return groups;
	}

	public void setGroups(List<Key> groups) {
		this.groups.clear();
		this.groups.addAll(groups);
	}
	
	public Map<String, Date> getFriends() {
		return friends;
	}

	public void setFriends(Map<String, Date> friends) {
		this.friends.clear();
		this.friends.putAll(friends);
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String emailAddress) {
		this.email = emailAddress;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getProfileImage() {
		return profileImageBlobKey;
	}

	public void setProfileImage(String profileImage) {
		this.profileImageBlobKey = profileImage;
	}

	public int getTotalNotes() {
		return totalNotes;
	}

	public void setTotalNotes(int totalNotes) {
		this.totalNotes = totalNotes;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof User){
			User user = (User) obj;
			if(this.email.equals(user.getEmail())){
				return true;
			}
		}
		return false;
	}

	@Override
	public int compareTo(User user) {
		return this.getEmail().compareToIgnoreCase(user.getEmail());
	}
}
