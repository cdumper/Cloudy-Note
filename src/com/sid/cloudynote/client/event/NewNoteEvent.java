package com.sid.cloudynote.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.sid.cloudynote.client.event.interfaces.INewNoteHandler;

public class NewNoteEvent extends GwtEvent<INewNoteHandler>{
	
	public NewNoteEvent() {
		super();
	}

	public static final Type<INewNoteHandler> TYPE = new Type<INewNoteHandler>();
	
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<INewNoteHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(INewNoteHandler handler) {
		handler.onNewNote(this);
	}
}
