package com.sid.cloudynote.client.view.interfaces;

import java.util.List;
import java.util.Map;

import com.google.appengine.api.datastore.Key;
import com.google.gwt.user.client.ui.Widget;
import com.sid.cloudynote.shared.InfoNote;
import com.sid.cloudynote.shared.Notebook;

public interface IEditableNoteView {
	public interface Presenter {

		void loadNotebooks();
		
		void addAttachment(final String fileName, final String key);
		
		void onClickAttach();
		
		void presentAttachmentLinks(final List<String> keys);

		void createNewNote(InfoNote note);

		void moveNote(InfoNote note, Notebook notebook);

		void updateNote(InfoNote note);
	}
	
	void newNote();

	void presentNote(InfoNote note);
	
	void setPresenter(Presenter presenter);
	
	void setNotebookMap(Map<Key, Notebook> notebookMap);

	void presentAttachmentLink(String fileName, String key);

	Widget asWidget();
}
