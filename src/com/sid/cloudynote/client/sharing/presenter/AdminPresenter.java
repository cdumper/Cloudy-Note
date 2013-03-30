package com.sid.cloudynote.client.sharing.presenter;

import java.util.List;
import java.util.Map;

import com.google.appengine.api.datastore.Key;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.sid.cloudynote.client.AppController;
import com.sid.cloudynote.client.DataManager;
import com.sid.cloudynote.client.event.GroupsChangedEvent;
import com.sid.cloudynote.client.event.NoteChangedEvent;
import com.sid.cloudynote.client.event.NotebookChangedEvent;
import com.sid.cloudynote.client.presenter.Presenter;
import com.sid.cloudynote.client.service.AccessRightService;
import com.sid.cloudynote.client.service.AccessRightServiceAsync;
import com.sid.cloudynote.client.service.GroupService;
import com.sid.cloudynote.client.service.GroupServiceAsync;
import com.sid.cloudynote.client.service.InfoNoteSearchService;
import com.sid.cloudynote.client.service.InfoNoteSearchServiceAsync;
import com.sid.cloudynote.client.service.InfoNoteService;
import com.sid.cloudynote.client.service.InfoNoteServiceAsync;
import com.sid.cloudynote.client.service.NotebookService;
import com.sid.cloudynote.client.service.NotebookServiceAsync;
import com.sid.cloudynote.client.service.UserService;
import com.sid.cloudynote.client.service.UserServiceAsync;
import com.sid.cloudynote.client.sharing.view.interfaces.IAdminView;
import com.sid.cloudynote.shared.Group;
import com.sid.cloudynote.shared.InfoNote;
import com.sid.cloudynote.shared.Notebook;
import com.sid.cloudynote.shared.User;

public class AdminPresenter implements Presenter, IAdminView.Presenter {
	private IAdminView view;
	private HandlerManager eventBus;

	public AdminPresenter(IAdminView view, HandlerManager eventBus) {
		super();
		this.view = view;
		this.eventBus = eventBus;
	}

	@Override
	public void go(HasWidgets container) {
		this.loadGroupList();
		this.loadNotebookList();
		container.clear();
		container.add(view.asWidget());
		view.setSelectedGroup(new Group("All Users"));
	}

	@Override
	public void loadGroupList() {
		GroupServiceAsync groupService = GWT.create(GroupService.class);
		groupService.getMyGroups(AppController.get().getLoginInfo().getEmail(),
				new AsyncCallback<List<Group>>() {

					@Override
					public void onFailure(Throwable caught) {
						GWT.log("Failed to get group list");
					}

					@Override
					public void onSuccess(List<Group> result) {
						view.setGroupList(result);
					}

				});
	}

	@Override
	public void loadNotebookList() {
		NotebookServiceAsync service = (NotebookServiceAsync) GWT
				.create(NotebookService.class);
		service.getNotebooks(new AsyncCallback<List<Notebook>>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("getNotebooksList falied!");
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(List<Notebook> result) {
				if (result != null) {
					view.setNotebookList(result);
				}
			}
		});
	}

	@Override
	public void loadUserList(final Group group) {
		if (group.getKey() != null) {
			// get users in group
			GroupServiceAsync groupService = GWT.create(GroupService.class);
			groupService.getUsersInGroup(group.getKey(),
					new AsyncCallback<List<User>>() {

						@Override
						public void onFailure(Throwable caught) {
							GWT.log("Failed to get users in group:"
									+ group.getName());
						}

						@Override
						public void onSuccess(List<User> result) {
							view.setSubListLabel("Members of "
									+ group.getName());
							view.setUserList(result);
						}
					});
		} else {
			if ("All Users".equals(group.getName())) {
				UserServiceAsync service = GWT.create(UserService.class);
				service.getFriends(AppController.get().getLoginInfo()
						.getEmail(), new AsyncCallback<List<User>>() {

					@Override
					public void onFailure(Throwable caught) {
						GWT.log("Failed to get all users");
					}

					@Override
					public void onSuccess(List<User> result) {
						view.setSubListLabel("All Users");
						view.setUserList(result);
					}
				});
			} else if ("UnGrouped".equals(group.getName())) {
				// TODO show ungrouped users
			}
		}
	}

	@Override
	public void loadNoteList(final Notebook notebook) {
		if (notebook.getKey() != null) {
			// get notes in notebook
			InfoNoteServiceAsync noteService = GWT
					.create(InfoNoteService.class);
			noteService.getNotes(notebook, new AsyncCallback<List<InfoNote>>() {

				@Override
				public void onFailure(Throwable caught) {
					GWT.log("Failed to get notes in notebook:"
							+ notebook.getName());
				}

				@Override
				public void onSuccess(List<InfoNote> result) {
					view.setSubListLabel("Notes in " + notebook.getName());
					view.setNoteList(result);
				}

			});
		} else {
			if ("All Notes".equals(notebook.getName())) {
				// show all notes
				InfoNoteSearchServiceAsync service = GWT
						.create(InfoNoteSearchService.class);
				service.searchNotes("", new AsyncCallback<List<InfoNote>>() {

					@Override
					public void onFailure(Throwable caught) {
						GWT.log("Failed to load all notes");
					}

					@Override
					public void onSuccess(List<InfoNote> result) {
						view.setSubListLabel("All Notes");
						view.setNoteList(result);
					}
				});
			} else if ("UnGrouped".equals(notebook.getName())) {
				//TODO show ungrouped users
			}
		}
	}

	@Override
	public void onUserAccessItemSelected(Group group) {
		this.loadUserList(group);
		view.presentGroupAccess(group);
	}

	@Override
	public void onNotePermissionItemSelected(Notebook notebook) {
		this.loadNoteList(notebook);
		view.presentNotebookPermission(notebook);
	}

	@Override
	public void onUserItemSelected(User user) {
		view.presentUserAccess(user);
	}

	@Override
	public void onNoteItemSelected(InfoNote note) {
		view.presentNotePermission(note);
	}

	@Override
	public void saveUserAccessChanges(Group group,
			Map<Key, Integer> groupAccess, User user,
			Map<Key, Integer> userAccess) {
		AccessRightServiceAsync service = GWT.create(AccessRightService.class);
		service.saveGroupAndUserAccess(group, groupAccess, user, userAccess, new AsyncCallback<Void>(){

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Failed to save the user access changes.");
			}

			@Override
			public void onSuccess(Void result) {
				GWT.log("Successfully saved the user access changes.");
				eventBus.fireEvent(new NotebookChangedEvent());
				eventBus.fireEvent(new GroupsChangedEvent());
				eventBus.fireEvent(new NoteChangedEvent(DataManager.getCurrentNotebook()));
			}
		});
	}

	@Override
	public void saveNotePermissionChanges(Notebook notebook,
			Map<Key, Integer> notebookGroupPermission,
			Map<String, Integer> notebookUserPermission, InfoNote note,
			Map<Key, Integer> noteGroupPermission,
			Map<String, Integer> noteUserPermission) {
		AccessRightServiceAsync service = GWT.create(AccessRightService.class);
		service.saveNotebookAndNotePermission(notebook, notebookGroupPermission, notebookUserPermission, note, noteGroupPermission, noteUserPermission, new AsyncCallback<Void>(){

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Failed to save the note permission changes.");
			}

			@Override
			public void onSuccess(Void result) {
				GWT.log("Successfully saved the note permission changes.");
				eventBus.fireEvent(new NotebookChangedEvent());
				eventBus.fireEvent(new GroupsChangedEvent());
				eventBus.fireEvent(new NoteChangedEvent(DataManager.getCurrentNotebook()));
			}
		});
	}
}
