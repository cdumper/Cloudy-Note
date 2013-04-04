package com.sid.cloudynote.server.serviceImpl;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.sid.cloudynote.client.service.ShareNoteService;
import com.sid.cloudynote.server.EmailService;
import com.sid.cloudynote.shared.InfoNote;

public class ShareNoteServiceImpl extends RemoteServiceServlet implements ShareNoteService{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5465653789797644676L;

	@Override
	public String sendByEmail(String email, String message, InfoNote note) {
		//TODO need to consider attachments in the future
		String messageBody = message + "</hr>" + note.getContent();
		EmailService.sendEmail(getUser().getEmail(), email, note.getTitle(), messageBody);
		
		return "Success";
	}
	
	private User getUser() {
		UserService userService = UserServiceFactory
				.getUserService();
		return userService.getCurrentUser();
	}

}
