package com.sid.cloudynote.client.view;

import com.axeiya.gwtckeditor.client.CKEditor;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;
import com.sid.cloudynote.client.model.DataManager;
import com.sid.cloudynote.client.model.Note;

public class EditPanel extends ResizeComposite{

	private static EditPanelUiBinder uiBinder = GWT
			.create(EditPanelUiBinder.class);
	
	private boolean isEditing = false;
	
	@UiField
	CKEditor ckeditor;
	
	@UiField
	TitleBar titleBar;
	
	interface EditPanelUiBinder extends UiBinder<Widget, EditPanel> {
	}

	public EditPanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	public void newNote(){
		titleBar.title.setText("Untitled");
		titleBar.setSelectedNotebook(DataManager.getCurrentNotebookKey());
		ckeditor.setData("");
	}

	public void presentNote(Note note) {
		// TODO Auto-generated method stub
		titleBar.title.setText(note.getTitle());
//		titleBar.setSelectedNotebook(note.getNotebook().getKey());
		titleBar.setSelectedNotebook(DataManager.getCurrentNotebookKey());
		ckeditor.setData(note.getContent());
	}
}
