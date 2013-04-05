package com.sid.cloudynote.client.sharing.presenter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.datastore.Key;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.sid.cloudynote.client.AppController;
import com.sid.cloudynote.client.DataManager;
import com.sid.cloudynote.client.presenter.Presenter;
import com.sid.cloudynote.client.service.GroupService;
import com.sid.cloudynote.client.service.GroupServiceAsync;
import com.sid.cloudynote.client.service.InfoNoteService;
import com.sid.cloudynote.client.service.InfoNoteServiceAsync;
import com.sid.cloudynote.client.sharing.view.interfaces.ISharingView;
import com.sid.cloudynote.shared.Group;
import com.sid.cloudynote.shared.InfoNote;

public class SharingPresenter implements Presenter, ISharingView.Presenter {
	private ISharingView view;
	private HandlerManager eventBus;

	public SharingPresenter(ISharingView view, HandlerManager eventBus) {
		super();
		this.view = view;
		this.eventBus = eventBus;
		this.loadGroupList();
	}

	@Override
	public void go(HasWidgets container) {
		container.clear();
		container.add(view.asWidget());
	}

	@Override
	public void viewPublicNotes() {
		InfoNoteServiceAsync service = (InfoNoteServiceAsync) GWT
				.create(InfoNoteService.class);
		AsyncCallback<List<InfoNote>> callback = new AsyncCallback<List<InfoNote>>() {
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Get public NotesList falied!");
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(List<InfoNote> result) {
				view.setNoteList(result);
			}
		};
		service.getPublicNotes(callback);
	}

	@Override
	public void viewSharedWithMeNotes() {
		InfoNoteServiceAsync service = (InfoNoteServiceAsync) GWT
				.create(InfoNoteService.class);
		AsyncCallback<List<InfoNote>> callback = new AsyncCallback<List<InfoNote>>() {
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Get shared NotesList falied!");
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(List<InfoNote> result) {
				view.setNoteList(result);
			}
		};
		service.getSharedNotes(AppController.get().getLoginInfo().getEmail(), callback);
	}

	@Override
	public void loadGroupList() {
		final String email = AppController.get().getLoginInfo().getEmail();
		GroupServiceAsync groupService = GWT.create(GroupService.class);
		groupService.getGroups(email, new AsyncCallback<List<Group>>() {
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Failed to load group list of user : " + email);
			}

			@Override
			public void onSuccess(List<Group> result) {
				view.setGroupList(result);

				Map<Key, Group> allGroups = new HashMap<Key, Group>();
				for (Group group : result) {
					allGroups.put(group.getKey(), group);
				}
				DataManager.setAllGroups(allGroups);
			}
		});
	}

	@Override
	public void onGroupItemSelected(final Group group) {
		InfoNoteServiceAsync service = GWT.create(InfoNoteService.class);
		service.getNotesInGroup(group.getKey(),
				new AsyncCallback<List<InfoNote>>() {

					@Override
					public void onFailure(Throwable caught) {
						GWT.log("Failed to get note list in group:"
								+ group.getName());
					}

					@Override
					public void onSuccess(List<InfoNote> result) {
						GWT.log("Successfully got note list in group:"
								+ group.getName());
						view.setNoteList(result);
					}
				});
	}

	@Override
	public void onClickEdit() {
		final InfoNote infoNote = view.getSeletedNote();
		InfoNoteServiceAsync service = GWT.create(InfoNoteService.class);
		service.verifyEditAccess(infoNote, new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Failed to verify the edit access");
			}

			@Override
			public void onSuccess(Boolean result) {
				if (result) {
					view.editNote(infoNote);
				} else {
					view.showAccessDeniedPanel();
				}
			}
		});
	}

	@Override
	public void backToList() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClickDone() {
		InfoNoteServiceAsync service = GWT.create(InfoNoteService.class);
		service.modify(view.getModifiedNoteData(), new AsyncCallback<InfoNote>(){

			@Override
			public void onFailure(Throwable caught) {
				GWT.log(caught.getMessage());
			}

			@Override
			public void onSuccess(InfoNote result) {
				view.updateNote(result);
				view.presentNote(result);
			}
		});
	}

	@Override
	public void searchNotes(String text) {
		// TODO search sharing notes
		System.out.println("Search: "+text);
	}
}
