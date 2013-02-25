package com.sid.cloudynote.server.serviceImpl;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.sid.cloudynote.client.service.UploadService;

public class UploadServiceImpl extends RemoteServiceServlet implements
		UploadService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1035980736595585885L;

	@Override
	public String createUploadURL() {
		BlobstoreService blobstoreService = BlobstoreServiceFactory
				.getBlobstoreService();
		return blobstoreService.createUploadUrl("/cloudy_note/upload");
	}
}
