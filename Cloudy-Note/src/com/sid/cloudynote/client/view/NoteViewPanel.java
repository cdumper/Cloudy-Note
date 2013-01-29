package com.sid.cloudynote.client.view;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.sid.cloudynote.client.DataManager;
import com.sid.cloudynote.client.service.InfoNoteService;
import com.sid.cloudynote.client.service.InfoNoteServiceAsync;
import com.sid.cloudynote.shared.InfoNote;
import com.sid.cloudynote.shared.Notebook;

public class NoteViewPanel extends SimplePanel {
	private Widget panel;
	private boolean isEditing = false;

	public boolean isEditing() {
		return isEditing;
	}

	private EditPanel editPanel;
	private NonEditPanel nonEditPanel = new NonEditPanel();

	public Widget getPanel() {
		return panel;
	}

	public void setPanel(Widget panel) {
		this.panel = panel;
		this.panel.setSize("100%", "100%");
		this.clear();
		this.add(panel);
		
		if (panel instanceof EditPanel) {
			editPanel = (EditPanel) panel;
		} else if (panel instanceof NonEditPanel) {
			nonEditPanel = (NonEditPanel) panel;
		} else {
			GWT.log("Can only add EditPanel or NonEditPanel");
		}
	}

	public NoteViewPanel() {
		super();
		setPanel(nonEditPanel);
	}

	public void newNote() {
		// TODO Auto-generated method stub
		if (editPanel == null) {
			editPanel = new EditPanel();
			editPanel.newNote();
		}
		this.setPanel(editPanel);
		this.isEditing = true;
	}

	public void stopEdit() {
//		this.nonEditPanel.setNote(DataManager.getCurrentNote());
		this.setPanel(nonEditPanel);
		this.isEditing = false;
	}

	public void startEdit() {
		if (editPanel == null) {
			editPanel = new EditPanel();
		}
		this.setPanel(editPanel);
		this.editPanel.presentNote(DataManager.getCurrentNote());
		this.isEditing = true;
	}

	public void presentNote(InfoNote note) {
		this.nonEditPanel.presentNote(note);
	}
	
	public void createNewNote(){
		editPanel.createNewNote();
	}
	
	public void updateNote(){
		editPanel.updateNote();
	}
}
