package com.sid.cloudynote.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.sid.cloudynote.client.event.interfaces.IEditNoteDoneHandler;

public class EditNoteDoneEvent extends GwtEvent<IEditNoteDoneHandler> {
	public static final Type<IEditNoteDoneHandler> TYPE = new Type<IEditNoteDoneHandler>();

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<IEditNoteDoneHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(IEditNoteDoneHandler handler) {
		System.out.println(this.getClass().getName());
		handler.onEditNoteDone(this);
	}

}
