package com.sid.cloudynote.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.NotStrict;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.sid.cloudynote.client.presenter.NoteListPresenter;
import com.sid.cloudynote.client.presenter.NotePresenter;
import com.sid.cloudynote.client.presenter.NotebookListPresenter;
import com.sid.cloudynote.client.presenter.Presenter;
import com.sid.cloudynote.client.presenter.SearchPresenter;
import com.sid.cloudynote.client.service.LoginService;
import com.sid.cloudynote.client.service.LoginServiceAsync;
import com.sid.cloudynote.client.view.BottomView;
import com.sid.cloudynote.client.view.NoteListView;
import com.sid.cloudynote.client.view.NoteView;
import com.sid.cloudynote.client.view.NotebookListView;
import com.sid.cloudynote.client.view.SearchView;
import com.sid.cloudynote.shared.LoginInfo;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Cloudy_Note implements EntryPoint, Presenter {
	interface Binder extends UiBinder<DockLayoutPanel, Cloudy_Note> {
	}

	interface GlobalResources extends ClientBundle {
		@NotStrict
		@Source("global.css")
		CssResource css();
	}

	private static final Binder binder = GWT.create(Binder.class);

	@UiField
	DockLayoutPanel dockLayoutPanel;
	@UiField
	SearchView searchView;
	@UiField
	BottomView bottomView;
	@UiField
	NotebookListView notebookListView;
	@UiField
	NoteListView noteListView;
	@UiField
	NoteView noteView;
	@UiField
	Label user;
	@UiField
	Anchor aboutLink;
	@UiField
	Anchor signOutLink;
	

	private HandlerManager eventBus;
	private AppController appViewer;

	// following variables are for the login service
	private LoginInfo loginInfo = null;
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
	    loginService.login(GWT.getHostPageBaseURL(), new AsyncCallback<LoginInfo>() {
	      public void onFailure(Throwable error) {
	      }

	      public void onSuccess(LoginInfo result) {
	        loginInfo = result;
	        if(loginInfo.isLoggedIn()) {
	          loadApp();
	        } else {
	          loadLogin();
	        }
	      }
	    });
	}
	
	private void loadLogin() {
	    // Assemble login panel.
	    signInLink.setHref(loginInfo.getLoginUrl());
	    loginPanel.add(loginLabel);
	    loginPanel.add(signInLink);
	    RootLayoutPanel.get().add(loginPanel);
	  }

	private void loadApp() {
		// initate ui components
		GWT.<GlobalResources> create(GlobalResources.class).css()
				.ensureInjected();
		dockLayoutPanel = binder.createAndBindUi(this);
		user.setText(loginInfo.getEmailAddress());
		signOutLink.setHref("Welcome back, "+loginInfo.getLogoutUrl());

		eventBus = new HandlerManager(null);
		appViewer = new AppController(this, eventBus);

		appViewer.go(RootLayoutPanel.get());
	}

	@Override
	public void go(HasWidgets container) {
		container.clear();
		container.add(this.dockLayoutPanel);
		bindPresentersAndViews();
	}

	private void bindPresentersAndViews() {
		NotebookListPresenter notebookListPresenter = new NotebookListPresenter(
				this.notebookListView, eventBus);
		NoteListPresenter noteListPresenter = new NoteListPresenter(
				this.noteListView, eventBus);
		SearchPresenter searchPresenter = new SearchPresenter(this.searchView,
				eventBus);
		NotePresenter notePresenter = new NotePresenter(
				this.noteView.asWidget(), eventBus);

		this.noteListView.setPresenter(noteListPresenter);
		this.notebookListView.setPresenter(notebookListPresenter);
		this.searchView.setPresenter(searchPresenter);
		this.noteView.setPresenter(notePresenter);

		notebookListPresenter.go(notebookListView.getContainer());
		noteListPresenter.go(noteListView.getContainer());
		searchPresenter.go(searchView.getContainer());
		notePresenter.go(noteView.getContainer());
	}
}