package com.sid.cloudynote.client.presenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.datastore.Key;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.sid.cloudynote.client.DataManager;
import com.sid.cloudynote.client.event.EditDoneButtonClickedEvent;
import com.sid.cloudynote.client.event.NoNotesExistEvent;
import com.sid.cloudynote.client.event.NoteSelectionChangedEvent;
import com.sid.cloudynote.client.service.InfoNoteService;
import com.sid.cloudynote.client.service.InfoNoteServiceAsync;
import com.sid.cloudynote.client.service.UserService;
import com.sid.cloudynote.client.service.UserServiceAsync;
import com.sid.cloudynote.client.view.NoteListView;
import com.sid.cloudynote.client.view.interfaces.INoteListView;
import com.sid.cloudynote.shared.InfoNote;
import com.sid.cloudynote.shared.Notebook;

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
	public void onNoteItemRightClicked(InfoNote clickedItem) {
		// TODO show context menu to be able to delete note
	}

	@Override
	public void loadNoteList(final Notebook notebook) {
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
		eventBus.fireEvent(new EditDoneButtonClickedEvent(value));
	}

	@Override
	public void shareNoteToUser(String email, InfoNote note, String permission) {
		int _permission = 1;
		if ("Read-Only".equals(permission)) {
			_permission = 1;
		} else if ("Write".equals(permission)) {
			_permission = 2;
		}
		
		// TODO perform share notes
		// Add entry in Note acl
		// note.getAccess().put("sid@shen.com",1);
		ArrayList<String> users = new ArrayList<String>();
		users.add(email);
		InfoNoteServiceAsync noteService = GWT.create(InfoNoteService.class);
		noteService.addAccessEntry(note,users,_permission, new AsyncCallback<Void>(){
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Shared notes failed");
			}

			@Override
			public void onSuccess(Void result) {
				
			}
		});

		// Add entry in User acl
		UserServiceAsync userService = GWT.create(UserService.class);
		ArrayList<Key> notes = new ArrayList<Key>();
		notes.add(note.getKey());
		userService.addAccessEntry(email, notes, _permission, new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Share notes failed");
			}

			@Override
			public void onSuccess(Void result) {

			}
		});
	}

	@Override
	public void makeNotesPublic(List<InfoNote> notes) {
		// TODO Auto-generated method stub
		InfoNoteServiceAsync service = GWT.create(InfoNoteService.class);
		service.makeNotesPublic(notes, new AsyncCallback<Void>(){
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Failed to make notes public");
			}

			@Override
			public void onSuccess(Void result) {
				
			}
		});
	}
}
