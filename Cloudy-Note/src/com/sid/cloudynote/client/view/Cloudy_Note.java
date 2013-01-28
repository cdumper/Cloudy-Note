package com.sid.cloudynote.client.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.datastore.Key;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.NotStrict;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.sid.cloudynote.client.model.DataManager;
import com.sid.cloudynote.client.model.InfoNote;
import com.sid.cloudynote.client.model.Notebook;
import com.sid.cloudynote.client.service.InfoNoteService;
import com.sid.cloudynote.client.service.InfoNoteServiceAsync;
import com.sid.cloudynote.client.service.NotebookService;
import com.sid.cloudynote.client.service.NotebookServiceAsync;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Cloudy_Note implements EntryPoint {
	interface Binder extends UiBinder<DockLayoutPanel, Cloudy_Note> {
	}

	interface GlobalResources extends ClientBundle {
		@NotStrict
		@Source("global.css")
		CssResource css();
	}

	static interface Images extends ClientBundle {
		ImageResource inbox();
	}

	private static final Binder binder = GWT.create(Binder.class);

	@UiField
	TopPanel topPanel;
	@UiField
	BottomPanel bottomPanel;
	@UiField
	NoteBookListPanel notebookListPanel;
	@UiField
	NoteListPanel noteListPanel;
	@UiField
	NoteViewPanel noteViewPanel;

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
//		preloadData();
		GWT.<GlobalResources> create(GlobalResources.class).css()
				.ensureInjected();
		DockLayoutPanel dockPanel = binder.createAndBindUi(this);
		
		// inject dependencies...
		topPanel.setNotebookPanel(notebookListPanel);
		topPanel.setNotePanel(noteListPanel);
		topPanel.setNoteViewPanel(noteViewPanel);
		notebookListPanel.setNotePanel(noteListPanel);
		noteListPanel.setNoteViewPanel(noteViewPanel);
		
		RootLayoutPanel rootPanel = RootLayoutPanel.get();
		rootPanel.add(dockPanel);

		Images images = GWT.create(Images.class);


		// HTMLTable editPanel = new FlexTable();
		// notePanel.addWest(new Tree(), 300);
		// notePanel.add(editPanel);
		// dockPanel.addNorth(topPanel, 10);
		// titleTextBox.setWidth("600px");
		//
		// editPanel.setText(0, 0, "Notes:");
		// editPanel.getRowFormatter().addStyleName(0, "LogTitle");
		// editPanel.setText(1, 0, "Title");
		// editPanel.setWidget(2, 0, titleTextBox);
		// editPanel.setText(3, 0, "Content");
		// editPanel.setWidget(4, 0, ckeditor);
		// editPanel.setWidget(5, 0, sendButton);

		// tree.addTreeListener(new TreeListener() {
		// @Override
		// public void onTreeItemSelected(TreeItem item) {
		// GetNoteServiceAsync getNoteService = GWT
		// .create(GetNoteService.class);
		// AsyncCallback<Note> callback = new AsyncCallback<Note>() {
		//
		// @Override
		// public void onFailure(Throwable caught) {
		// System.out.println("Failure! getNote");
		// caught.printStackTrace();
		// }
		//
		// @Override
		// public void onSuccess(Note note) {
		// titleTextBox.setText(note.getTitle());
		// ckeditor.setData(note.getContent());
		// }
		//
		// };
		// System.out.println("select " + item.getText());
		// getNoteService.getNote(item.getText(), callback);
		// }
		//
		// @Override
		// public void onTreeItemStateChanged(TreeItem item) {
		//
		// }
		// });

		// refreshNotesList();
		//
		// sendButton.addClickHandler(new ClickHandler() {
		// public void onClick(ClickEvent event) {
		// saveNote(titleTextBox.getText(), ckeditor.getHTML());
		// }
		// });
	}

private void preloadData() {
	loadNotebooks();
	loadNotes();
}

private void loadNotes() {
	InfoNoteServiceAsync service = (InfoNoteServiceAsync) GWT
			.create(InfoNoteService.class);
	AsyncCallback<List<InfoNote>> callback = new AsyncCallback<List<InfoNote>>() {

		@Override
		public void onFailure(Throwable caught) {
			System.out.println("falied! getNotesList");
			caught.printStackTrace();
		}

		@Override
		public void onSuccess(List<InfoNote> result) {
			if (result.size() != 0) {
				Map<Key,InfoNote> noteMap = new HashMap<Key,InfoNote>();
				for(InfoNote note : result){
					noteMap.put(note.getKey(), note);
				}
				DataManager.setNotes(noteMap);
				DataManager.setCurrentNote(result.get(0).getKey());
			} else {
				DataManager.setNotes(null);
				System.out.println("No notes exist!");
			}
		}
	};
	service.getPaginationData(callback);
}

private void loadNotebooks() {
	NotebookServiceAsync service = (NotebookServiceAsync) GWT
			.create(NotebookService.class);
	AsyncCallback<List<Notebook>> callback = new AsyncCallback<List<Notebook>>() {

		@Override
		public void onFailure(Throwable caught) {
			System.out.println("falied! getNotebooksList");
			caught.printStackTrace();
		}

		@Override
		public void onSuccess(List<Notebook> result) {
			System.out.println("preget notebook list data");
			
			if (result.size() != 0) {
				Map<Key,Notebook> notebookMap = new HashMap<Key,Notebook>();
				for(Notebook notebook : result){
					notebookMap.put(notebook.getKey(), notebook);
				}
				DataManager.setNotebooks(notebookMap);
				DataManager.setCurrentNotebook(result.get(0).getKey());
			} else {
				DataManager.setNotebooks(null);
				System.out.println("No notebooks exist!");
			}
			System.out.println("notebooks number: "+DataManager.getNotebooks().size());
		}
	};
	service.getPaginationData(callback);
}

	// private void saveNote(String title, String content) {
	// AddNoteServiceAsync addNoteService = (AddNoteServiceAsync) GWT
	// .create(AddNoteService.class);
	// AsyncCallback<Void> callback = new AsyncCallback<Void>() {
	//
	// @Override
	// public void onFailure(Throwable caught) {
	// System.out.println("falied! addNote");
	// }
	//
	// @Override
	// public void onSuccess(Void result) {
	// Window.alert("success!!!!");
	// refreshNotesList();
	// }
	//
	// };
	// addNoteService.addNote(title, content, callback);
	// }

	// protected void refreshNotesList() {
	// GetNotesListServiceAsync getNotesService = (GetNotesListServiceAsync) GWT
	// .create(GetNotesListService.class);
	// AsyncCallback<List<Note>> callback = new AsyncCallback<List<Note>>() {
	//
	// @Override
	// public void onFailure(Throwable caught) {
	// System.out.println("falied! getNotesList");
	// caught.printStackTrace();
	// }
	//
	// @Override
	// public void onSuccess(List<Note> result) {
	// trtmAllNotebooks.removeItems();
	// for (Note note : result) {
	// TreeItem node = new TreeItem(note.getTitle());
	// trtmAllNotebooks.addItem(node);
	// trtmAllNotebooks.setState(true);
	// }
	// }
	//
	// };
	// getNotesService.getNoteList(callback);
	// }
}
