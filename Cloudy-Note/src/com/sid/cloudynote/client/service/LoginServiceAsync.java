package com.sid.cloudynote.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sid.cloudynote.shared.User;

public interface LoginServiceAsync {
	void login(String requestUri, AsyncCallback<User> callback);
}
