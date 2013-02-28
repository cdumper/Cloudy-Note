package com.sid.cloudynote.client.sharing.view.interfaces;

import com.google.gwt.user.client.ui.Widget;

public interface IFriendView {
	public interface Presenter {
		void showAllFriends();

		void findFriends();

		void loadGroupList();

		void showFriendsInGroup();

		void createGroup();

		void deleteGroup();

		void modifyGroup();
	}

	void setPresenter(Presenter presenter);

	Widget asWidget();
}
