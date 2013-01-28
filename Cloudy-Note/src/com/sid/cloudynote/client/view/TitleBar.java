package com.sid.cloudynote.client.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.datastore.Key;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.sid.cloudynote.client.model.DataManager;
import com.sid.cloudynote.client.model.Notebook;

public class TitleBar extends Composite {

	private static TitleBarUiBinder uiBinder = GWT
			.create(TitleBarUiBinder.class);

	interface TitleBarUiBinder extends UiBinder<Widget, TitleBar> {
	}
	private Map<Key, Notebook> notebookMap;
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

	public void setSelectedNotebook(Key key) {
		for (int i = 0; i < notebookList.size(); i++) {
			if (notebookList.get(i).equals(key)) {
				notebook.setSelectedIndex(i);
				break;
			}
		}
	}

	private void loadNotebooks() {
		notebookMap = DataManager.getNotebooks();
		for (Notebook nb : notebookMap.values()) {
			notebookList.add(nb.getKey());
			notebook.addItem(nb.getName());
		}
		setSelectedNotebook(DataManager.getCurrentNotebookKey());
	}

	@UiHandler("attach")
	void onClick(ClickEvent e) {
		// TODO attachments
		Window.alert("TODO...Attach files!");
	}

	public Notebook getSelectedNotebook() {
		return notebookMap.get(notebookList.get(notebook.getSelectedIndex()));
	}
}