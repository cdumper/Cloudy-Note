package com.sid.cloudynote.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class EditNoteEvent extends GwtEvent<IEditNoteHandler>{
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
