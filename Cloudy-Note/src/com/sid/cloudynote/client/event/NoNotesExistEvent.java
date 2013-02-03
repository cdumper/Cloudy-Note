package com.sid.cloudynote.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class NoNotesExistEvent extends GwtEvent<INoNotesExistHandler>{
	public static final Type<INoNotesExistHandler> TYPE = new Type<INoNotesExistHandler>();
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<INoNotesExistHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(INoNotesExistHandler handler) {
		handler.onNotesExistEvent(this);
	}

}
