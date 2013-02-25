package com.sid.cloudynote.server;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

public class UploadServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6633378377435316992L;

	private BlobstoreService blobstoreService = BlobstoreServiceFactory
			.getBlobstoreService();

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(req);
		BlobKey blobKey = blobs.get("myFile").get(0);

		if (blobKey != null) {
			resp.setContentType("text/html");
			resp.getWriter().write(blobKey.getKeyString());
			resp.flushBuffer();
		}
	}
}