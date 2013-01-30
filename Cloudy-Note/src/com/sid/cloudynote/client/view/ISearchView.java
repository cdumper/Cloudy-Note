package com.sid.cloudynote.client.view;

import com.google.gwt.user.client.ui.Widget;

public interface ISearchView {
	public interface Presenter{
		void onNewNotebookButtonClicked();
		void onNewNoteButtonClicked();
		void onSearchMade();
		void onEditDoneButtonClicked();
	}
	void setPresenter(Presenter presenter);
	Widget asWidget();
}
