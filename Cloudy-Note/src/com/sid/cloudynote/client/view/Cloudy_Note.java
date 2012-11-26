package com.sid.cloudynote.client.view;

import java.util.List;

import com.axeiya.gwtckeditor.client.CKEditor;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.TreeListener;
import com.sid.cloudynote.client.model.Note;
import com.sid.cloudynote.client.service.AddNoteService;
import com.sid.cloudynote.client.service.AddNoteServiceAsync;
import com.sid.cloudynote.client.service.GetNoteService;
import com.sid.cloudynote.client.service.GetNoteServiceAsync;
import com.sid.cloudynote.client.service.GetNotesListService;
import com.sid.cloudynote.client.service.GetNotesListServiceAsync;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Cloudy_Note implements EntryPoint {
	private TextBox titleTextBox = new TextBox();
	private CKEditor ckeditor = new CKEditor();
	private Button sendButton = new Button("Send");
	private final DockPanel dockPanel = new DockPanel();
	private final SimplePanel simplePanel = new SimplePanel();
	private final Tree tree = new Tree();
	private final TreeItem trtmAllNotebooks = new TreeItem("All NoteBooks");
	private final TreeItem trtmPrivateNotebooks = new TreeItem(
			"Private NoteBooks");

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		cleanWidget();
		RootPanel rootPanel = RootPanel.get("main");

		rootPanel.add(dockPanel);
		HTMLTable panel = new FlexTable();
		dockPanel.add(panel, DockPanel.CENTER);
		ckeditor.setSize("720px", "360px");

		titleTextBox.setWidth("600px");

		panel.setText(0, 0, "Notes:");
		panel.getRowFormatter().addStyleName(0, "LogTitle");
		panel.setText(1, 0, "Title");
		panel.setWidget(2, 0, titleTextBox);
		panel.setText(3, 0, "Content");
		panel.setWidget(4, 0, ckeditor);
		panel.setWidget(5, 0, sendButton);

		dockPanel.add(simplePanel, DockPanel.WEST);

		simplePanel.setWidget(tree);
		tree.setSize("100%", "100%");

		tree.addItem(trtmAllNotebooks);
		tree.addItem(trtmPrivateNotebooks);
		tree.addTreeListener(new TreeListener(){
			@Override
			public void onTreeItemSelected(TreeItem item) {
				GetNoteServiceAsync getNoteService = GWT.create(GetNoteService.class);
				AsyncCallback<Note> callback = new AsyncCallback<Note>(){

					@Override
					public void onFailure(Throwable caught) {
						System.out.println("Failure! getNote");
						caught.printStackTrace();
					}

					@Override
					public void onSuccess(Note note) {
						titleTextBox.setText(note.getTitle());
						ckeditor.setData(note.getContent());
					}

				};
				System.out.println("select "+item.getText());
				getNoteService.getNote(item.getText(), callback);
			}

			@Override
			public void onTreeItemStateChanged(TreeItem item) {
				
			}
		});
		
		refreshNotesList();
		
		sendButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				saveNote(titleTextBox.getText(), ckeditor.getHTML());
				// saveNote(new Note(titleTextBox.getText(),
				// ckeditor.getHTML()));
			}
		});
	}

	private void cleanWidget() {
		ckeditor.setData("");
		titleTextBox.setText("");
	}

	private void saveNote(String title,String content) {
		AddNoteServiceAsync addNoteService = (AddNoteServiceAsync) GWT
				.create(AddNoteService.class);
//		ServiceDefTarget serviceDef = (ServiceDefTarget) addNoteService;
//		serviceDef.setServiceEntryPoint(GWT.getModuleBaseURL()
//				+ "AddNoteService");
		AsyncCallback<Void> callback = new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				System.out.println("falied! addNote");
			}

			@Override
			public void onSuccess(Void result) {
				Window.alert("success!!!!");
				refreshNotesList();
			}

		};
		addNoteService.addNote(title,content, callback);
	}

	protected void refreshNotesList() {
		GetNotesListServiceAsync getNotesService = (GetNotesListServiceAsync) GWT.create(GetNotesListService.class);
		AsyncCallback<List<Note>> callback = new AsyncCallback<List<Note>>() {

			@Override
			public void onFailure(Throwable caught) {
				System.out.println("falied! getNotesList");
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(List<Note> result) {
				trtmAllNotebooks.removeItems();
				for(Note note : result){
					TreeItem node = new TreeItem(note.getTitle());
					trtmAllNotebooks.addItem(node);
					trtmAllNotebooks.setState(true);
				}
			}

		};
		getNotesService.getNoteList(callback);
	}

//	private void saveNote(String title, String content) {
//
//		String url = GWT.getHostPageBaseURL() + "servlet?" + "OP=AddNote";
//
//		RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, url);
//		builder.setHeader("Content-Type", "text/x-gwt-rpc; charset=utf-8");
//
//		String data = "<Group>Title<Content>" + title
//				+ "<Group>Content<Content>" + content;
//		builder.setRequestData(data);
//
//		builder.setCallback(new RequestCallback() {
//
//			public void onError(Request request, Throwable exception) {
//				System.out.println("onError Couldn't retrieve JSON)");
//			}
//
//			public void onResponseReceived(Request request, Response response) {
//
//				if (200 == response.getStatusCode()) {
//					String retTest = response.getText();
//					if (retTest.equals("OK")) {
//						Window.alert("OK");
//						// getLogList();
//						cleanWidget();
//					}
//				} else {
//					Window.alert("No Response");
//				}
//
//			}
//		});
//
//		try {
//			builder.send();
//		} catch (RequestException e) {
//			e.printStackTrace();
//		}
//
//	}

}
