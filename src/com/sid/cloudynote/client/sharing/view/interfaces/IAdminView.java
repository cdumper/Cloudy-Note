package com.sid.cloudynote.client.sharing.view.interfaces;

import java.util.List;

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
	}

	void setLabel(String label);
	void setGroupList(List<Group> groups);
	void setNotebookList(List<Notebook> notebooks);
	void setUserList(List<User> users);
	void setNoteList(List<InfoNote> notes);
	Widget asWidget();
	void setPresenter(Presenter presenter);
}
