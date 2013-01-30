package com.sid.cloudynote.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;
import com.sid.cloudynote.client.DataManager;
import com.sid.cloudynote.client.event.EditDoneButtonClickedEvent;
import com.sid.cloudynote.client.event.EditNoteDoneEvent;
import com.sid.cloudynote.client.event.EditNoteEvent;
import com.sid.cloudynote.client.event.IEditDoneButtonClickedHandler;
import com.sid.cloudynote.client.event.IEditNoteDoneHandler;
import com.sid.cloudynote.client.event.IEditNoteHandler;
import com.sid.cloudynote.client.event.INewNoteHandler;
import com.sid.cloudynote.client.event.NewNoteEvent;
import com.sid.cloudynote.client.view.interfaces.INoteView;

public class NoteView extends ResizeComposite implements INoteView,
		INewNoteHandler, IEditNoteHandler, IEditNoteDoneHandler, IEditDoneButtonClickedHandler {
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
		widget = new NonEditableNoteView();
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
		EditableNoteView editView = new EditableNoteView();
		editView.setHeight("100%");
		editView.setWidth("100%");
		editView.presentNote(DataManager.getCurrentNote());
		this.container.clear();
		this.container.add(editView);
		presenter.setView(editView);
		presenter.setEditing(true);
	}
	
	@Override
	public void onEditNoteDone(EditNoteDoneEvent event) {
		NonEditableNoteView nonEditView = new NonEditableNoteView();
		nonEditView.setHeight("100%");
		nonEditView.setWidth("100%");
		nonEditView.presentNote(DataManager.getCurrentNote());
		this.container.clear();
		this.container.add(nonEditView);
		presenter.setView(nonEditView);
		presenter.setEditing(false);
	}

	@Override
	public void onNewNote(NewNoteEvent event) {
		EditableNoteView editView = new EditableNoteView();
		editView.setHeight("100%");
		editView.setWidth("100%");
		this.container.clear();
		this.container.add(editView);
		presenter.setView(editView);
		presenter.setEditing(true);
	}

	@Override
	public void onEditDoneButtonClicked(EditDoneButtonClickedEvent event) {
		if (presenter.isEditing()) {
			presenter.stopEdit();
		} else {
			presenter.startEdit();
		}
	}
}
