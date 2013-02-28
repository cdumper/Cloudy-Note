package com.sid.cloudynote.client.sharing.presenter;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasWidgets;
import com.sid.cloudynote.client.presenter.Presenter;
import com.sid.cloudynote.client.sharing.view.FriendView;
import com.sid.cloudynote.client.sharing.view.interfaces.IFriendView;

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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showFriendsInGroup() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createGroup() {
		// TODO Auto-generated method stub
		
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
