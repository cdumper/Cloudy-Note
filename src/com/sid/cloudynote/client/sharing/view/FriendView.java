package com.sid.cloudynote.client.sharing.view;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.sid.cloudynote.client.AppController;
import com.sid.cloudynote.client.DataManager;
import com.sid.cloudynote.client.event.GroupsChangedEvent;
import com.sid.cloudynote.client.event.interfaces.IGroupsChangedHandler;
import com.sid.cloudynote.client.service.UserService;
import com.sid.cloudynote.client.service.UserServiceAsync;
import com.sid.cloudynote.client.sharing.view.interfaces.IFriendView;
import com.sid.cloudynote.client.view.Container;
import com.sid.cloudynote.shared.Group;
import com.sid.cloudynote.shared.User;

public class FriendView extends ResizeComposite implements IFriendView,
		IGroupsChangedHandler {
	private final String SEARCHBOX_TEXT = "Search Friends...";
	private Presenter presenter;
	private List<FriendListItem> friendsItemList;
	private boolean isEditing = false;
	private Group currentGroup;
	@UiField
	Style style;

	public interface Style extends CssResource {
		String rowDiv();

		String hidden();

		String visible();

		String button();
	};

	public interface Resources extends ClientBundle {
		@Source("../../resources/images/allusers.png")
		ImageResource allUser();

		@Source("../../resources/images/search.png")
		ImageResource findUser();

		@Source("../../resources/images/group.png")
		ImageResource group();

		@Source("../../resources/images/user-icon.png")
		ImageResource userIcon();

		@Source("../../resources/images/plus.png")
		ImageResource plus();
		
		@Source("../../resources/images/minus.png")
		ImageResource minus();
	}

	private static FriendViewUiBinder uiBinder = GWT
			.create(FriendViewUiBinder.class);
	@UiField
	Container container;
	@UiField
	DockLayoutPanel content;
	@UiField
	HTMLPanel leftPanel;
	@UiField
	HTMLPanel friendsPanel;
	@UiField
	TextBox searchBox;
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
	public Resources res;

	interface FriendViewUiBinder extends UiBinder<Widget, FriendView> {
	}

	public FriendView() {
		initWidget(uiBinder.createAndBindUi(this));
		initialFriendsPanel();
		this.searchBox.setText(SEARCHBOX_TEXT);

		this.searchBox.addFocusHandler(new FocusHandler() {
			@Override
			public void onFocus(FocusEvent event) {
				if (SEARCHBOX_TEXT.equals(searchBox.getText()))
					searchBox.setText("");
			}
		});

		this.searchBox.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				if (searchBox.getText().trim().equals(""))
					searchBox.setText(SEARCHBOX_TEXT);
			}

		});

		this.searchBox.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				presenter.SearchFriend(searchBox.getText());
			}
		});
	}

	private void initialFriendsPanel() {
		this.friendsPanel.clear();
		Element allFriendsDiv = DOM.createDiv();
		DOM.sinkEvents(allFriendsDiv, Event.ONCLICK);
		DOM.setEventListener(allFriendsDiv, new EventListener() {
			public void onBrowserEvent(Event event) {
				currentGroup = null;
				presentFriends(new ArrayList<User>(DataManager.getAllFriends()
						.values()), false);
			}
		});
		allFriendsDiv.setClassName(style.rowDiv());
		allFriendsDiv.setInnerHTML(AbstractImagePrototype.create(res.allUser())
				.getHTML() + "All Firends");

		Element findFriendsDiv = DOM.createDiv();
		DOM.sinkEvents(findFriendsDiv, Event.ONCLICK);
		DOM.setEventListener(findFriendsDiv, new EventListener() {
			public void onBrowserEvent(Event event) {
				currentGroup = null;
				showFindFriendsDialog();
			}
		});
		findFriendsDiv.setClassName(style.rowDiv());
		findFriendsDiv.setInnerHTML(AbstractImagePrototype.create(
				res.findUser()).getHTML()
				+ "Find Firend");

		this.friendsPanel.getElement().appendChild(allFriendsDiv);
		this.friendsPanel.getElement().appendChild(findFriendsDiv);
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
			this.presentFriends(new ArrayList<User>(DataManager.getAllFriends()
					.values()), false);
		} else {
			// gather the group name and members list for storage
			String groupName = this.groupNameBox.getText();
			String userEmail = AppController.get().getLoginInfo().getEmail();
			List<String> members = new ArrayList<String>();
			for (FriendListItem item : friendsItemList) {
				if (item.getSelected()) {
					members.add(item.getUser().getEmail());
				}
			}

			// either create a new group if the variable currentGroup is null or
			// modify the current one
			if (currentGroup == null) {
				presenter.createGroup(groupName, userEmail, members);
			} else {
				presenter
						.modifyGroup(currentGroup.getKey(), groupName, members);
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

	private void showFindFriendsDialog() {
		final DialogBox dialog = new DialogBox();
		dialog.setText("Find Friend");
		dialog.setAnimationEnabled(true);
		dialog.setGlassEnabled(true);
		HTMLPanel content = new HTMLPanel("");
		content.setWidth("300px");
		dialog.setWidget(content);

		Label label = new Label("Please enter the user name or email address:");
		final TextBox email = new TextBox();
		email.setWidth("250px");
		final HorizontalPanel userListPanel = new HorizontalPanel();
		HorizontalPanel buttonPanel = new HorizontalPanel();
		Button submit = new Button("Submit", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				UserServiceAsync service = GWT.create(UserService.class);
				service.findUser(email.getText(), 5,
						new AsyncCallback<List<User>>() {

							@Override
							public void onFailure(Throwable caught) {
								GWT.log("Failed to find user");
							}

							@Override
							public void onSuccess(List<User> result) {
								userListPanel.clear();
								if (result.size() == 0) {
									userListPanel.add(new Label(
											"Sorry, no users were found"));
									userListPanel.add(new Button("Invite",new ClickHandler(){

										@Override
										public void onClick(ClickEvent event) {
											presenter.inviteUser(email.getText());
											((Button)event.getSource()).setEnabled(false);
										}
										
									}));
								}
								for (final User user : result) {
									final HorizontalPanel userItem = new HorizontalPanel();
									userListPanel.add(userItem);
									Image image = new Image();
									image.setSize("32px", "32px");
									if (user.getProfileImageUrl() == null) {
										image.setResource(res.userIcon());
									} else {
										image.setUrl(user.getProfileImageUrl());
									}
									userItem.add(image);
									
									Label anchor = new Label();
									anchor.setText(user.getNickname()+"("+user.getEmail()+")");
									anchor.addClickHandler(new ClickHandler(){

										@Override
										public void onClick(ClickEvent event) {
											AppController.get().viewUserProfile(user.getEmail());
											dialog.hide();
										}
										
									});
									userItem.add(anchor);
									
									final Image plus = new Image(res.plus());
									final Image minus = new Image(res.minus());
									plus.addClickHandler(new ClickHandler() {
										@Override
										public void onClick(ClickEvent event) {
											presenter.addFriend(user
													.getEmail());
											userItem.remove(plus);
											userItem.add(minus);
										}
									});
									
									
									if (!AppController.get().getLoginInfo()
											.getFriends()
											.containsKey(user.getEmail())) {
										userItem.add(plus);
									} else {
										userItem.add(minus);
									}
								}
							}
						});
			}
		});

		Button cancel = new Button("Cancel", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				dialog.hide();
			}

		});
		buttonPanel.add(cancel);
		buttonPanel.add(submit);

		content.add(label);
		content.add(email);
		content.add(userListPanel);
		content.add(buttonPanel);

		dialog.center();
	}

	// switch between editing mode & non-editing mode
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
		for (FriendListItem item : friendsItemList) {
			item.checkBox.setStyleName(style);
		}
	}

	@Override
	public void onGroupsChanged(GroupsChangedEvent event) {
		presenter
				.loadMyGroupList(AppController.get().getLoginInfo().getEmail());
		presenter.loadAllFriendsList(AppController.get().getLoginInfo()
				.getEmail());
		this.presentFriends(new ArrayList<User>(DataManager.getAllFriends()
				.values()), false);
	}

	public void setGroupList(List<Group> result) {
		while (this.groupsPanel.getElement().hasChildNodes()) {
			this.groupsPanel.getElement().removeChild(
					this.groupsPanel.getElement().getFirstChild());
		}
		for (final Group group : result) {
			Element div = DOM.createDiv();
			DOM.sinkEvents(div, Event.ONCLICK);
			DOM.setEventListener(div, new EventListener() {
				public void onBrowserEvent(Event event) {
					currentGroup = group;
					presenter.showFriendsInGroup(group.getKey());
				}
			});
			div.setClassName(style.rowDiv());
			div.setInnerHTML(AbstractImagePrototype.create(res.group())
					.getHTML()
					+ group.getName()
					+ " ("
					+ group.getMembers().size() + ")");
			this.groupsPanel.getElement().appendChild(div);
		}
	}

	public void presentFriends(List<User> users, Boolean checked) {
		Map<User, Boolean> map = new LinkedHashMap<User, Boolean>();
		for (User u : users) {
			map.put(u, checked);
		}
		this.presentFriends(map);
	}

	public void presentFriends(Map<User, Boolean> users) {
		if (friendsItemList == null) {
			friendsItemList = new ArrayList<FriendListItem>();
		}
		if (users != null) {
			friendsItemList.clear();
			this.friendsListPanel.clear();

			for (Entry<User, Boolean> user : users.entrySet()) {
				FriendListItem item = new FriendListItem(user.getKey(),
						user.getValue(), AppController.get().getEventBus());
				friendsItemList.add(item);
				this.friendsListPanel.add(item);
			}
		}
	}
}
