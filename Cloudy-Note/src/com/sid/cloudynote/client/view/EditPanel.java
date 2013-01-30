package com.sid.cloudynote.client.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.axeiya.gwtckeditor.client.CKEditor;
import com.google.appengine.api.datastore.Key;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.sid.cloudynote.client.DataManager;
import com.sid.cloudynote.client.event.EditNoteDoneEvent;
import com.sid.cloudynote.client.event.IEditNoteDoneHandler;
import com.sid.cloudynote.client.service.InfoNoteService;
import com.sid.cloudynote.client.service.InfoNoteServiceAsync;
import com.sid.cloudynote.shared.InfoNote;
import com.sid.cloudynote.shared.Notebook;

public class EditPanel extends ResizeComposite implements IEditNoteDoneHandler {

	private static EditPanelUiBinder uiBinder = GWT
			.create(EditPanelUiBinder.class);

	@UiField
	CKEditor ckeditor;
	@UiField
	TextBox title;
	@UiField
	ListBox notebook;
	@UiField
	Anchor attach;
	// @UiField
	// TitleBar titleBar;

	private Map<Key, Notebook> notebookMap;
	private List<Key> notebookList = new ArrayList<Key>();

	interface EditPanelUiBinder extends UiBinder<Widget, EditPanel> {
	}

	public EditPanel() {
		initWidget(uiBinder.createAndBindUi(this));
		this.loadNotebooks();
		// this.presentNote(DataManager.getCurrentNote());
	}

	public void newNote() {
		title.setText("Untitled");
		setSelectedNotebook(DataManager.getCurrentNotebookKey());
		ckeditor.setData("Content...");
	}

	public void presentNote(InfoNote note) {
		// TODO Auto-generated method stub
		title.setText(note.getTitle());
		setSelectedNotebook(DataManager.getCurrentNotebookKey());
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
		InfoNote note = new InfoNote(getSelectedNotebook(), title.getText(),
				ckeditor.getData());
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
		note.setNotebook(getSelectedNotebook());
		note.setTitle(title.getText());
		note.setContent(ckeditor.getData());
		service.modify(note, callback);
	}

	public void setSelectedNotebook(Key key) {
		for (int i = 0; i < notebookList.size(); i++) {
			if (notebookList.get(i).equals(key)) {
				notebook.setSelectedIndex(i);
				break;
			}
		}
	}

	private void loadNotebooks() {
		notebookMap = DataManager.getNotebooks();
		for (Notebook nb : notebookMap.values()) {
			notebookList.add(nb.getKey());
			notebook.addItem(nb.getName());
		}
		setSelectedNotebook(DataManager.getCurrentNotebookKey());
	}

	public Notebook getSelectedNotebook() {
		return notebookMap.get(notebookList.get(notebook.getSelectedIndex()));
	}

	@UiHandler("attach")
	void onClick(ClickEvent e) {
		// TODO attachments
		Window.alert("TODO...Attach files!");
	}

	@Override
	public void onEditNoteDone(EditNoteDoneEvent event) {
		// TODO Auto-generated method stub
		System.out.println("edit done. save note");
	}
}