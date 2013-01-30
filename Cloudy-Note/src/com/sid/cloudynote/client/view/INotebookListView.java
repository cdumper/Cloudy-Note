package com.sid.cloudynote.client.view;

import java.util.List;

import com.google.gwt.user.client.ui.Widget;
import com.sid.cloudynote.shared.Notebook;

public interface INotebookListView {
	public interface Presenter {
		void onNotebookItemSelected(Notebook clickedItem);
		void onNotebookItemRightClicked(Notebook clickedItem);
		void loadNotebookList();
	}
	
	void setPresenter(Presenter presenter);
	void setNotebookList(List<Notebook> notebooks);
	Widget asWidget();
}
