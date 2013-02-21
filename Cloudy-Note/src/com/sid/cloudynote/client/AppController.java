package com.sid.cloudynote.client;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasWidgets;
import com.sid.cloudynote.client.event.EditDoneButtonClickedEvent;
import com.sid.cloudynote.client.event.EditNoteDoneEvent;
import com.sid.cloudynote.client.event.EditNoteEvent;
import com.sid.cloudynote.client.event.NewNoteEvent;
import com.sid.cloudynote.client.event.NoNotesExistEvent;
import com.sid.cloudynote.client.event.NoteChangedEvent;
import com.sid.cloudynote.client.event.NoteSelectionChangedEvent;
import com.sid.cloudynote.client.event.NotebookChangedEvent;
import com.sid.cloudynote.client.event.TagChangedEvent;
import com.sid.cloudynote.client.presenter.Presenter;

public class AppController implements Presenter, ValueChangeHandler<String> {
	private final HandlerManager eventBus;
	private Cloudy_Note cn;

	public AppController(Cloudy_Note cn, HandlerManager eventBus) {
		this.cn = cn;
		this.eventBus = eventBus;
		bind();
	}

	private void bind() {
		eventBus.addHandler(NewNoteEvent.TYPE, cn.noteView);
		eventBus.addHandler(NewNoteEvent.TYPE, cn.searchView);
		eventBus.addHandler(EditNoteEvent.TYPE, cn.noteView);
		eventBus.addHandler(NoteChangedEvent.TYPE, cn.noteListView);
		eventBus.addHandler(NotebookChangedEvent.TYPE, cn.notebookListView);
		eventBus.addHandler(EditNoteDoneEvent.TYPE, cn.noteView);
		eventBus.addHandler(EditNoteDoneEvent.TYPE, cn.searchView);
		eventBus.addHandler(NoNotesExistEvent.TYPE, cn.noteView);
		eventBus.addHandler(NoNotesExistEvent.TYPE, cn.searchView);
		eventBus.addHandler(EditDoneButtonClickedEvent.TYPE, cn.noteView);
		eventBus.addHandler(NoteSelectionChangedEvent.TYPE, cn.noteView);
		eventBus.addHandler(TagChangedEvent.TYPE, cn.notebookListView);
	}

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		// TODO appController history controll part
		System.out.println("onValueChange fired");
	}

	@Override
	public void go(final HasWidgets container) {
		this.cn.go(container);
	}
}
