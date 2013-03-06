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

	public void setContent(Widget content) {
		this.content = content;
	}

	public NotePresenter(NoteView view, HandlerManager eventBus) {
		super();
		this.eventBus = eventBus;
		this.view = view;
		NonEditableNoteView nonEditView = new NonEditableNoteView();
		NonEditableNotePresenter presenter = new NonEditableNotePresenter(nonEditView,eventBus);
		nonEditView.setPresenter(presenter);
		nonEditView.presentNote(DataManager.getCurrentNote());
		this.content = nonEditView;
	}

	@Override
	public void go(HasWidgets container) {
		container.clear();
		Widget widget = content.asWidget();
		widget.setHeight("100%");
		widget.setWidth("100%");
		container.add(widget);
	}

	// this function may be called when note selection changed
	@Override
	public void presentNote(InfoNote note) {
//		eventBus.fireEvent(new EditNoteDoneEvent());
		//TODO
		NonEditableNoteView nonEditView = new NonEditableNoteView();
		NonEditableNotePresenter presenter = new NonEditableNotePresenter(nonEditView,eventBus);
		nonEditView.setPresenter(presenter);
		nonEditView.presentNote(note);
		this.setContent(nonEditView);
		
		this.go(view.getContainer());
	}

	@Override
	public void editNote(InfoNote note) {
		EditableNoteView editView = new EditableNoteView();
		EditableNotePresenter presenter = new EditableNotePresenter(editView,eventBus);
		editView.setPresenter(presenter);
		editView.presentNote(note);
		editView.setNew(false);
		this.setContent(editView);
		
		this.go(view.getContainer());
	}

//	@Override
//	public void setView(NoteView view) {
//		this.view = view;
//	}

	@Override
	public void showNewNote() {
		EditableNoteView editView = new EditableNoteView();
		EditableNotePresenter presenter = new EditableNotePresenter(editView,eventBus);
		editView.setPresenter(presenter);
		editView.newNote();
		editView.setNew(true);
		this.setContent(editView);
		
		this.go(view.getContainer());
	}
}
