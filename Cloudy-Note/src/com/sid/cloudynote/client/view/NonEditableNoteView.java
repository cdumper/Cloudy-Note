package com.sid.cloudynote.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.sid.cloudynote.client.DataManager;
import com.sid.cloudynote.shared.InfoNote;
import com.sid.cloudynote.shared.Note;

public class NonEditableNoteView extends Composite {
	private Note note;

	public Note getNote() {
		return note;
	}

	public void setNote(Note note) {
		this.note = note;
	}

	private static NonEditPanelUiBinder uiBinder = GWT
			.create(NonEditPanelUiBinder.class);

	interface NonEditPanelUiBinder extends UiBinder<Widget, NonEditableNoteView> {
	}

	public NonEditableNoteView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public NonEditableNoteView(Note note) {
		initWidget(uiBinder.createAndBindUi(this));
		this.note = note;
		this.title.setText(note.getTitle());
		this.notebook.setText(note.getNotebook().getName());
		this.content.setText(note.getContent());
	}

	@UiField
	Label title;
	@UiField
	Label notebook;
	@UiField
	TextArea content;

	public NonEditableNoteView(String firstName) {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	public void presentNote(InfoNote note) {
		title.setText(note.getTitle());
		notebook.setText(DataManager.getCurrentNotebook().getName());
		content.setText(note.getContent());
	}
}
