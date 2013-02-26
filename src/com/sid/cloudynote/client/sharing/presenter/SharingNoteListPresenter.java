package com.sid.cloudynote.client.sharing.presenter;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.sid.cloudynote.client.event.ViewSharedNoteEvent;
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
		this.loadPublicNoteList();
	}

	@Override
	public void onNoteItemSelected(InfoNote clickedItem) {
		// TODO view/edit public/shared notes

	}

	@Override
	public void loadPublicNoteList() {
		InfoNoteServiceAsync service = (InfoNoteServiceAsync) GWT
				.create(InfoNoteService.class);
		AsyncCallback<List<InfoNote>> callback = new AsyncCallback<List<InfoNote>>() {
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Get public NotesList falied!");
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(List<InfoNote> result) {
				view.setNoteList(result);
			}
		};
		service.getPublicNotes(callback);
	}

	@Override
	public void loadSharedNoteList(String id) {
		InfoNoteServiceAsync service = (InfoNoteServiceAsync) GWT
				.create(InfoNoteService.class);
		AsyncCallback<List<InfoNote>> callback = new AsyncCallback<List<InfoNote>>() {
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Get shared NotesList falied!");
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(List<InfoNote> result) {
				view.setNoteList(result);
			}
		};
		service.getSharedNotes(id, callback);
	}

	@Override
	public void loadGroups() {
		// TODO loadGroups
	}

	@Override
	public void startEditing(InfoNote infoNote) {
		// TODO startEditing the note if have write access
	}

	@Override
	public void viewNote(InfoNote note) {
		eventBus.fireEvent(new ViewSharedNoteEvent(note));
	}
}
