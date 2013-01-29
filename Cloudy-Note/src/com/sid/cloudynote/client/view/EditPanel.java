package com.sid.cloudynote.client.view;

import com.axeiya.gwtckeditor.client.CKEditor;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;
import com.sid.cloudynote.client.DataManager;
import com.sid.cloudynote.client.service.InfoNoteService;
import com.sid.cloudynote.client.service.InfoNoteServiceAsync;
import com.sid.cloudynote.shared.InfoNote;
import com.sid.cloudynote.shared.Notebook;

public class EditPanel extends ResizeComposite{

	private static EditPanelUiBinder uiBinder = GWT
			.create(EditPanelUiBinder.class);
	
	@UiField
	CKEditor ckeditor;
	
	@UiField
	TitleBar titleBar;
	
	interface EditPanelUiBinder extends UiBinder<Widget, EditPanel> {
	}

	public EditPanel() {
		initWidget(uiBinder.createAndBindUi(this));
//		this.presentNote(DataManager.getCurrentNote());
	}
	
	public void newNote(){
		titleBar.title.setText("Untitled");
		titleBar.setSelectedNotebook(DataManager.getCurrentNotebookKey());
		ckeditor.setData("Content...");
	}

	public void presentNote(InfoNote note) {
		// TODO Auto-generated method stub
		titleBar.title.setText(note.getTitle());
		titleBar.setSelectedNotebook(DataManager.getCurrentNotebookKey());
		ckeditor.setData(note.getContent());
	}

	public void createNewNote() {
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
				GWT.log("New InfoNote added successfully!");
			}
		};
		InfoNote note = new InfoNote(titleBar.getSelectedNotebook(),titleBar.title.getText(),ckeditor.getData());
		service.add(note, callback);
	}

	public void updateNote() {
		InfoNoteServiceAsync service = (InfoNoteServiceAsync) GWT
				.create(InfoNoteService.class);
		AsyncCallback<Void> callback = new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("update note failed");
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(Void result) {
				GWT.log("Note updated successfully!");
			}
		};
		InfoNote note = DataManager.getCurrentNote();
		note.setNotebook(titleBar.getSelectedNotebook());
		note.setTitle(titleBar.title.getText());
		note.setContent(ckeditor.getData());
		service.modify(note, callback);
	}
}