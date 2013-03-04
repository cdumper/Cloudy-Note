package com.sid.cloudynote.client.sharing.view.interfaces;

import java.util.Set;

import com.google.appengine.api.datastore.Key;
import com.google.gwt.user.client.ui.Widget;
import com.sid.cloudynote.shared.Group;

public interface IFriendView {
	public interface Presenter {
		//TODO
		void findFriends();

		/**
		 * Retrieve the group list of user with given email
		 * Note: The returned group list ONLY including the groups the user owns(created)
		 * @param email
		 */
		void loadMyGroupList(String email);

		/**
		 * Create a new group with given groupName, owner and members information
		 * @param groupName
		 * @param owner
		 * @param members
		 */
		void createGroup(String groupName, String owner, Set<String> members);

		/**
		 * Delete the given group
		 */
		void deleteGroup(Group group);

		/**
		 * Modify the existing group with given key using the given information
		 * @param key
		 * @param groupName
		 * @param members
		 */
		void modifyGroup(Key key, String groupName, Set<String> members);

		/**
		 * Load and present the friends list of user with given email
		 * @param email
		 */
		void loadAllFriendsList(String email);

		/**
		 * Retrieve the member list of given group and present in the view in non-editing mode
		 * @param group
		 */
		void showFriendsInGroup(Key group);

		/**
		 * Retrieve and show the user list of given group.
		 * Then switch the view to editing mode,which means each friendListItem starts with a checkbox
		 * @param currentGroup
		 */
		void editGroup(Group currentGroup);
	}

	void setPresenter(Presenter presenter);

	Widget asWidget();
}
