package com.sid.cloudynote.client.sharing.presenter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.datastore.Key;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.sid.cloudynote.client.DataManager;
import com.sid.cloudynote.client.event.NoNotesExistEvent;
import com.sid.cloudynote.client.event.NoteSelectionChangedEvent;
import com.sid.cloudynote.client.presenter.Presenter;
import com.sid.cloudynote.client.service.InfoNoteService;
import com.sid.cloudynote.client.service.InfoNoteServiceAsync;
import com.sid.cloudynote.client.sharing.view.SharingNoteListView;
import com.sid.cloudynote.client.sharing.view.interfaces.ISharingNoteListView;
import com.sid.cloudynote.shared.InfoNote;

public class SharingNoteListPresenter implements Presenter,
		ISharingNoteListView.Presenter {
	private final HandlerManager eventBus;
	private final SharingNoteListView view;

	public SharingNoteListPresenter(SharingNoteListView view,
			HandlerManager eventBus) {
		super();
		this.eventBus = eventBus;
		this.view = view;
	}

	@Override
	public void go(HasWidgets container) {
		container.clear();
		container.add(this.view.asWidget());
		this.loadNoteList();
	}

	@Override
	public void onNoteItemSelected(InfoNote clickedItem) {
		// TODO Auto-generated method stub

	}

	@Override
	public void loadNoteList() {
		// TODO Auto-generated method stub
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
					eventBus.fireEvent(new NoteSelectionChangedEvent(result
							.get(0)));
				} else {
					eventBus.fireEvent(new NoNotesExistEvent());
				}
				view.setNoteList(result);
			}
		};
		service.getNotes(DataManager.getCurrentNotebook(), callback);
	}

	@Override
	public void startEditing(InfoNote infoNote) {
		// TODO Auto-generated method stub

	}
}
