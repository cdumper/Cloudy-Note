package com.sid.cloudynote.client.sharing.view;

import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.Key;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sid.cloudynote.client.DataManager;
import com.sid.cloudynote.client.event.GroupsChangedEvent;
import com.sid.cloudynote.client.service.UserService;
import com.sid.cloudynote.client.service.UserServiceAsync;
import com.sid.cloudynote.client.view.Container;
import com.sid.cloudynote.shared.Group;
import com.sid.cloudynote.shared.User;

public class FriendListItem extends ResizeComposite implements Comparable<FriendListItem> {
	private HandlerManager eventBus;
	private User user;
	private List<Group> groupList = new ArrayList<Group>();
	final DecoratedPopupPanel popup = new DecoratedPopupPanel();
	private boolean active = false;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	private static FriendListItemUiBinder uiBinder = GWT
			.create(FriendListItemUiBinder.class);

	interface FriendListItemUiBinder extends UiBinder<Widget, FriendListItem> {
	}
	
	public interface Style extends CssResource {
		String select();
	}

	public FriendListItem() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public FriendListItem(User user, Boolean selected, HandlerManager eventBus) {
		this();
		this.user = user;
		this.checkBox.setValue(selected);
		this.eventBus = eventBus;
		presentUser();
		populateGroupList();
	}

	private void presentUser() {
		userLink.setText(user.getEmail());
		joinSinceLabel.setText("Friend since:1999/9/9");
		totalNotesLabel.setText("Total notes: 42");
		for (Group group : DataManager.getMyGroups().values()) {
			if (group.getMembers().contains(user.getEmail())) {
				groupList.add(group);
			}
		}
		presentGroups();
	}

	@UiField
	Container container;
	@UiField
	HTMLPanel content;
	@UiField
	CheckBox checkBox;
	@UiField
	Button userLink;
	@UiField
	ListBox groups;
	@UiField
	Anchor modifyGroups;
	@UiField
	Label joinSinceLabel;
	@UiField
	Label totalNotesLabel;
	@UiField
	Label groupLabel;
	@UiField
	public Style style;

	@UiHandler("modifyGroups")
	void onClickModifyGroups(ClickEvent e) {
		if (!active) {
			popup.showRelativeTo(modifyGroups);
			active = true;
			modifyGroups.addStyleName(style.select());
		} else {
			popup.hide();
			active = false;
			modifyGroups.removeStyleName(style.select());
		}
	}

	private void populateGroupList() {
		VerticalPanel content = new VerticalPanel();
		popup.setWidget(content);
		popup.setAutoHideEnabled(true);
		/**
		 * Persist the changes when the pop-up panel is closed
		 */
		popup.addCloseHandler(new CloseHandler<PopupPanel>(){

			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
//				if(event.isAutoClosed()){
//					active = !active;
//				}
				List<Key> groups = new ArrayList<Key>();
				for(Group group : groupList) {
					groups.add(group.getKey());
				}
				UserServiceAsync userService = GWT.create(UserService.class);
				userService.addUserToGroups(user.getEmail(), groups, new AsyncCallback<Void>(){

					@Override
					public void onFailure(Throwable caught) {
						GWT.log("Failed to modify groups of user:"+user.getEmail());
					}

					@Override
					public void onSuccess(Void result) {
						GWT.log("Successfully modified groups of user:"+user.getEmail());
						eventBus.fireEvent(new GroupsChangedEvent());
					}
				});
			}
		});
		
		for (final Group group : DataManager.getMyGroups().values()) {
			CheckBox cb = new CheckBox(group.getName());
			if(groupList.contains(group)){
				cb.setValue(true);
			} else {
				cb.setValue(false);
			}
			content.add(cb);
			cb.addValueChangeHandler(new ValueChangeHandler<Boolean>(){

				@Override
				public void onValueChange(ValueChangeEvent<Boolean> event) {
					if(event.getValue()){
						groupList.add(group);
					} else {
						groupList.remove(group);
					}
					presentGroups();
				}
			});
		}
		
		this.groups.clear();
		this.groups.addItem("Modify Groups");
		this.groups.addFocusHandler(new FocusHandler() {
			@Override
			public void onFocus(FocusEvent event) {
				((ListBox) event.getSource()).setFocus(false);
				if (!active) {
					popup.showRelativeTo(groups);
					active = true;
				} else {
					popup.hide();
					active = false;
				}
			}
		});
	}
	
	private void presentGroups() {
		if (groupList.size() != 0) {
			String group = "Group: ";
			for (int i = 0; i < groupList.size(); i++) {
				group += groupList.get(i).getName();
				if (i != groupList.size() - 1)
					group += ", ";
			}
			groupLabel.setText(group);
		} else {
			groupLabel.setText(" ");
		}
	}
	
	public Container getContainer() {
		return this.container;
	}

	@Override
	public Widget asWidget() {
		return this.content;
	}

	public boolean getSelected() {
		return this.checkBox.getValue();
	}

	@Override
	public int compareTo(FriendListItem item) {
		return this.getUser().getEmail().compareToIgnoreCase(item.getUser().getEmail());
	}
}
