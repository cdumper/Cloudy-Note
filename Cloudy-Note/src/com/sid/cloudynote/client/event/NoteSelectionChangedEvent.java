package com.sid.cloudynote.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class NoteSelectionChangedEvent extends GwtEvent<INoteSelectionChangedHandler>{
	public static final Type<INoteSelectionChangedHandler> TYPE = new Type<INoteSelectionChangedHandler>();
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<INoteSelectionChangedHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(INoteSelectionChangedHandler handler) {
		handler.onNoteSelectionChanged(this);
	}

}
