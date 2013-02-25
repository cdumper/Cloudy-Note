package com.sid.cloudynote.client.service;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("blobService")
public interface BlobService extends RemoteService{
	List<String> getBlobFileName(List<String> key);
}
