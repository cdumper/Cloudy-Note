package com.sid.cloudynote.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sid.cloudynote.shared.LoginInfo;

public interface LoginServiceAsync {
	void login(String requestUri, AsyncCallback<LoginInfo> callback);
}
