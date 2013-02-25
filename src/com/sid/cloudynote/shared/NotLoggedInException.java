package com.sid.cloudynote.shared;

import java.io.Serializable;

public class NotLoggedInException extends Exception implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8723735879707408043L;

	public NotLoggedInException() {
		super();
	}

	public NotLoggedInException(String message) {
		super(message);
	}

}
