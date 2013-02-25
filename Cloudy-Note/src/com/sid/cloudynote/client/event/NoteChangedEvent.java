package com.sid.cloudynote.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.sid.cloudynote.client.event.interfaces.INoteChangedHandler;
import com.sid.cloudynote.shared.Notebook;

public class NoteChangedEvent extends GwtEvent<INoteChangedHandler> {
	private Notebook notebook;
	public Notebook getNotebook() {
		return notebook;
	}

	public void setNotebook(Notebook notebook) {
		this.notebook = notebook;
	}

	public NoteChangedEvent(Notebook notebook) {
		super();
		this.notebook = notebook;
	}

	public static final Type<INoteChangedHandler> TYPE = new Type<INoteChangedHandler>();

	@Override
	protected void dispatch(INoteChangedHandler handler) {
		handler.onNoteChanged(this);
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<INoteChangedHandler> getAssociatedType() {
		return TYPE;
	}
}