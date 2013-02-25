package com.sid.cloudynote.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.sid.cloudynote.client.service.LoginService;
import com.sid.cloudynote.client.service.LoginServiceAsync;
import com.sid.cloudynote.shared.User;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Cloudy_Note implements EntryPoint {
	private HandlerManager eventBus;
	private AppController controller;

	// following variables are for the login service
	private User loginUser = null;
	private VerticalPanel loginPanel = new VerticalPanel();
	private Label loginLabel = new Label(
			"Please sign in to your Google Account to access the Cloudy Note application.");
	private Anchor signInLink = new Anchor("Sign In");

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		// Check login status using login service.
		LoginServiceAsync loginService = GWT.create(LoginService.class);
		loginService.login(GWT.getHostPageBaseURL(),
				new AsyncCallback<User>() {
					public void onFailure(Throwable error) {
					}

					public void onSuccess(User result) {
						loginUser = result;
						if (loginUser.isLoggedIn()) {
							loadApp();
						} else {
							loadLogin();
						}
					}
				});
	}

	private void loadLogin() {
		// Assemble login panel.
		signInLink.setHref(loginUser.getLoginUrl());
		loginPanel.add(loginLabel);
		loginPanel.add(signInLink);
		RootLayoutPanel.get().add(loginPanel);
	}

	private void loadApp() {
		eventBus = new HandlerManager(null);
		controller = new AppController(eventBus, loginUser);
		
		controller.go(RootLayoutPanel.get());
	}
}