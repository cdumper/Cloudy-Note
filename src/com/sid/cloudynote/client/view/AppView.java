package com.sid.cloudynote.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.NotStrict;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sid.cloudynote.client.AppController;
import com.sid.cloudynote.client.event.EditNoteDoneEvent;
import com.sid.cloudynote.client.event.EditNoteEvent;
import com.sid.cloudynote.client.event.GroupsChangedEvent;
import com.sid.cloudynote.client.event.HideSharingNoteViewEvent;
import com.sid.cloudynote.client.event.NewNoteEvent;
import com.sid.cloudynote.client.event.NoNotesExistEvent;
import com.sid.cloudynote.client.event.NoteChangedEvent;
import com.sid.cloudynote.client.event.NoteSelectionChangedEvent;
import com.sid.cloudynote.client.event.NotebookChangedEvent;
import com.sid.cloudynote.client.event.PresentNotesEvent;
import com.sid.cloudynote.client.event.TagChangedEvent;
import com.sid.cloudynote.client.event.ViewGroupNotesEvent;
import com.sid.cloudynote.client.event.ViewPublicNotesEvent;
import com.sid.cloudynote.client.event.ViewSharedNoteEvent;
import com.sid.cloudynote.client.event.ViewSharedNotesEvent;
import com.sid.cloudynote.client.presenter.Presenter;
import com.sid.cloudynote.client.sharing.presenter.FriendViewPresenter;
import com.sid.cloudynote.client.sharing.view.FriendView;
import com.sid.cloudynote.client.sharing.view.SharingView;
import com.sid.cloudynote.shared.User;

public class AppView extends Composite implements Presenter {
	@UiField
	DockLayoutPanel dockLayoutPanel;
	@UiField
	Anchor user;
	@UiField
	Button aboutButton;
	@UiField
	Button notificationButton;
	@UiField
	Button settingsButton;
	@UiField
	Anchor signOutLink;
	@UiField
	DeckPanel deck;
	@UiField
	PersonalView personalView;
	@UiField
	SharingView sharingView;
	@UiField
	Button myNotesButton;
	@UiField
	Button othersNotesButton;
	@UiField
	Button friendsButton;
	@UiField
	Button adminButton;
	@UiField
	FriendView friendsView;

	private HandlerManager eventBus;
	private User loginInfo = null;
	private FriendViewPresenter friendsPresenter;
	private static AppViewUiBinder uiBinder = GWT.create(AppViewUiBinder.class);

	interface AppViewUiBinder extends UiBinder<Widget, AppView> {
	}
	
	interface GlobalResources extends ClientBundle {
		@NotStrict
		@Source("global.css")
		CssResource css();
	}

	public AppView(HandlerManager eventBus, User login) {
		GWT.<GlobalResources> create(GlobalResources.class).css()
				.ensureInjected();
		initWidget(uiBinder.createAndBindUi(this));
		this.eventBus = eventBus;
		this.loginInfo = login;
		this.personalView.setEventBus(eventBus);
		user.setText(loginInfo.getEmail());
		signOutLink.setHref(loginInfo.getLogoutUrl());
		bindEvents();
		deck.showWidget(0);
	}

	@Override
	public void go(HasWidgets container) {
		showMyNotes(container);
	}
	
	public void showMyNotes(HasWidgets container){
		container.clear();
		deck.showWidget(0);
		container.add(dockLayoutPanel);
	}
	
	public void showOthersNotes(HasWidgets container) {
		container.clear();
		if (sharingView == null) {
			sharingView = new SharingView();
		}
		sharingView.seteEventBus(eventBus);
		bindSharingEvents();
		deck.showWidget(1);
		container.add(dockLayoutPanel);
	}

	public void showFriends(HasWidgets container) {
		container.clear();
		if (friendsView == null) {
			friendsView = new FriendView();
			friendsPresenter = new FriendViewPresenter(friendsView,eventBus);
			friendsView.setPresenter(friendsPresenter);
		}
		if (friendsPresenter == null) {
			friendsPresenter = new FriendViewPresenter(friendsView,eventBus);
			friendsView.setPresenter(friendsPresenter);
		}
		friendsPresenter.go(friendsView.getContainer());
		bindFriendEvents();
		deck.showWidget(2);
		container.add(dockLayoutPanel);
	}
	
	//TODO show admin page
	public void showAdmin(HasWidgets container){
		container.clear();
		deck.showWidget(0);
		container.add(dockLayoutPanel);
	}
	
	private void bindFriendEvents() {
		eventBus.addHandler(GroupsChangedEvent.TYPE, friendsView);
	}
	
	private void bindSharingEvents() {
		eventBus.addHandler(ViewPublicNotesEvent.TYPE, sharingView.noteListView);
		eventBus.addHandler(ViewSharedNotesEvent.TYPE, sharingView.noteListView);
		eventBus.addHandler(ViewGroupNotesEvent.TYPE, sharingView.noteListView);
		eventBus.addHandler(ViewSharedNoteEvent.TYPE, sharingView);
		eventBus.addHandler(HideSharingNoteViewEvent.TYPE, sharingView);
		eventBus.addHandler(GroupsChangedEvent.TYPE, sharingView.groupView);
	}

	private void bindEvents() {
		eventBus.addHandler(NewNoteEvent.TYPE, personalView.noteView);
		eventBus.addHandler(EditNoteEvent.TYPE, personalView.noteView);
		eventBus.addHandler(NoteChangedEvent.TYPE, personalView.noteListView);
		eventBus.addHandler(PresentNotesEvent.TYPE, personalView.noteListView);
		eventBus.addHandler(NotebookChangedEvent.TYPE,
				personalView.notebookListView);
		eventBus.addHandler(EditNoteDoneEvent.TYPE, personalView.noteView);
		eventBus.addHandler(NoNotesExistEvent.TYPE, personalView.noteView);
		eventBus.addHandler(NoteSelectionChangedEvent.TYPE,
				personalView.noteView);
		eventBus.addHandler(TagChangedEvent.TYPE, personalView.notebookListView);
		eventBus.addHandler(GroupsChangedEvent.TYPE, personalView.noteListView);
	}

	@UiHandler("myNotesButton")
	void showMyNotes(ClickEvent e) {
		AppController.get().setPageState(AppController.PERSONAL_PAGE);
		AppController.get().go(RootLayoutPanel.get());
	}

	@UiHandler("othersNotesButton")
	void showOthersNotes(ClickEvent e) {
		AppController.get().setPageState(AppController.SHARING_PAGE);
		AppController.get().go(RootLayoutPanel.get());
	}

	@UiHandler("friendsButton")
	void showFriends(ClickEvent e) {
		AppController.get().setPageState(AppController.FRIENDS_PAGE);
		AppController.get().go(RootLayoutPanel.get());
	}
	
	@UiHandler("adminButton")
	void showAdmin(ClickEvent e) {
		AppController.get().setPageState(AppController.ADMIN_PAGE);
		AppController.get().go(RootLayoutPanel.get());
	}
}
