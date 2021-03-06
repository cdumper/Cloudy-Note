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
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.sid.cloudynote.client.AppController;
import com.sid.cloudynote.client.DataManager;
import com.sid.cloudynote.client.event.NewNoteEvent;
import com.sid.cloudynote.client.event.NoteChangedEvent;
import com.sid.cloudynote.client.event.NotebookChangedEvent;
import com.sid.cloudynote.client.event.PresentNotesEvent;
import com.sid.cloudynote.client.event.TagChangedEvent;
import com.sid.cloudynote.client.event.UserInfoChangedEvent;
import com.sid.cloudynote.client.service.InfoNoteService;
import com.sid.cloudynote.client.service.InfoNoteServiceAsync;
import com.sid.cloudynote.client.service.NotebookService;
import com.sid.cloudynote.client.service.NotebookServiceAsync;
import com.sid.cloudynote.client.service.TagService;
import com.sid.cloudynote.client.service.TagServiceAsync;
import com.sid.cloudynote.client.service.UserService;
import com.sid.cloudynote.client.service.UserServiceAsync;
import com.sid.cloudynote.client.view.NotebookListView;
import com.sid.cloudynote.client.view.interfaces.INotebookListView;
import com.sid.cloudynote.shared.InfoNote;
import com.sid.cloudynote.shared.Notebook;
import com.sid.cloudynote.shared.Tag;
import com.sid.cloudynote.shared.User;

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
		dialog.setAnimationEnabled(true);
		dialog.setGlassEnabled(true);
		dialog.setText("Create a new Notebook");
		final TextBox name = new TextBox();
		name.setWidth("200px");
		Label label = new Label();
		label.setText("Notebook Name:");
		Button confirm = new Button("Confirm");
		Button cancel = new Button("Cancel");
		HTMLPanel content = new HTMLPanel("");
		dialog.add(content);
		content.setWidth("250px");
		content.add(label);
		content.add(name);
		content.add(cancel);
		content.add(confirm);

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

	private DialogBox newTagDialog() {
		final DialogBox dialog = new DialogBox();
		dialog.setAnimationEnabled(true);
		dialog.setGlassEnabled(true);
		dialog.setText("Create a new Tag");
		final TextBox name = new TextBox();
		name.setWidth("200px");
		Label label = new Label();
		label.setText("Tag Name:");
		Button confirm = new Button("Confirm");
		Button cancel = new Button("Cancel");
		HTMLPanel content = new HTMLPanel("");
		dialog.add(content);
		content.setWidth("250px");
		content.add(label);
		content.add(name);
		content.add(cancel);
		content.add(confirm);

		confirm.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// call rpc to create new tag
				createNewTag(name.getText());
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
				eventBus.fireEvent(new PresentNotesEvent(tag.getName(), result));
			}

		});
	}

	@Override
	public void onNewTagButtonClicked() {
		newTagDialog().center();
	}

	@Override
	public void deleteNotebook(final Notebook selectedNotebook) {
		// TODO need to take into account the CASSCADE DELETE (deleting
		// all the notes in the notebook)
		NotebookServiceAsync service = GWT.create(NotebookService.class);
		service.delete(selectedNotebook, new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Delete notebook failed!");
			}

			@Override
			public void onSuccess(Void result) {
				loadNotebookList();
				UserServiceAsync service = GWT.create(UserService.class);
				service.getUser(selectedNotebook.getUser().getEmail(), new AsyncCallback<User>(){

					@Override
					public void onFailure(Throwable caught) {
						GWT.log("Failed to get user info");
					}

					@Override
					public void onSuccess(User result) {
						eventBus.fireEvent(new UserInfoChangedEvent(result));
					}
					
				});
			}
		});
	}
}
