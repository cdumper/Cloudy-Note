package com.sid.cloudynote.client.sharing.presenter;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.sid.cloudynote.client.AppController;
import com.sid.cloudynote.client.presenter.Presenter;
import com.sid.cloudynote.client.service.GroupService;
import com.sid.cloudynote.client.service.GroupServiceAsync;
import com.sid.cloudynote.client.service.InfoNoteService;
import com.sid.cloudynote.client.service.InfoNoteServiceAsync;
import com.sid.cloudynote.client.service.NotebookService;
import com.sid.cloudynote.client.service.NotebookServiceAsync;
import com.sid.cloudynote.client.sharing.view.interfaces.IAdminView;
import com.sid.cloudynote.shared.Group;
import com.sid.cloudynote.shared.InfoNote;
import com.sid.cloudynote.shared.Notebook;
import com.sid.cloudynote.shared.User;

public class AdminPresenter implements Presenter, IAdminView.Presenter{
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
	}

	@Override
	public void loadGroupList() {
		GroupServiceAsync groupService = GWT.create(GroupService.class);
		groupService.getMyGroups(AppController.get().getLoginInfo().getEmail(), new AsyncCallback<List<Group>>(){

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
			groupService.getUsersInGroup(group.getKey(), new AsyncCallback<List<User>>(){

				@Override
				public void onFailure(Throwable caught) {
					GWT.log("Failed to get users in group:"+group.getName());
				}

				@Override
				public void onSuccess(List<User> result) {
					view.setUserList(result);
				}
				
			});
		} else {
			if ("All Users".equals(group.getName())) {
				// TODO show all users
			} else if ("UnGrouped".equals(group.getName())) {
				// TODO show ungrouped users
			}
		}
	}

	@Override
	public void loadNoteList(final Notebook notebook) {
		if (notebook.getKey() != null) {
			// get notes in notebook
			InfoNoteServiceAsync noteService = GWT.create(InfoNoteService.class);
			noteService.getNotes(notebook, new AsyncCallback<List<InfoNote>>(){

				@Override
				public void onFailure(Throwable caught) {
					GWT.log("Failed to get notes in notebook:"+notebook.getName());
				}

				@Override
				public void onSuccess(List<InfoNote> result) {
					view.setNoteList(result);
				}
				
			});
		} else {
			if ("All Notes".equals(notebook.getName())) {
				// TODO show all notes
			}
		}
	}

	@Override
	public void onUserAccessItemSelected(Group group) {
		this.loadUserList(group);
	}

	@Override
	public void onNotePermissionItemSelected(Notebook notebook) {
		this.loadNoteList(notebook);
	}

	@Override
	public void onUserItemSelected(User user) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNoteItemSelected(InfoNote note) {
		// TODO Auto-generated method stub
		
	}
}
