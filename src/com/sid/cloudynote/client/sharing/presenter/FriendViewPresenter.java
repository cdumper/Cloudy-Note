package com.sid.cloudynote.client.sharing.presenter;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.sid.cloudynote.client.AppController;
import com.sid.cloudynote.client.event.GroupsChangedEvent;
import com.sid.cloudynote.client.presenter.Presenter;
import com.sid.cloudynote.client.service.GroupService;
import com.sid.cloudynote.client.service.GroupServiceAsync;
import com.sid.cloudynote.client.sharing.view.FriendView;
import com.sid.cloudynote.client.sharing.view.interfaces.IFriendView;
import com.sid.cloudynote.shared.Group;

public class FriendViewPresenter implements Presenter, IFriendView.Presenter {
	private FriendView view;
	private HandlerManager eventBus;
	
	public FriendViewPresenter(FriendView view, HandlerManager eventBus) {
		super();
		this.view = view;
		this.eventBus = eventBus;
	}

	@Override
	public void showAllFriends() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void findFriends() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loadGroupList() {
		final String email = AppController.get().getLoginInfo().getEmailAddress();
		GroupServiceAsync groupService = GWT.create(GroupService.class);
		groupService.getGroups(email, new AsyncCallback<Set<Group>>(){
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Failed to load group list of user : " + email);
			}

			@Override
			public void onSuccess(Set<Group> result) {
				view.setGroupList(result);
			}
		});
	}

	@Override
	public void showFriendsInGroup() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createGroup() {
		Set<String> members = new HashSet<String>();
		members.add(AppController.get().getLoginInfo().getEmailAddress());
		GroupServiceAsync groupService = GWT.create(GroupService.class);
		groupService.createGroup("My Group", members, new AsyncCallback<Void>(){
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Failed to create group:My Group");
			}

			@Override
			public void onSuccess(Void result) {
				GWT.log("Successfully created group: My Group");
				eventBus.fireEvent(new GroupsChangedEvent());
			}
		});
	}

	@Override
	public void deleteGroup() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void modifyGroup() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void go(HasWidgets container) {
	}
}
