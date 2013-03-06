package com.sid.cloudynote.client.view.interfaces;

import com.google.gwt.user.client.ui.Widget;
import com.sid.cloudynote.shared.InfoNote;

public interface INoteView {
	public interface Presenter {
		void presentNote(InfoNote note);
		void editNote(InfoNote note);
		void showNewNote();
	}
	
	void setPresenter(Presenter presenter);
	Widget asWidget();
}
