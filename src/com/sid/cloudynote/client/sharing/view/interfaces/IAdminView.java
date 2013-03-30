package com.sid.cloudynote.client.sharing.view.interfaces;

import java.util.List;
import java.util.Map;

import com.google.appengine.api.datastore.Key;
import com.google.gwt.user.client.ui.Widget;
import com.sid.cloudynote.shared.Group;
import com.sid.cloudynote.shared.InfoNote;
import com.sid.cloudynote.shared.Notebook;
import com.sid.cloudynote.shared.User;

public interface IAdminView {
	public interface Presenter {
		void loadGroupList();
		void loadNotebookList();
		void loadUserList(Group group);
		void loadNoteList(Notebook notebook);
		
		void onUserAccessItemSelected(Group group);
		void onNotePermissionItemSelected(Notebook notebook);
		void onUserItemSelected(User user);
		void onNoteItemSelected(InfoNote note);
		
		void saveUserAccessChanges(Group group, Map<Key, Integer> groupAccess, User user, Map<Key, Integer> userAccess);
		void saveNotePermissionChanges(Notebook notebook, Map<Key, Integer> notebookGroupPermission, Map<String,Integer> notebookUserPermission, InfoNote note, Map<Key, Integer> noteGroupPermission, Map<String,Integer> noteUserPermission);
	}

	void setSubListLabel(String label);
	void setGroupList(List<Group> groups);
	void setNotebookList(List<Notebook> notebooks);
	void setUserList(List<User> users);
	void setNoteList(List<InfoNote> notes);
	Widget asWidget();
	void setPresenter(Presenter presenter);
	void presentNotePermission(InfoNote note);
	void presentGroupAccess(Group group);
	void presentNotebookPermission(Notebook notebook);
	void presentUserAccess(User user);
	void setSelectedGroup(Group group);
	void setSelectedUser(User user);
	void setSelectedNotebook(Notebook notebook);
	void setSelectedNote(InfoNote note);
}
