package com.sid.cloudynote.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class SearchView extends ResizeComposite implements ISearchView{
	@UiTemplate("SearchView.ui.xml")
	interface SearchViewUiBinder extends UiBinder<Widget, SearchView> {
	}
	private static SearchViewUiBinder uiBinder = GWT
			.create(SearchViewUiBinder.class);

	private boolean isNewNote = false;
	private NoteViewPanel noteViewPanel;
	private Presenter presenter;

	@UiField
	Container container;
	public Container getContainer() {
		return container;
	}

	@UiField
	HorizontalPanel content;
	@UiField
	Button newNotebook;
	@UiField
	Button newNote;
	@UiField
	TextBox searchField;
	@UiField
	Button edit;

	public SearchView() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	@UiHandler("newNote")
	void onClickNewNote(ClickEvent e) {
		presenter.onNewNoteButtonClicked();
		
//		noteViewPanel.newNote();
//		edit.setText("Done");
//		isNewNote = true;
	}

	@UiHandler("newNotebook")
	void onClickNewNotebook(ClickEvent e) {
		presenter.onNewNotebookButtonClicked();
//		newNotebookDialog().center();
	}

	private void saveNote() {
		if (isNewNote) {
			this.noteViewPanel.createNewNote();
			isNewNote = false;
		} else {
			this.noteViewPanel.updateNote();
		}
//		notePanel.loadNotes();
	}

	@UiHandler("edit")
	void onClickEdit(ClickEvent e) {
		//change the isEditing state, re-render the edit panel
		if (noteViewPanel.isEditing()) {
			this.saveNote();
			noteViewPanel.stopEdit();
			edit.setText("Edit");
		} else {
			noteViewPanel.startEdit();
			edit.setText("Done");
		}
	}

	public void setNoteViewPanel(NoteViewPanel noteViewPanel) {
		this.noteViewPanel = noteViewPanel;
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	
	@Override
	public Widget asWidget(){
		return this.content;
	}
}