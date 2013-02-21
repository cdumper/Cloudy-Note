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
import com.sid.cloudynote.client.event.EditDoneButtonClickedEvent;
import com.sid.cloudynote.client.event.EditNoteDoneEvent;
import com.sid.cloudynote.client.event.IEditDoneButtonClickedHandler;
import com.sid.cloudynote.client.event.IEditNoteDoneHandler;
import com.sid.cloudynote.client.event.INewNoteHandler;
import com.sid.cloudynote.client.event.INoNotesExistHandler;
import com.sid.cloudynote.client.event.NewNoteEvent;
import com.sid.cloudynote.client.event.NoNotesExistEvent;
import com.sid.cloudynote.client.view.interfaces.ISearchView;

public class SearchView extends ResizeComposite implements ISearchView, INoNotesExistHandler, INewNoteHandler, IEditNoteDoneHandler, IEditDoneButtonClickedHandler{
	@UiTemplate("SearchView.ui.xml")
	interface SearchViewUiBinder extends UiBinder<Widget, SearchView> {
	}
	private static SearchViewUiBinder uiBinder = GWT
			.create(SearchViewUiBinder.class);

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

	public Button getEdit() {
		return edit;
	}

	public SearchView() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	@UiHandler("newNote")
	void onClickNewNote(ClickEvent e) {
		presenter.onNewNoteButtonClicked();
	}

	@UiHandler("newNotebook")
	void onClickNewNotebook(ClickEvent e) {
		presenter.onNewNotebookButtonClicked();
	}

	@UiHandler("edit")
	void onClickEdit(ClickEvent e) {
		presenter.onClickEditDoneButton();
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	
	@Override
	public Widget asWidget(){
		return this.content;
	}

	@Override
	public void onNotesExistEvent(NoNotesExistEvent event) {
		this.edit.setVisible(false);
	}

	@Override
	public void onNewNote(NewNoteEvent event) {
		this.edit.setVisible(true);
		this.edit.setText("Done");
	}

	@Override
	public void onEditNoteDone(EditNoteDoneEvent event) {
		this.edit.setVisible(true);
		this.edit.setText("Edit");
	}

	@Override
	public void onEditDoneButtonClicked(EditDoneButtonClickedEvent event) {
		presenter.changeButtonText();
	}
}