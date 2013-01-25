package com.sid.cloudynote.client.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.datastore.Key;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.sid.cloudynote.client.model.DataManager;
import com.sid.cloudynote.client.model.Notebook;
import com.sid.cloudynote.client.service.NotebookService;
import com.sid.cloudynote.client.service.NotebookServiceAsync;

public class TitleBar extends Composite {

	private static TitleBarUiBinder uiBinder = GWT
			.create(TitleBarUiBinder.class);

	interface TitleBarUiBinder extends UiBinder<Widget, TitleBar> {
	}

	private List<Key> notebookList = new ArrayList<Key>();
	
	@UiField
	TextBox title;
	@UiField
	ListBox notebook;
	@UiField
	Anchor attach;

	public TitleBar() {
		initWidget(uiBinder.createAndBindUi(this));
		loadNotebooks();
		// List<Notebook> notebooks = DataManager.getNotebooks();
		// for(Notebook nb : notebooks){
		// System.out.println(nb.getName());
		// notebook.addItem(nb.getName());
		// }
		title.setText("new note");
	}
	
	public void setSelectedNotebook(Key key){
		for(int i=0;i<notebookList.size();i++){
			if(notebookList.get(i).equals(key)){
				notebook.setSelectedIndex(i);
				break;
			}
		}
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
				if (result.size() != 0) {
					Map<Key, Notebook> notebookMap = new HashMap<Key, Notebook>();
					for (Notebook notebook : result) {
						notebookMap.put(notebook.getKey(), notebook);
					}
					DataManager.setNotebooks(notebookMap);
					DataManager.setCurrentNotebook(result.get(0).getKey());
					for(int i=0;i<result.size();i++){
						notebookList.add(i, result.get(i).getKey());
						notebook.addItem(result.get(i).getName());
					}
					setSelectedNotebook(DataManager.getCurrentNotebookKey());
					
//					for (Notebook nb : result) {
//						//allNotes.addItem(new TreeItem(notebook.getName()));
//						notebook.addItem(nb.getName());
//					}
				} else {
					DataManager.setNotebooks(null);
					System.out.println("No notebooks exist!");
				}
			}
		};
		service.getPaginationData(callback);
	}

	@UiHandler("attach")
	void onClick(ClickEvent e) {
		Window.alert("TODO...Attach files!");
	}
}
