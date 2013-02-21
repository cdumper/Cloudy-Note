package com.sid.cloudynote.client.view.interfaces;

import com.google.gwt.user.client.ui.Widget;

public interface ISearchView {
	public interface Presenter{
		void onNewNotebookButtonClicked();
		void onNewNoteButtonClicked();
		void onSearchMade();
		void onClickEditDoneButton();
		void changeButtonText();
	}
	void setPresenter(Presenter presenter);
	Widget asWidget();
}
