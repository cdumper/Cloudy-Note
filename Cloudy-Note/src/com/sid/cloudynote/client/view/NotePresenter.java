package com.sid.cloudynote.client.view;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.sid.cloudynote.client.event.EditNoteDoneEvent;
import com.sid.cloudynote.client.event.EditNoteEvent;
import com.sid.cloudynote.client.presenter.Presenter;

public class NotePresenter extends SimplePanel implements Presenter,
		INoteView.Presenter {
	private final HandlerManager eventBus;
	private Widget view;
	private boolean isEditing = false;

	public NotePresenter(Widget view, HandlerManager eventBus) {
		super();
		this.isEditing = false;
		this.eventBus = eventBus;
		this.view = view;
	}

	@Override
	public boolean isEditing() {
		return isEditing;
	}

	@Override
	public void setEditing(boolean isEditing) {
		this.isEditing = isEditing;
	}

	@Override
	public void go(HasWidgets container) {
		container.clear();
		Widget widget = view.asWidget();
		widget.setHeight("100%");
		widget.setWidth("100%");
		container.add(widget);
	}

	@Override
	public void saveNote() {
		// TODO Auto-generated method stub
		EditPanel panel = (EditPanel) view;
		panel.createNewNote();
		eventBus.fireEvent(new EditNoteDoneEvent());
	}

	@Override
	public void stopEdit() {
		saveNote();
	}

	@Override
	public void startEdit() {
		eventBus.fireEvent(new EditNoteEvent());
	}

	@Override
	public void setView(Widget view) {
		this.view = view;
	}
}
