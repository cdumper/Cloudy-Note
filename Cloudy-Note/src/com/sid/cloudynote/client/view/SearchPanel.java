package com.sid.cloudynote.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sid.cloudynote.client.event.INotebookChangedHandler;
import com.sid.cloudynote.client.event.NotebookChangedEvent;
import com.sid.cloudynote.client.service.NotebookService;
import com.sid.cloudynote.client.service.NotebookServiceAsync;
import com.sid.cloudynote.shared.Notebook;

public class SearchPanel extends Composite {
	private static SearchPanelUiBinder uiBinder = GWT
			.create(SearchPanelUiBinder.class);

	interface SearchPanelUiBinder extends UiBinder<Widget, SearchPanel> {
	}

	public SearchPanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	private boolean isNewNote = false;
	private NotebookListView notebookPanel;
	private NoteListView notePanel;
	private NoteViewPanel noteViewPanel;
	@UiField
	Button newNotebook;
	@UiField
	Button newNote;
	@UiField
	TextBox searchField;
	@UiField
	Button edit;

	@UiHandler("newNote")
	void onClickNewNote(ClickEvent e) {
		noteViewPanel.newNote();
		// saveNote();
		edit.setText("Done");
		isNewNote = true;
	}

	@UiHandler("newNotebook")
	void onClickNewNotebook(ClickEvent e) {
		newNotebookDialog().center();
	}

	private DialogBox newNotebookDialog() {
		final DialogBox dialog = new DialogBox();
		final TextBox name = new TextBox();
		Button confirm = new Button("Confirm");
		Button cancel = new Button("Cancel");
		dialog.setTitle("Create new notebook");
		VerticalPanel panel = new VerticalPanel();
		dialog.add(panel);
		panel.add(name);
		panel.add(cancel);
		panel.add(confirm);
		panel.setSize("200", "200");

		confirm.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				createNewNotebook(name.getText());
				dialog.hide();
			}
		});

		cancel.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				dialog.hide();
			}
		});
		return dialog;
	}

	private void createNewNotebook(String name) {
		NotebookServiceAsync service = (NotebookServiceAsync) GWT
				.create(NotebookService.class);
		AsyncCallback<Void> callback = new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Get Notebooks List falied!");
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(Void result) {
				GWT.log("Notebook added successfully!");
				// notebookPanel.loadNotebooks();
				HandlerManager eventBus = new HandlerManager(null);
				eventBus.fireEvent(new NotebookChangedEvent());
			}

		};
		service.add(new Notebook(name), callback);
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
		// TODO change the isEditing state, re-render the edit panel
		if (noteViewPanel.isEditing()) {
			this.saveNote();
			noteViewPanel.stopEdit();
			edit.setText("Edit");
		} else {
			noteViewPanel.startEdit();
			edit.setText("Done");
		}
	}

	public void setNotebookPanel(NotebookListView notebookListPanel) {
		this.notebookPanel = notebookListPanel;
	}

	public void setNotePanel(NoteListView noteListPanel) {
		this.notePanel = noteListPanel;
	}

	public void setNoteViewPanel(NoteViewPanel noteViewPanel) {
		this.noteViewPanel = noteViewPanel;
	}
}