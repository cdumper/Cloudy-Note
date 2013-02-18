package com.sid.cloudynote.server;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

public class ServeServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4983511900564415803L;
	private BlobstoreService blobstoreService = BlobstoreServiceFactory
			.getBlobstoreService();
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws IOException {
		BlobKey blobKey = new BlobKey(req.getParameter("blob-key"));
		blobstoreService.serve(blobKey, res);
		System.out.println("serve servlet excuted");
	}
}
