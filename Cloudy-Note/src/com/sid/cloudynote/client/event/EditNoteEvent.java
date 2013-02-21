package com.sid.cloudynote.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.sid.cloudynote.shared.InfoNote;

public class EditNoteEvent extends GwtEvent<IEditNoteHandler>{
	private InfoNote note;
	public InfoNote getNote() {
		return note;
	}

	public void setNote(InfoNote note) {
		this.note = note;
	}

	public EditNoteEvent(InfoNote note) {
		super();
		this.note = note;
	}

	public static final Type<IEditNoteHandler> TYPE = new Type<IEditNoteHandler>();
	
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<IEditNoteHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(IEditNoteHandler handler) {
		handler.onEditNote(this);
	}

}
