package com.sid.cloudynote.client.sharing.view.interfaces;

import java.util.Set;

import com.google.appengine.api.datastore.Key;
import com.google.gwt.user.client.ui.Widget;

public interface IFriendView {
	public interface Presenter {
		void showAllFriends();

		void findFriends();

		void loadGroupList();

		void showFriendsInGroup();

		void createGroup(String groupName, String owner, Set<String> members);

		void deleteGroup();

		void modifyGroup(Key key, String groupName, Set<String> members);
	}

	void setPresenter(Presenter presenter);

	Widget asWidget();
}
