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
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sid.cloudynote.client.DataManager;
import com.sid.cloudynote.client.view.interfaces.IEditableNoteView;
import com.sid.cloudynote.shared.InfoNote;
import com.sid.cloudynote.shared.Notebook;

public class EditableNoteView extends ResizeComposite implements IEditableNoteView {

	private static EditPanelUiBinder uiBinder = GWT
			.create(EditPanelUiBinder.class);

	@UiField
	Container container;
	@UiField
	DockLayoutPanel content;
	@UiField
	VerticalPanel topPanel;
	@UiField
	Button doneButton;
	@UiField
	CKEditor ckeditor;
	@UiField
	TextBox title;
	@UiField
	ListBox notebook;
	@UiField
	Anchor attach;

	private boolean isNew;
	private Presenter presenter;
	private Map<Key, Notebook> notebookMap;
	private List<Key> notebookList = new ArrayList<Key>();

	interface EditPanelUiBinder extends UiBinder<Widget, EditableNoteView> {
	}

	public EditableNoteView() {
		initWidget(uiBinder.createAndBindUi(this));
//		this.loadNotebooks();
		// this.presentNote(DataManager.getCurrentNote());
	}
	
	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	@UiHandler("doneButton")
	void onClickDone(ClickEvent e) {
		if (this.isNew)
			presenter.createNewNote(this.getInfoNote());
		else {
			InfoNote note = DataManager.getCurrentNote();
			note.setTitle(this.getInfoNote().getTitle());
			note.setContent(this.getInfoNote().getContent());
			// note.setNotebook(panel.getInfoNote().getNotebook());
			if (!note.getNotebook().getKey()
					.equals(this.getInfoNote().getNotebook().getKey())) {
				presenter.moveNote(note, this.getInfoNote().getNotebook());
			} else {
				presenter.updateNote(note);
			}
		}
	}

	@Override
	public void newNote() {
		title.setText("Untitled");
		setSelectedNotebook(DataManager.getCurrentNotebookKey());
		ckeditor.setData("Content...");
	}

	@Override
	public void presentNote(InfoNote note) {
		if (note != null) {
			title.setText(note.getTitle());
			setSelectedNotebook(DataManager.getCurrentNotebookKey());
			ckeditor.setData(note.getContent());
			if (note.getAttachments() != null) {
				presentAttachmentLinks(note.getAttachments());
			}
		}
	}

	public InfoNote getInfoNote() {
		return new InfoNote(getSelectedNotebook(), title.getText(),
				ckeditor.getData());
	}

//	private String removeHTMLTags(String data) {
//		return data.replaceAll("\\<.*?>", "");
//	}

	public void setSelectedNotebook(Key key) {
		for (int i = 0; i < notebookList.size(); i++) {
			if (notebookList.get(i).equals(key)) {
				notebook.setSelectedIndex(i);
				break;
			}
		}
	}

	private void loadNotebooks() {
		presenter.loadNotebooks();
	}

	public Notebook getSelectedNotebook() {
		return notebookMap.get(notebookList.get(notebook.getSelectedIndex()));
	}

	@UiHandler("attach")
	void onClickAttach(ClickEvent e) {
		presenter.onClickAttach();
	}

	private void presentAttachmentLinks(final List<String> keys) {
		presenter.presentAttachmentLinks(keys);
	}

	@Override
	public void presentAttachmentLink(String fileName, String key) {
		if (key == null)
			key = "null";
		Anchor file = new Anchor();
		file.setText(fileName);
		file.setHref("/cloudy_note/serve?blob-key=" + key);
		topPanel.add(file);
	}
	
	@Override
	public void setPresenter (Presenter presenter){
		this.presenter = presenter;
		this.loadNotebooks();
	}
	
	@Override
	public Widget asWidget() {
		return this.content;
	}
	
	public Container getContainer () {
		return this.container;
	}

	@Override
	public void setNotebookMap(Map<Key, Notebook> notebookMap) {
		this.notebookMap = notebookMap;
		for (Notebook nb : notebookMap.values()) {
			notebookList.add(nb.getKey());
			notebook.addItem(nb.getName());
		}
		setSelectedNotebook(DataManager.getCurrentNotebookKey());
	}
}