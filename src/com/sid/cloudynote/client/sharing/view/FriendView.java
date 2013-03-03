package com.sid.cloudynote.client.sharing.view;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
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
	private Set<FriendListItem> friendsItemSet;
	private boolean isEditing =false;
	private Group currentGroup;
	interface Style extends CssResource{
		String hidden();
		String visible();
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
	Style style;

	interface FriendViewUiBinder extends UiBinder<Widget, FriendView> {
	}

	public FriendView() {
		initWidget(uiBinder.createAndBindUi(this));
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
			switchStyle(style.visible());
			this.createGroupButton.setText("Done");
			isEditing = true;
		} else {
			// gather the group name and members list for storage 
			String groupName = this.groupNameBox.getText();
			String userEmail = AppController.get().getLoginInfo().getEmailAddress();
			Set<String> members = new HashSet<String>();
			for(FriendListItem item :friendsItemSet){
				if(item.getSelected()){
					members.add(item.getUser().getEmailAddress());
				}
			}
			
			//either create a new group if the variable currentGroup is null or modify the current one 
			if (currentGroup == null) {
				presenter.createGroup(groupName,userEmail,members);
			} else {
				presenter.modifyGroup(currentGroup.getKey(),groupName,members);
			}
			
			switchStyle(style.hidden());
			this.createGroupButton.setText("Create Group");
			isEditing = false;
		}
	}
	
	@UiHandler("editGroupButton")
	void onEditGroup(ClickEvent e){
	}
	
	void switchStyle(String style){
		namePanel.setStyleName(style);
		for(FriendListItem item :friendsItemSet){
			item.checkBox.setStyleName(style);
		}
	}

	@Override
	public void onGroupsChanged(GroupsChangedEvent event) {
		presenter.loadGroupList();
	}

	public void setGroupList(Set<Group> result) {
		// TODO Auto-generated method stub
		System.out.println("set group list in friend page:" + result.size());
	}

	public void setFriendsList(List<User> friends) {
		if (friendsItemSet == null) {
			friendsItemSet = new HashSet<FriendListItem>();
		}
		if (friends != null) {
			friendsItemSet.clear();
			this.friendsListPanel.clear();
			for (User user : friends) {
				FriendListItem item = new FriendListItem(user);
				friendsItemSet.add(item);
				this.friendsListPanel.add(item);
			}
		}
	}
}
