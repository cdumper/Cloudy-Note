package com.sid.cloudynote.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface UploadServiceAsync {

	void createUploadURL(AsyncCallback<String> callback);

}
