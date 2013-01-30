package com.sid.cloudynote.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class EditNoteDoneEvent extends GwtEvent<IEditNoteDoneHandler> {
	public static final Type<IEditNoteDoneHandler> TYPE = new Type<IEditNoteDoneHandler>();

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<IEditNoteDoneHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(IEditNoteDoneHandler handler) {
		handler.onEditNoteDone(this);
	}

}
