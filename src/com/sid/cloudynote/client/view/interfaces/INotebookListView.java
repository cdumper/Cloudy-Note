package com.sid.cloudynote.client.view.interfaces;

import java.util.List;

import com.google.gwt.user.client.ui.Widget;
import com.sid.cloudynote.shared.Notebook;
import com.sid.cloudynote.shared.Tag;

public interface INotebookListView {
	public interface Presenter {
		void onNotebookItemSelected(Notebook clickedItem);
		void loadNotebookList();
		void loadTagList();
		void createNewNotebook(String text);
		void createNewTag(String text);
		void onNewNotebookButtonClicked();
		void onNewNoteButtonClicked();
		void renameTag(Tag tag, String text);
		void deleteTag(Tag tag);
		void loadNotesByTag(Tag tag);
		void onNewTagButtonClicked();
		void deleteNotebook(Notebook selectedNotebook);
	}
	
	void setPresenter(Presenter presenter);
	void setNotebookList(List<Notebook> notebooks);
	void setTagList(List<Tag> result);
	Widget asWidget();
}
