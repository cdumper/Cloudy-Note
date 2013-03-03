package com.sid.cloudynote.client.sharing.view;

import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;
import com.sid.cloudynote.client.service.GroupService;
import com.sid.cloudynote.client.service.GroupServiceAsync;
import com.sid.cloudynote.client.view.Container;
import com.sid.cloudynote.shared.Group;
import com.sid.cloudynote.shared.User;

public class FriendListItem extends ResizeComposite {
	private User user;

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

	public FriendListItem() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public FriendListItem(User user) {
		this();
		this.user = user;
		presentUser();
	}

	private void presentUser() {
		userLink.setText(user.getEmailAddress());
		joinSinceLabel.setText("Member since:1999/9/9");
		totalNotesLabel.setText("Total notes: 43");
		// TODO
		// get the group which the user is in
		if (user.getGroups() != null && user.getGroups().size() != 0) {
			GroupServiceAsync service = GWT.create(GroupService.class);
			service.getGroups(user.getEmailAddress(),
					new AsyncCallback<Set<Group>>() {

						@Override
						public void onFailure(Throwable caught) {
							GWT.log("Failed to read groups of user: "
									+ user.getEmailAddress());
						}

						@Override
						public void onSuccess(Set<Group> result) {
							String group = "";
							if (result != null && result.size() != 0) {
								group = "Group: ";
								for (Group g : result) {
									group += g.getName();
									group += " ";
								}
							}
							groupLabel.setText(group);
						}

					});
		}
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
	Label joinSinceLabel;
	@UiField
	Label totalNotesLabel;
	@UiField
	Label groupLabel;

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
}
