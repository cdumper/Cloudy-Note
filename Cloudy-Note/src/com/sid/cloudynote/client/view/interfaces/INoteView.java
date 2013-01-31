package com.sid.cloudynote.client.view.interfaces;

import com.google.gwt.user.client.ui.Widget;
import com.sid.cloudynote.shared.InfoNote;

public interface INoteView {
	public interface Presenter {
		void saveNote();
		void stopEdit();
		void startEdit();
		boolean isEditing();
		void setEditing(boolean isEditing);
		void setView(Widget view);
		void createNewNote(InfoNote note);
		void updateNote(InfoNote note);
		void setNewNote(boolean b);
	}
	
	void setPresenter(Presenter presenter);
	Widget asWidget();
}
