package com.sid.cloudynote.client.presenter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.sid.cloudynote.client.DataManager;
import com.sid.cloudynote.client.event.EditNoteDoneEvent;
import com.sid.cloudynote.client.event.EditNoteEvent;
import com.sid.cloudynote.client.event.NoteChangedEvent;
import com.sid.cloudynote.client.service.InfoNoteService;
import com.sid.cloudynote.client.service.InfoNoteServiceAsync;
import com.sid.cloudynote.client.view.EditableNoteView;
import com.sid.cloudynote.client.view.interfaces.INoteView;
import com.sid.cloudynote.shared.InfoNote;

public class NotePresenter extends SimplePanel implements Presenter,
		INoteView.Presenter {
	private final HandlerManager eventBus;
	private Widget view;
	private boolean isEditing = false;

	public NotePresenter(Widget view, HandlerManager eventBus) {
		super();
		this.isEditing = false;
		this.eventBus = eventBus;
		this.view = view;
	}

	@Override
	public boolean isEditing() {
		return isEditing;
	}

	@Override
	public void setEditing(boolean isEditing) {
		this.isEditing = isEditing;
	}

	@Override
	public void go(HasWidgets container) {
		container.clear();
		Widget widget = view.asWidget();
		widget.setHeight("100%");
		widget.setWidth("100%");
		container.add(widget);
	}

	@Override
	public void saveNote() {
		// TODO Auto-generated method stub
		EditableNoteView panel = (EditableNoteView) view;
		createNewNote(panel.getInfoNote());
		eventBus.fireEvent(new EditNoteDoneEvent());
	}

	@Override
	public void stopEdit() {
		saveNote();
	}

	@Override
	public void startEdit() {
		eventBus.fireEvent(new EditNoteEvent());
	}

	@Override
	public void setView(Widget view) {
		this.view = view;
	}

	@Override
	public void createNewNote(InfoNote note) {
		InfoNoteServiceAsync service = (InfoNoteServiceAsync) GWT
				.create(InfoNoteService.class);
		AsyncCallback<Void> callback = new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("falied! getNotesList");
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(Void result) {
				eventBus.fireEvent(new NoteChangedEvent(DataManager.getCurrentNotebook()));
				GWT.log("New InfoNote added successfully!");
			}
		};
		service.add(note, callback);
	}

	@Override
	public void updateNote(InfoNote note) {
		// TODO Auto-generated method stub
		eventBus.fireEvent(new NoteChangedEvent(DataManager.getCurrentNotebook()));
	}
}
