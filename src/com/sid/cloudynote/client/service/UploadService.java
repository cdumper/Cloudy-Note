package com.sid.cloudynote.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("uploadService")
public interface UploadService extends RemoteService{
	public String createUploadURL();
}
