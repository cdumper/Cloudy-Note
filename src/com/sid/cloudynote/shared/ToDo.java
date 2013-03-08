package com.sid.cloudynote.shared;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

//TODO todo note model to be implemented
@PersistenceCapable(identityType=IdentityType.APPLICATION)
public class ToDo extends Note{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@PrimaryKey
	@Persistent(valueStrategy=IdGeneratorStrategy.IDENTITY)
	private Key key;
	
	public ToDo() {
		super();
	}

	@Override
	public String getTitle() {
		return null;
	}

	@Override
	public String getContent() {
		return null;
	}

	@Override
	public Key getKey() {
		return key;
	}
}
