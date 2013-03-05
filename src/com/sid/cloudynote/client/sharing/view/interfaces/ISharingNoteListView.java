package com.sid.cloudynote.client.sharing.view.interfaces;

import java.util.List;

import com.google.gwt.user.client.ui.Widget;
import com.sid.cloudynote.shared.Group;
import com.sid.cloudynote.shared.InfoNote;

public interface ISharingNoteListView {
	public interface Presenter {
		void onNoteItemSelected(InfoNote clickedItem);
		void loadPublicNoteList();
		void loadSharedNoteList(String id);
		void loadNotesInGroup(Group group);
		void startEditing(InfoNote infoNote);
		void viewNote(InfoNote note);
	}
	
	void setPresenter(Presenter presenter);
	void setNoteList(List<InfoNote> notes);
	Widget asWidget();
}
