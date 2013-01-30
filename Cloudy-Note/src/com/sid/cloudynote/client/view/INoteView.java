package com.sid.cloudynote.client.view;

import com.google.gwt.user.client.ui.Widget;

public interface INoteView {
	public interface Presenter {
		void saveNote();
		void stopEdit();
		void startEdit();
		boolean isEditing();
		void setEditing(boolean isEditing);
		void setView(Widget view);
	}
	
	void setPresenter(Presenter presenter);
	Widget asWidget();
}
