package com.sid.cloudynote.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.users.User;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class InfoNote implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8463139417096235807L;
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;
	@Persistent
	User user;
	@Persistent
	private String title;
	@Persistent
	private String content;
	@Persistent(defaultFetchGroup = "true")
	private Notebook notebook;
	@Persistent
	private int visibility = Visibility.PRIVATE;
	@Persistent
	private Date createdTime;
	@Persistent
	private Date lastModifiedTime;
	@Persistent
	private List<Tag> tags;
	@Persistent
	private List<String> attachments;
	@Persistent(serialized = "true", defaultFetchGroup = "true") 
	private Map<String, Integer> access = new HashMap<String, Integer>();

	public Map<String, Integer> getAccess() {
		return access;
	}

	public int getVisibility() {
		return visibility;
	}

	public void setVisibility(int visibility) {
		this.visibility = visibility;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public Date getLastModifiedTime() {
		return lastModifiedTime;
	}

	public void setLastModifiedTime(Date lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}

	public void setAccess(Map<String, Integer> access) {
		this.access = access;
	}

	public InfoNote() {
		super();
	}

	public InfoNote(Notebook notebook, String title, String content) {
		this.notebook = notebook;
		this.title = title;
		this.content = content;
//		this.property = null;
//		this.tags = null;
//		this.attachments = null;
//		this.user = null;
	}

	public InfoNote(Notebook notebook, String title, String content, List<String> attachments) {
		this.notebook = notebook;
		this.title = title;
		this.content = content;
		this.attachments = attachments;
	}

	public String getContent() {
		return content;
	}

	public Key getKey() {
		return key;
	}

	public Notebook getNotebook() {
		return notebook;
	}

	public String getTitle() {
		return title;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public void setNotebook(Notebook notebook) {
		this.notebook = notebook;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	
	public List<Tag> getTags() {
		return tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}


	public List<String> getAttachments() {
		if(attachments==null) attachments = new ArrayList<String>();
		return attachments;
	}

	public void setAttachments(List<String> attachments) {
		this.attachments = attachments;
	}
}
