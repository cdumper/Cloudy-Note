package com.sid.cloudynote.client.view.interfaces;

import java.util.List;

import com.google.gwt.user.client.ui.Widget;
import com.sid.cloudynote.shared.InfoNote;
import com.sid.cloudynote.shared.Notebook;

public interface INoteListView {
	public interface Presenter {
		void onNoteItemSelected(InfoNote clickedItem);
		void onNoteItemRightClicked(InfoNote clickedItem);
		void loadNoteList(Notebook notebook);
		void startEditing(InfoNote infoNote);
		void shareNoteToUser(String text, InfoNote note, String itemText);
		void makeNotesPublic(List<InfoNote> notes);
	}
	
	void setPresenter(Presenter presenter);
	void setNoteList(List<InfoNote> notes);
	Widget asWidget();
}
