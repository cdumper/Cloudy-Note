package com.sid.cloudynote.client.presenter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.sid.cloudynote.client.event.EditDoneButtonClickedEvent;
import com.sid.cloudynote.client.event.NewNoteEvent;
import com.sid.cloudynote.client.event.NotebookChangedEvent;
import com.sid.cloudynote.client.service.NotebookService;
import com.sid.cloudynote.client.service.NotebookServiceAsync;
import com.sid.cloudynote.client.view.SearchView;
import com.sid.cloudynote.client.view.interfaces.ISearchView;
import com.sid.cloudynote.shared.Notebook;

public class SearchPresenter implements Presenter, ISearchView.Presenter {
	private final HandlerManager eventBus;
	private final SearchView view;

	public SearchPresenter(SearchView view, HandlerManager eventBus) {
		super();
		this.eventBus = eventBus;
		this.view = view;
	}

	@Override
	public void go(HasWidgets container) {
		container.clear();
		container.add(view.asWidget());
	}

	@Override
	public void onNewNotebookButtonClicked() {
		newNotebookDialog().center();
	}

	@Override
	public void onNewNoteButtonClicked() {
		eventBus.fireEvent(new NewNoteEvent());
		view.getEdit().setText("Done");
	}

	@Override
	public void onSearchMade() {
		// TODO SearchNoteEvent : when perform a search
		// eventBus.fireEvent(new SearchNoteEvent());
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
				// call rpc to create new notebook
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
				// new notebook created fires the notebookChangedEvent
				eventBus.fireEvent(new NotebookChangedEvent());
			}
		};
		service.add(new Notebook(name), callback);
	}

	@Override
	public void onEditDoneButtonClicked() {
		eventBus.fireEvent(new EditDoneButtonClickedEvent());
		if(view.getEdit().getText().equals("Edit"))
			view.getEdit().setText("Done");
		else if(view.getEdit().getText().equals("Done"))
			view.getEdit().setText("Edit");
	}
}
