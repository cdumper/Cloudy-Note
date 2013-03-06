package com.sid.cloudynote.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;
import com.sid.cloudynote.client.DataManager;
import com.sid.cloudynote.client.event.EditNoteDoneEvent;
import com.sid.cloudynote.client.event.EditNoteEvent;
import com.sid.cloudynote.client.event.NewNoteEvent;
import com.sid.cloudynote.client.event.NoNotesExistEvent;
import com.sid.cloudynote.client.event.NoteSelectionChangedEvent;
import com.sid.cloudynote.client.event.interfaces.IEditNoteDoneHandler;
import com.sid.cloudynote.client.event.interfaces.IEditNoteHandler;
import com.sid.cloudynote.client.event.interfaces.INewNoteHandler;
import com.sid.cloudynote.client.event.interfaces.INoNotesExistHandler;
import com.sid.cloudynote.client.event.interfaces.INoteSelectionChangedHandler;
import com.sid.cloudynote.client.view.interfaces.INoteView;

public class NoteView extends ResizeComposite implements INoteView,
		INewNoteHandler, IEditNoteHandler, IEditNoteDoneHandler, INoNotesExistHandler, INoteSelectionChangedHandler{
	@UiTemplate("NoteView.ui.xml")
	interface NoteViewUiBinder extends UiBinder<Widget, NoteView> {
	}

	private static NoteViewUiBinder uiBinder = GWT
			.create(NoteViewUiBinder.class);

	@UiField
	Container container;
	private Widget widget;

	public NoteView() {
		initWidget(uiBinder.createAndBindUi(this));
		NonEditableNoteView nonEditView = new NonEditableNoteView();
		nonEditView.presentNote(DataManager.getCurrentNote());
		this.widget = nonEditView;
	}

	public Container getContainer() {
		return container;
	}

	private Presenter presenter;

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public Widget asWidget() {
		return this.widget;
	}

	@Override
	public void onEditNote(EditNoteEvent event) {
		presenter.editNote(event.getNote());
	}

	@Override
	public void onEditNoteDone(EditNoteDoneEvent event) {
		presenter.presentNote(DataManager.getCurrentNote());
	}

	@Override
	public void onNewNote(NewNoteEvent event) {
		presenter.showNewNote();
	}

	@Override
	public void onNotesExistEvent(NoNotesExistEvent event) {
		this.container.clear();
	}

	@Override
	public void onNoteSelectionChanged(NoteSelectionChangedEvent event) {
		DataManager.setCurrentNote(event.getClickedItem());
		presenter.presentNote(event.getClickedItem());
	}
}
