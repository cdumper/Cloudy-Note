package com.sid.cloudynote.client.sharing.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.sid.cloudynote.client.AppController;
import com.sid.cloudynote.client.event.GroupsChangedEvent;
import com.sid.cloudynote.client.event.interfaces.IGroupsChangedHandler;
import com.sid.cloudynote.client.sharing.view.interfaces.IFriendView;
import com.sid.cloudynote.client.view.Container;
import com.sid.cloudynote.shared.Group;
import com.sid.cloudynote.shared.User;

public class FriendView extends ResizeComposite implements IFriendView,
		IGroupsChangedHandler {
	private Presenter presenter;
	private List<User> allFriends = new ArrayList<User>();
	private Set<FriendListItem> friendsItemSet;
	private boolean isEditing = false;
	private Group currentGroup;

	public interface Style extends CssResource {
		String hidden();

		String visible();

		String button();
	}

	private static FriendViewUiBinder uiBinder = GWT
			.create(FriendViewUiBinder.class);
	@UiField
	Container container;
	@UiField
	DockLayoutPanel content;
	@UiField
	FlowPanel leftPanel;
	@UiField
	HTMLPanel friendsButtonListPanel;
	@UiField
	Button allFriendsButton;
	@UiField
	Button findFriendsButton;
	@UiField
	SuggestBox searchBox;
	@UiField
	HTMLPanel groupsPanel;
	@UiField
	HTMLPanel centerPanel;
	@UiField
	HTMLPanel groupsButtonPanel;
	@UiField
	Button createGroupButton;
	@UiField
	Button editGroupButton;
	@UiField
	Button deleteGroupButton;
	@UiField
	HTMLPanel namePanel;
	@UiField
	TextBox groupNameBox;
	@UiField
	HTMLPanel friendsListPanel;
	@UiField
	public Style style;

	interface FriendViewUiBinder extends UiBinder<Widget, FriendView> {
	}

	public FriendView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public List<User> getAllFriends() {
		return allFriends;
	}

	public void setAllFriends(List<User> allFriends) {
		this.allFriends = allFriends;
	}

	public boolean isEditing() {
		return isEditing;
	}

	public void setEditing(boolean isEditing) {
		this.isEditing = isEditing;
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public Widget asWidget() {
		return this.content;
	}

	public Container getContainer() {
		return this.container;
	}

	@UiHandler("createGroupButton")
	void onCreateGroupButtonClicked(ClickEvent e) {
		if (!isEditing) {
			currentGroup = null;
			this.groupNameBox.setText("New Group");
			this.presentFriends(allFriends,false);
		} else {
			// gather the group name and members list for storage
			String groupName = this.groupNameBox.getText();
			String userEmail = AppController.get().getLoginInfo()
					.getEmail();
			Set<String> members = new HashSet<String>();
			for (FriendListItem item : friendsItemSet) {
				if (item.getSelected()) {
					members.add(item.getUser().getEmail());
				}
			}

			// either create a new group if the variable currentGroup is null or
			// modify the current one
			if (currentGroup == null) {
				presenter.createGroup(groupName, userEmail, members);
			} else {
				presenter.modifyGroup(currentGroup.getKey(), groupName, members);
			}
		}
		changeEditingMode();
	}

	@UiHandler("editGroupButton")
	void onEditGroup(ClickEvent e) {
		this.groupNameBox.setText(this.currentGroup.getName());
		presenter.editGroup(this.currentGroup);
	}
	
	@UiHandler("deleteGroupButton")
	void onDeleteGroup(ClickEvent e) {
		presenter.deleteGroup(this.currentGroup);
	}

	@UiHandler("allFriendsButton")
	void onAllFriends(ClickEvent e) {
		this.currentGroup = null;
		this.presentFriends(allFriends,false);
	}

	//switch between editing mode & non-editing mode
	public void changeEditingMode() {
		String style;
		if (this.isEditing) {
			style = this.style.hidden();
			this.createGroupButton.setText("Create Group");
			this.isEditing = false;
		} else {
			style = this.style.visible();
			this.createGroupButton.setText("Done");
			this.isEditing = true;
		}
		namePanel.setStyleName(style);
		for (FriendListItem item : friendsItemSet) {
			item.checkBox.setStyleName(style);
		}
	}

	@Override
	public void onGroupsChanged(GroupsChangedEvent event) {
		presenter.loadMyGroupList(AppController.get().getLoginInfo().getEmail());
		this.presentFriends(allFriends,false);
	}

	public void setGroupList(Set<Group> result) {
		this.groupsPanel.clear();
		for (final Group group : result) {
			Button button = new Button(group.getName(), new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					currentGroup = group;
					presenter.showFriendsInGroup(group.getKey());
				}
			});
			button.addStyleName(style.button());
			this.groupsPanel.add(button);
		}
	}
	
	public void presentFriends(List<User> users, Boolean checked) {
		Map<User,Boolean> map = new HashMap<User, Boolean>();
		for(User u : users){
			map.put(u, checked);
		}
		this.presentFriends(map);
	}

	public void presentFriends(Map<User,Boolean> users) {
		if (friendsItemSet == null) {
			friendsItemSet = new HashSet<FriendListItem>();
		}
		if (users != null) {
			friendsItemSet.clear();
			this.friendsListPanel.clear();
			
			for (Entry<User,Boolean> user : users.entrySet()) {
				FriendListItem item = new FriendListItem(user.getKey(),user.getValue());
				friendsItemSet.add(item);
				this.friendsListPanel.add(item);
			}
		}
	}
	
	public void setAllFriendsList (List<User> friends) {
		this.allFriends = friends;
	}
}
