package com.sid.cloudynote.client.sharing.presenter;

import java.util.List;
import java.util.Set;

import com.google.appengine.api.datastore.Key;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.sid.cloudynote.client.AppController;
import com.sid.cloudynote.client.event.GroupsChangedEvent;
import com.sid.cloudynote.client.presenter.Presenter;
import com.sid.cloudynote.client.service.GroupService;
import com.sid.cloudynote.client.service.GroupServiceAsync;
import com.sid.cloudynote.client.service.UserService;
import com.sid.cloudynote.client.service.UserServiceAsync;
import com.sid.cloudynote.client.sharing.view.FriendView;
import com.sid.cloudynote.client.sharing.view.interfaces.IFriendView;
import com.sid.cloudynote.shared.Group;
import com.sid.cloudynote.shared.User;

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
	public void createGroup(final String groupName, final String owner, final Set<String> members) {
		GroupServiceAsync groupService = GWT.create(GroupService.class);
		groupService.createGroup(groupName, owner, members, new AsyncCallback<Void>(){
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Failed to create group:"+groupName);
			}

			@Override
			public void onSuccess(Void result) {
				GWT.log("Successfully created group:"+groupName);
				eventBus.fireEvent(new GroupsChangedEvent());
			}
		});
	}

	@Override
	public void deleteGroup() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void go(HasWidgets container) {
		container.clear();
		this.loadGroupList();
		this.loadFriendsList();
		container.add(view.asWidget());
	}

	private void loadFriendsList() {
		final String email = AppController.get().getLoginInfo().getEmailAddress();
		UserServiceAsync userService = GWT.create(UserService.class);
		userService.getFriends(email, new AsyncCallback<List<User>>(){
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Failed to load group list of user : " + email);
			}

			@Override
			public void onSuccess(List<User> friends) {
				view.setFriendsList(friends);
			}
		});
	}

	@Override
	public void modifyGroup(Key key, String groupName, Set<String> members) {
		Group group = new Group();
		group.setKey(key);
		group.setName(groupName);
		group.setMembers(members);
		GroupServiceAsync groupService = GWT.create(GroupService.class);
		groupService.modifyGroup(group, new AsyncCallback<Void>(){
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Failed to create group:My Group");
			}

			@Override
			public void onSuccess(Void result) {
				GWT.log("Successfully modified group");
				eventBus.fireEvent(new GroupsChangedEvent());
			}
		});
	}
}
