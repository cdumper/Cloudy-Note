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
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.sid.cloudynote.client.DataManager;
import com.sid.cloudynote.shared.InfoNote;
import com.sid.cloudynote.shared.Notebook;

public class EditableNoteView extends ResizeComposite {

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

	private Map<Key, Notebook> notebookMap;
	private List<Key> notebookList = new ArrayList<Key>();

	interface EditPanelUiBinder extends UiBinder<Widget, EditableNoteView> {
	}

	public EditableNoteView() {
		initWidget(uiBinder.createAndBindUi(this));
		this.loadNotebooks();
//		this.presentNote(DataManager.getCurrentNote());
	}

	public void newNote() {
		title.setText("Untitled");
		setSelectedNotebook(DataManager.getCurrentNotebookKey());
		ckeditor.setData("Content...");
	}

	public void presentNote(InfoNote note) {
		if (note != null) {
			title.setText(note.getTitle());
			setSelectedNotebook(DataManager.getCurrentNotebookKey());
			ckeditor.setData(note.getContent());
		}
	}

	public InfoNote getInfoNote() {
		return new InfoNote(getSelectedNotebook(), title.getText(),
				ckeditor.getData());
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
}