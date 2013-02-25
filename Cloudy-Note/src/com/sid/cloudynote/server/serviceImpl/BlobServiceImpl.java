package com.sid.cloudynote.server.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.sid.cloudynote.client.service.BlobService;
import com.sid.cloudynote.shared.NotLoggedInException;

public class BlobServiceImpl extends RemoteServiceServlet implements
		BlobService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 874980440598498197L;

	@Override
	public List<String> getBlobFileName(List<String> keys) {
		try {
			checkLoggedIn();
		} catch (NotLoggedInException e1) {
			e1.printStackTrace();
		}
		BlobInfoFactory blobInfoFactory = new BlobInfoFactory();
		List<String> fileNames = new ArrayList<String>();
		BlobKey blobKey;
		BlobInfo blobInfo;
		for(String key : keys){
			blobKey = new BlobKey(key);
			blobInfo = blobInfoFactory.loadBlobInfo(blobKey);
			fileNames.add(blobInfo.getFilename());
		}
		return fileNames;
	}

	private void checkLoggedIn() throws NotLoggedInException {
		if (getUser() == null) {
			throw new NotLoggedInException("Not logged in.");
		}
	}

	private User getUser() {
		UserService userService = UserServiceFactory.getUserService();
		return userService.getCurrentUser();
	}

}
