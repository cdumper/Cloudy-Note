package com.sid.cloudynote.client.presenter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.appengine.api.datastore.Key;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.sid.cloudynote.client.DataManager;
import com.sid.cloudynote.client.event.EditNoteEvent;
import com.sid.cloudynote.client.event.NoNotesExistEvent;
import com.sid.cloudynote.client.event.NoteChangedEvent;
import com.sid.cloudynote.client.event.NoteSelectionChangedEvent;
import com.sid.cloudynote.client.event.NotebookChangedEvent;
import com.sid.cloudynote.client.service.GroupService;
import com.sid.cloudynote.client.service.GroupServiceAsync;
import com.sid.cloudynote.client.service.InfoNoteSearchService;
import com.sid.cloudynote.client.service.InfoNoteSearchServiceAsync;
import com.sid.cloudynote.client.service.InfoNoteService;
import com.sid.cloudynote.client.service.InfoNoteServiceAsync;
import com.sid.cloudynote.client.service.ShareService;
import com.sid.cloudynote.client.service.ShareServiceAsync;
import com.sid.cloudynote.client.service.UserService;
import com.sid.cloudynote.client.service.UserServiceAsync;
import com.sid.cloudynote.client.view.NoteListView;
import com.sid.cloudynote.client.view.NotebookListView;
import com.sid.cloudynote.client.view.interfaces.INoteListView;
import com.sid.cloudynote.shared.Group;
import com.sid.cloudynote.shared.InfoNote;
import com.sid.cloudynote.shared.Notebook;
import com.sid.cloudynote.shared.User;

public class NoteListPresenter implements Presenter, INoteListView.Presenter {
	private final HandlerManager eventBus;
	private final NoteListView view;

	public NoteListPresenter(NoteListView view, HandlerManager eventBus) {
		super();
		this.eventBus = eventBus;
		this.view = view;
	}

	@Override
	public void onNoteItemSelected(InfoNote clickedItem) {
		eventBus.fireEvent(new NoteSelectionChangedEvent(clickedItem));
	}

	@Override
	public void loadNoteList(final Notebook notebook) {
		if (NotebookListView.ALL_NOTES.equals(notebook.getName()) && notebook.getKey() == null) {
			System.out.println("all notes");
			this.searchNotes("");
			return;
		}
		
		InfoNoteServiceAsync service = (InfoNoteServiceAsync) GWT
				.create(InfoNoteService.class);
		AsyncCallback<List<InfoNote>> callback = new AsyncCallback<List<InfoNote>>() {
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("getNotesList falied!");
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(List<InfoNote> result) {
				if (result != null && result.size() != 0) {
					Map<Key, InfoNote> noteMap = new HashMap<Key, InfoNote>();
					DataManager.setNotes(noteMap);
					eventBus.fireEvent(new NoteSelectionChangedEvent(result
							.get(0)));
				} else {
					eventBus.fireEvent(new NoNotesExistEvent());
				}
				view.setNoteList(result);
				view.setLabel(notebook.getName());
			}
		};
		service.getNotes(notebook, callback);
	}

	@Override
	public void go(HasWidgets container) {
		container.clear();
		container.add(view.asWidget());
	}

	@Override
	public void startEditing(InfoNote value) {
		// eventBus.fireEvent(new EditDoneButtonClickedEvent());
		// if(view.getEdit().getText().equals("Edit"))
		// view.getEdit().setText("Done");
		// else if(view.getEdit().getText().equals("Done"))
		// view.getEdit().setText("Edit");
		eventBus.fireEvent(new EditNoteEvent(value));
	}

	@Override
	public void makeNotesPublic(List<InfoNote> notes) {
		InfoNoteServiceAsync service = GWT.create(InfoNoteService.class);
		service.makeNotesPublic(notes, new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Failed to make notes public");
			}

			@Override
			public void onSuccess(Void result) {

			}
		});
	}

	@Override
	public void deleteNote(InfoNote note) {
		InfoNoteServiceAsync service = GWT.create(InfoNoteService.class);
		service.delete(note, new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Delete notebook failed!");
			}

			@Override
			public void onSuccess(Void result) {
				eventBus.fireEvent(new NotebookChangedEvent());
				loadNoteList(DataManager.getCurrentNotebook());
			}
		});
	}

	@Override
	public void loadGroupList(final String email) {
		GroupServiceAsync service = GWT.create(GroupService.class);
		service.getGroups(email, new AsyncCallback<List<Group>>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Failed to get groups of user:" + email);
			}

			@Override
			public void onSuccess(List<Group> result) {
				Map<Key, Group> allGroups = new HashMap<Key, Group>();
				for (Group group : result) {
					allGroups.put(group.getKey(), group);
				}
				DataManager.setAllGroups(allGroups);
			}
		});
	}

	@Override
	public void loadFriendsList(final String email) {
		UserServiceAsync service = GWT.create(UserService.class);
		service.getFriends(email, new AsyncCallback<List<User>>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Failed to get friends list of user:" + email);
			}

			@Override
			public void onSuccess(List<User> result) {
				Map<String, User> allFriends = new HashMap<String, User>();
				for (User user : result) {
					allFriends.put(user.getEmail(), user);
				}
				DataManager.setAllFriends(allFriends);
			}
		});
	}

	@Override
	public void shareNoteToUsers(final InfoNote note,
			final Map<String, Integer> access) {
		ShareServiceAsync service = GWT.create(ShareService.class);
		service.shareNoteToUsers(note, access, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Failed to share note to users");
			}

			@Override
			public void onSuccess(Void result) {
				GWT.log("Successfully update sharing settings of note to users");
				eventBus.fireEvent(new NoteChangedEvent(DataManager
						.getCurrentNotebook()));
			}

		});
	}

	@Override
	public void shareNoteToUsersAndGroups(InfoNote note,
			Map<String, Integer> userAccess, Map<Key, Integer> groupAccess) {
		ShareServiceAsync service = GWT.create(ShareService.class);
		service.shareNoteToUsersAndGroups(note, userAccess, groupAccess,
				new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						GWT.log("Failed to share note to users and groups");
					}

					@Override
					public void onSuccess(Void result) {
						GWT.log("Successfully update sharing settings of note to users and groups");
						eventBus.fireEvent(new NoteChangedEvent(DataManager
								.getCurrentNotebook()));
					}

				});
	}

	@Override
	public void shareNoteToGroups(InfoNote note, Map<Key, Integer> groupsAccess) {
		ShareServiceAsync service = GWT.create(ShareService.class);
		service.shareNoteToGroups(note, groupsAccess,
				new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						GWT.log("Failed to share note to groups");
					}

					@Override
					public void onSuccess(Void result) {
						GWT.log("Successfully update sharing settings of note to groups");
						eventBus.fireEvent(new NoteChangedEvent(DataManager
								.getCurrentNotebook()));
					}
				});
	}

	@Override
	public void searchNotes(String value) {
		InfoNoteSearchServiceAsync service = GWT
				.create(InfoNoteSearchService.class);
		service.searchNotes(value, new AsyncCallback<List<InfoNote>>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Failed to search notes");
			}

			@Override
			public void onSuccess(List<InfoNote> result) {
//				if (result.size() == 0) {
//					view.setLabel("Search Results");
//				} else {
//					Key notebook = result.get(0).getNotebook().getKey();
//					boolean flag = true;
//					for (InfoNote note : result) {
//						if (!notebook.equals(note.getNotebook().getKey())) {
//							flag = false;
//							break;
//						}
//					}
//					if (flag) {
//						view.setLabel(result.get(0).getNotebook().getName());
//					} else {
//						view.setLabel("All Notes");
//					}
//				}
				view.setLabel("Search Results");
				view.setNoteList(result);
			}
		});
	}
}
