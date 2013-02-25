package com.sid.cloudynote.client.service;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface BlobServiceAsync {
	void getBlobFileName(List<String> key, AsyncCallback<List<String>> callback);
}
