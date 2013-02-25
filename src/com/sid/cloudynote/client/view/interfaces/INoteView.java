package com.sid.cloudynote.client.view.interfaces;

import com.google.gwt.user.client.ui.Widget;
import com.sid.cloudynote.shared.InfoNote;

public interface INoteView {
	public interface Presenter {
		void saveNote();
		void stopEdit();
		boolean isEditing();
		void setEditing(boolean isEditing);
		void setView(Widget view);
		void createNewNote(InfoNote note);
		void updateNote(InfoNote note);
		void setNewNote(boolean b);
		void presentNote();
		void startEdit(InfoNote note);
	}
	
	void setPresenter(Presenter presenter);
	Widget asWidget();
}
