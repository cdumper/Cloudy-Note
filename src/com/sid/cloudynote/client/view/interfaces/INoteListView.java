package com.sid.cloudynote.client.view.interfaces;

import java.util.List;
import java.util.Map;

import com.google.appengine.api.datastore.Key;
import com.google.gwt.user.client.ui.Widget;
import com.sid.cloudynote.shared.InfoNote;
import com.sid.cloudynote.shared.Notebook;

public interface INoteListView {
	public interface Presenter {
		void onNoteItemSelected(InfoNote clickedItem);

		void loadNoteList(Notebook notebook);

		void startEditing(InfoNote infoNote);

		void makeNotesPublic(List<InfoNote> notes);

		void deleteNote(InfoNote note);

		void loadGroupList(String email);

		void loadFriendsList(String email);

		void shareNoteToGroups(InfoNote note, Map<Key, Integer> groupsAccess);

		void shareNoteToUsers(InfoNote note, Map<String, Integer> access);

		void shareNoteToUsersAndGroups(InfoNote note,
				Map<String, Integer> userAccess, Map<Key, Integer> groupAccess);

	}

	void setPresenter(Presenter presenter);

	void setNoteList(List<InfoNote> notes);

	Widget asWidget();
}
