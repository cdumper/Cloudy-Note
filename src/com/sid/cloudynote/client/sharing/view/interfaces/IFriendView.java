package com.sid.cloudynote.client.sharing.view.interfaces;

import java.util.List;

import com.google.appengine.api.datastore.Key;
import com.google.gwt.user.client.ui.Widget;
import com.sid.cloudynote.shared.Group;

public interface IFriendView {
	public interface Presenter {
		/**
		 * The method to search friend with typed search text
		 * It will try to match the text with email AND nickname 
		 * @param string
		 */
		void SearchFriend(String email);

		/**
		 * Add a user a friend
		 * @param email
		 * @return
		 */
		void addFriend(String email);

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
		void createGroup(String groupName, String owner, List<String> members);

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
		void modifyGroup(Key key, String groupName, List<String> members);

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

		void inviteUser(String text);

	}

	void setPresenter(Presenter presenter);

	Widget asWidget();
}
