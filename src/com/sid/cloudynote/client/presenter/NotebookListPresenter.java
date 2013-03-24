package com.sid.cloudynote.client.presenter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.datastore.Key;
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
import com.sid.cloudynote.client.AppController;
import com.sid.cloudynote.client.DataManager;
import com.sid.cloudynote.client.event.NewNoteEvent;
import com.sid.cloudynote.client.event.NoteChangedEvent;
import com.sid.cloudynote.client.event.NotebookChangedEvent;
import com.sid.cloudynote.client.event.PresentNotesEvent;
import com.sid.cloudynote.client.event.TagChangedEvent;
import com.sid.cloudynote.client.service.InfoNoteService;
import com.sid.cloudynote.client.service.InfoNoteServiceAsync;
import com.sid.cloudynote.client.service.NotebookService;
import com.sid.cloudynote.client.service.NotebookServiceAsync;
import com.sid.cloudynote.client.service.TagService;
import com.sid.cloudynote.client.service.TagServiceAsync;
import com.sid.cloudynote.client.view.NotebookListView;
import com.sid.cloudynote.client.view.interfaces.INotebookListView;
import com.sid.cloudynote.shared.InfoNote;
import com.sid.cloudynote.shared.Notebook;
import com.sid.cloudynote.shared.Tag;

public class NotebookListPresenter implements Presenter,
		INotebookListView.Presenter {
	private final HandlerManager eventBus;
	private final NotebookListView view;

	public NotebookListPresenter(NotebookListView view, HandlerManager eventBus) {
		super();
		this.view = view;
		this.eventBus = eventBus;
	}

	@Override
	public void onNotebookItemSelected(Notebook clickedItem) {
		eventBus.fireEvent(new NoteChangedEvent(clickedItem));
		DataManager.setCurrentNotebook(clickedItem.getKey());
	}

	@Override
	public void go(HasWidgets container) {
		this.loadNotebookList();
		this.loadTagList();
		container.clear();
		container.add(view.asWidget());
	}

	@Override
	public void loadNotebookList() {
		NotebookServiceAsync service = (NotebookServiceAsync) GWT
				.create(NotebookService.class);
		AsyncCallback<List<Notebook>> callback = new AsyncCallback<List<Notebook>>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("getNotebooksList falied!");
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(List<Notebook> result) {
				if (result.size() != 0) {
					view.setNotebookList(result);
					Map<Key, Notebook> notebookMap = new HashMap<Key, Notebook>();
					for (Notebook notebook : result) {
						notebookMap.put(notebook.getKey(), notebook);
					}
					DataManager.setNotebooks(notebookMap);
					if (DataManager.getCurrentNotebookKey() == null) {
						DataManager.setCurrentNotebook(result.get(0).getKey());
						eventBus.fireEvent(new NoteChangedEvent(result.get(0)));
					}
				} else {
					GWT.log("No notebooks exist!");
					createDefaultNotebook();
				}
			}
		};
		service.getNotebooks(callback);
	}

	@Override
	public void loadTagList() {
		TagServiceAsync service = (TagServiceAsync) GWT
				.create(TagService.class);
		AsyncCallback<List<Tag>> callback = new AsyncCallback<List<Tag>>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("getNotebooksList falied!");
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(List<Tag> result) {
				if (result.size() != 0) {
					view.setTagList(result);
					Map<Key, Tag> tagMap = new HashMap<Key, Tag>();
					for (Tag tag : result) {
						tagMap.put(tag.getKey(), tag);
					}
					DataManager.setAllTags(tagMap);
				} else {
					GWT.log("No tags exist!");
				}
			}
		};
		service.getTags(AppController.get().getLoginInfo().getEmail(), callback);
	}

	private void createDefaultNotebook() {
		NotebookServiceAsync service = (NotebookServiceAsync) GWT
				.create(NotebookService.class);
		AsyncCallback<Void> callback = new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Add Notebook falied!");
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(Void result) {
				GWT.log("Default notebook created...");
				eventBus.fireEvent(new NotebookChangedEvent());
			}
		};
		service.add(new Notebook(AppController.get().getLoginInfo().getNickname()+"'s notebook"), callback);
	}

	@Override
	public void createNewNotebook(String name) {
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
				eventBus.fireEvent(new NotebookChangedEvent());

			}

		};
		service.add(new Notebook(name), callback);
	}

	@Override
	public void createNewTag(String name) {
		TagServiceAsync service = (TagServiceAsync) GWT
				.create(TagService.class);
		AsyncCallback<Void> callback = new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Get Tags List falied!");
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(Void result) {
				GWT.log("Tag added successfully!");
				eventBus.fireEvent(new TagChangedEvent());
			}
		};
		service.add(new Tag(name), callback);
	}

	@Override
	public void onNewNotebookButtonClicked() {
		newNotebookDialog().center();
	}

	@Override
	public void onNewNoteButtonClicked() {
		eventBus.fireEvent(new NewNoteEvent());
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

	@Override
	public void renameTag(Tag tag, String name) {
		tag.setName(name);
		TagServiceAsync service = GWT.create(TagService.class);
		service.modify(tag, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Rename tag failed!");
			}

			@Override
			public void onSuccess(Void result) {
				loadTagList();
			}
		});
	}

	@Override
	public void deleteTag(Tag tag) {
		TagServiceAsync service = GWT.create(TagService.class);
		service.delete(tag, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Delete tag failed!");
			}

			@Override
			public void onSuccess(Void result) {
				loadTagList();
			}
		});
	}

	@Override
	public void loadNotesByTag(final Tag tag) {
		InfoNoteServiceAsync service = GWT.create(InfoNoteService.class);
		service.getNotesByTag(tag, new AsyncCallback<List<InfoNote>>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Failed to get notes by tag:" + tag.getName());
			}

			@Override
			public void onSuccess(List<InfoNote> result) {
				eventBus.fireEvent(new PresentNotesEvent(result));
			}

		});
	}
}
