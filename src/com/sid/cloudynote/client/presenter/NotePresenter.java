package com.sid.cloudynote.client.presenter;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.sid.cloudynote.client.DataManager;
import com.sid.cloudynote.client.view.EditableNoteView;
import com.sid.cloudynote.client.view.NonEditableNoteView;
import com.sid.cloudynote.client.view.NoteView;
import com.sid.cloudynote.client.view.interfaces.INoteView;
import com.sid.cloudynote.shared.InfoNote;

public class NotePresenter extends SimplePanel implements Presenter,
		INoteView.Presenter {
	private final HandlerManager eventBus;
	private NoteView view;
	private Widget content;
	private NonEditableNoteView nonEditView; 
	private EditableNoteView editView;
	private EditableNotePresenter editablePresenter;
	private NonEditableNotePresenter nonEditablePresenter;

	public void setContent(Widget content) {
		this.content = content;
	}
	
	@Override
	public void go(HasWidgets container) {
		container.clear();
		Widget widget = content.asWidget();
		widget.setHeight("100%");
		widget.setWidth("100%");
		container.add(widget);
	}

	public NotePresenter(NoteView view, HandlerManager eventBus) {
		super();
		this.eventBus = eventBus;
		this.view = view;
		if(nonEditView==null)	nonEditView = new NonEditableNoteView();
		if(nonEditablePresenter==null)	nonEditablePresenter = new NonEditableNotePresenter(nonEditView,eventBus);
		nonEditView.setPresenter(nonEditablePresenter);
		nonEditView.setNote(DataManager.getCurrentNote());
		nonEditView.presentNote();
		this.content = nonEditView;
	}

	@Override
	public void presentNote(InfoNote note) {
		if(nonEditView==null)	nonEditView = new NonEditableNoteView();
		if(nonEditablePresenter==null)	nonEditablePresenter = new NonEditableNotePresenter(nonEditView,eventBus);
		nonEditView.setPresenter(nonEditablePresenter);
		nonEditView.setNote(note);
		nonEditView.presentNote();
		this.setContent(nonEditView);
		
		this.go(view.getContainer());
	}

	@Override
	public void editNote(InfoNote note) {
		if(editView==null)	editView = new EditableNoteView();
		if(editablePresenter==null)	editablePresenter = new EditableNotePresenter(editView,eventBus);
		editView.setPresenter(editablePresenter);
		editView.setNote(note);
		editView.presentNote();
//		editView.presentNote(note);
		editView.setNew(false);
		this.setContent(editView);
		
		this.go(view.getContainer());
	}

	@Override
	public void showNewNote() {
		if(editView==null)	editView = new EditableNoteView();
		if(editablePresenter==null)	editablePresenter = new EditableNotePresenter(editView,eventBus);
		editView.setPresenter(editablePresenter);
		editView.newNote();
		editView.setNew(true);
		this.setContent(editView);
		
		this.go(view.getContainer());
	}
}
