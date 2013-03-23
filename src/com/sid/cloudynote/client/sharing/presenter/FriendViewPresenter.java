package com.sid.cloudynote.client.sharing.presenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
	public void findFriend(String string) {
		List<User> result = new ArrayList<User>();
		for(User user : view.getAllFriends()){
			if(user.getEmail().contains(string) || user.getNickname().contains(string)){
				result.add(user);
			}
		}
		view.presentFriends(result, false);
	}

	@Override
	public void loadMyGroupList(final String email) {
		GroupServiceAsync groupService = GWT.create(GroupService.class);
		groupService.getMyGroups(email, new AsyncCallback<Set<Group>>() {
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
	public void createGroup(final String groupName, final String owner,
			final Set<String> members) {
		GroupServiceAsync groupService = GWT.create(GroupService.class);
		groupService.createGroup(groupName, owner, members,
				new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
						GWT.log("Failed to create group:" + groupName);
					}

					@Override
					public void onSuccess(Void result) {
						GWT.log("Successfully created group:" + groupName);
						eventBus.fireEvent(new GroupsChangedEvent());
					}
				});
	}

	@Override
	public void deleteGroup(final Group group) {
		GroupServiceAsync service = GWT.create(GroupService.class);
		service.deleteGroup(group, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Failed to delete group: " + group.getName());
			}

			@Override
			public void onSuccess(Void result) {
				GWT.log("Successfully deleted group: " + group.getName());
				eventBus.fireEvent(new GroupsChangedEvent());
			}
		});
	}

	@Override
	public void go(HasWidgets container) {
		container.clear();
		this.loadMyGroupList(AppController.get().getLoginInfo()
				.getEmail());
		this.loadAllFriendsList(AppController.get().getLoginInfo()
				.getEmail());
		container.add(view.asWidget());
	}

	@Override
	public void modifyGroup(Key key, String groupName, Set<String> members) {
		Group group = new Group();
		group.setKey(key);
		group.setOwner(AppController.get().getLoginInfo().getEmail());
		group.setName(groupName);
		group.setMembers(members);
		GroupServiceAsync groupService = GWT.create(GroupService.class);
		groupService.modifyGroup(group, new AsyncCallback<Void>() {
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

	@Override
	public void loadAllFriendsList(final String email) {
		UserServiceAsync userService = GWT.create(UserService.class);
		userService.getFriends(email, new AsyncCallback<List<User>>() {
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Failed to load group list of user : " + email);
			}

			@Override
			public void onSuccess(List<User> friends) {
				view.presentFriends(friends, false);
				view.setAllFriends(friends);
			}
		});
	}

	@Override
	public void showFriendsInGroup(final Key group) {
		GroupServiceAsync groupService = GWT.create(GroupService.class);
		groupService.getUsersInGroup(group, new AsyncCallback<List<User>>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Failed to retrive member list of group with key: "
						+ group);
			}

			@Override
			public void onSuccess(List<User> result) {
				view.presentFriends(result, false);
			}

		});
	}

	@Override
	public void editGroup(final Group currentGroup) {
		GroupServiceAsync groupService = GWT.create(GroupService.class);
		groupService.getUsersInGroup(currentGroup.getKey(),
				new AsyncCallback<List<User>>() {

					@Override
					public void onFailure(Throwable caught) {
						GWT.log("Failed to retrieve user list of group: "
								+ currentGroup.getName());
					}

					@Override
					public void onSuccess(List<User> result) {
						Map<User, Boolean> map = new HashMap<User, Boolean>();
						for (User u : result) {
							map.put(u, true);
						}
						for (User u : view.getAllFriends()) {
							boolean exist = false; 
							for (Entry<User, Boolean> m : map.entrySet()) {
								if(m.getKey().getEmail().equals(u.getEmail())) {
									exist = true;
								}
							}
							if (!exist) {
								map.put(u, false);
							}
						}
						view.presentFriends(map);
						view.changeEditingMode();
					}
				});
	}

}
