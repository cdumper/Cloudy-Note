package com.sid.cloudynote.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
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
import com.sid.cloudynote.client.model.InfoNote;
import com.sid.cloudynote.client.model.Notebook;
import com.sid.cloudynote.client.service.InfoNoteService;
import com.sid.cloudynote.client.service.InfoNoteServiceAsync;
import com.sid.cloudynote.client.service.NotebookService;
import com.sid.cloudynote.client.service.NotebookServiceAsync;

public class SearchPanel extends Composite {
	private static SearchPanelUiBinder uiBinder = GWT
			.create(SearchPanelUiBinder.class);

	interface SearchPanelUiBinder extends UiBinder<Widget, SearchPanel> {
	}

	public SearchPanel() {
		initWidget(uiBinder.createAndBindUi(this));
		this.newNote.setText("New Note");

	}

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
		InfoNoteServiceAsync service = (InfoNoteServiceAsync) GWT
				.create(InfoNoteService.class);
		AsyncCallback<Void> callback = new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				System.out.println("falied! getNotesList");
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(Void result) {
				System.out.println("New note added successfully!");
			}
		};
		service.add(new InfoNote(), callback);
	}

	@UiHandler("newNotebook")
	void onClickNewNotebook(ClickEvent e) {
		newNotebookDialog().show();

		// NotebookServiceAsync service = (NotebookServiceAsync) GWT
		// .create(NotebookService.class);
		// AsyncCallback<Void> callback = new AsyncCallback<Void>() {
		//
		// @Override
		// public void onFailure(Throwable caught) {
		// System.out.println("falied! getNotebooksList");
		// caught.printStackTrace();
		// }
		//
		// @Override
		// public void onSuccess(Void result) {
		// System.out.println("Notebook added successfully!");
		// }
		//
		// };
		// service.add(new Notebook(), callback);
	}

	private DialogBox newNotebookDialog() {
		// TODO Auto-generated method stub
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
		
		cancel.addClickHandler(new ClickHandler(){
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
				System.out.println("falied! getNotebooksList");
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(Void result) {
//				System.out.println("Notebook added successfully!");
			}

		};
		service.add(new Notebook(name), callback);
	}

	@UiHandler("edit")
	void onClickEdit(ClickEvent e) {
	}
}