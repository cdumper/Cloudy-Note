package com.sid.cloudynote.client.sharing.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.Widget;
import com.sid.cloudynote.client.sharing.view.interfaces.IFriendView;
import com.sid.cloudynote.client.view.Container;

public class FriendView extends ResizeComposite implements IFriendView{
	private Presenter presenter;
	private static FriendViewUiBinder uiBinder = GWT
			.create(FriendViewUiBinder.class);
	@UiField Container container;
	@UiField DockLayoutPanel content;
	@UiField FlowPanel leftPanel;
	@UiField HTMLPanel friendsButtonListPanel;
	@UiField Button allFriendsButton;
	@UiField Button findFriendsButton;
	@UiField SuggestBox searchBox;
	@UiField HTMLPanel groupsPanel;
	@UiField HTMLPanel centerPanel;
	@UiField HTMLPanel groupsButtonPanel;
	@UiField Button createGroupButton;
	@UiField Button editGroupButton;
	@UiField Button deleteGroupButton;
	@UiField HTMLPanel friendsListPanel;

	interface FriendViewUiBinder extends UiBinder<Widget, FriendView> {
	}

	public FriendView() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	@Override
	public void setPresenter (Presenter presenter) {
		this.presenter = presenter;
	}
	
	@Override
	public Widget asWidget(){
		return this.content;
	}
	
	public Container getContainer () {
		return this.container;
	}
	
}
