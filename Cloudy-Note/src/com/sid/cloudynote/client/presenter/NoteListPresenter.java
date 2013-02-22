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
import com.sid.cloudynote.client.event.EditDoneButtonClickedEvent;
import com.sid.cloudynote.client.event.NoNotesExistEvent;
import com.sid.cloudynote.client.event.NoteSelectionChangedEvent;
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
		eventBus.fireEvent(new NoteSelectionChangedEvent(clickedItem));
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
				GWT.log("getNotesList falied!");
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(List<InfoNote> result) {
				if (result != null && result.size() != 0) {
					Map<Key, InfoNote> noteMap = new HashMap<Key, InfoNote>();
					DataManager.setNotes(noteMap);
					eventBus.fireEvent(new NoteSelectionChangedEvent(result.get(0)));
				} else {
					eventBus.fireEvent(new NoNotesExistEvent());
				}
				view.setNoteList(result);
			}
		};
		service.getNotes(notebook, callback);
	}

	@Override
	public void go(HasWidgets container) {
		container.clear();
		container.add(view.asWidget());
	}

	@Override
	public void startEditing(InfoNote value) {
//		eventBus.fireEvent(new EditDoneButtonClickedEvent());
//		if(view.getEdit().getText().equals("Edit"))
//			view.getEdit().setText("Done");
//		else if(view.getEdit().getText().equals("Done"))
//			view.getEdit().setText("Edit");
		eventBus.fireEvent(new EditDoneButtonClickedEvent(value));
	}
}
