package com.sid.cloudynote.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.sid.cloudynote.client.model.InfoNote;
import com.sid.cloudynote.client.service.InfoNoteService;
import com.sid.cloudynote.client.service.InfoNoteServiceAsync;

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
	Button newNote;
	@UiField
	TextBox searchField;

	@UiHandler("newNote")
	void onClick(ClickEvent e) {
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
		 service.add(new InfoNote(),callback);
	}
}
