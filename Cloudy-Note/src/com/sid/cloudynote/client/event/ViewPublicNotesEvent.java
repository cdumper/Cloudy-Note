package com.sid.cloudynote.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.sid.cloudynote.client.event.interfaces.IViewPublicHandler;

public class ViewPublicNotesEvent extends GwtEvent<IViewPublicHandler>{
	public static final Type<IViewPublicHandler> TYPE = new Type<IViewPublicHandler>();
	@Override
	public Type<IViewPublicHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(IViewPublicHandler handler) {
		handler.onViewPublicNotes(this);
	}
}
