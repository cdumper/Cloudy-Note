package com.sid.cloudynote.client.presenter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.datastore.Key;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.sid.cloudynote.client.DataManager;
import com.sid.cloudynote.client.event.EditNoteDoneEvent;
import com.sid.cloudynote.client.service.InfoNoteService;
import com.sid.cloudynote.client.service.InfoNoteServiceAsync;
import com.sid.cloudynote.client.view.NoteListView;
import com.sid.cloudynote.client.view.interfaces.INoteListView;
import com.sid.cloudynote.shared.InfoNote;
import com.sid.cloudynote.shared.Notebook;

public class NoteListPresenter implements Presenter, INoteListView.Presenter {
	private final HandlerManager eventBus;
	private final NoteListView view;

	public NoteListPresenter(NoteListView view, HandlerManager eventBus) {
		super();
		this.eventBus = eventBus;
		this.view = view;
	}

	@Override
	public void onNoteItemSelected(InfoNote clickedItem) {
		//TODO on note item selected
		 eventBus.fireEvent(new EditNoteDoneEvent());
	}

	@Override
	public void onNoteItemRightClicked(InfoNote clickedItem) {
		// TODO show context menu to be able to delete note

	}

	@Override
	public void loadNoteList(final Notebook notebook) {
		InfoNoteServiceAsync service = (InfoNoteServiceAsync) GWT
				.create(InfoNoteService.class);
		AsyncCallback<List<InfoNote>> callback = new AsyncCallback<List<InfoNote>>() {
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("falied! getNotesList");
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(List<InfoNote> result) {
				if (result != null && result.size() != 0) {
					Map<Key, InfoNote> noteMap = new HashMap<Key, InfoNote>();
					for (InfoNote note : result) {
						noteMap.put(note.getKey(), note);
					}
					DataManager.setNotes(noteMap);
					// everytime set the selected note to the first one
					//					if (notebook.getKey() != DataManager.getCurrentNotebookKey() || DataManager.getCurrentNoteKey() == null) 
						DataManager.setCurrentNote(result.get(0).getKey());
						// eventBus.fireEvent(new EditNoteEvent());
				} else {
					GWT.log("No notes exist!");
				}
				view.setNoteList(result);
			}
		};
		service.getNotes(notebook, callback);
	}

	@Override
	public void go(HasWidgets container) {
		// this.loadNoteList(DataManager.getCurrentNotebook());
		container.clear();
		container.add(view.asWidget());
	}

}
