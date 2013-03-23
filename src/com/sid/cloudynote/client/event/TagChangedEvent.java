package com.sid.cloudynote.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.sid.cloudynote.client.event.interfaces.ITagChangedHandler;

public class TagChangedEvent extends GwtEvent<ITagChangedHandler>{
	public static final Type<ITagChangedHandler> TYPE = new Type<ITagChangedHandler>();

	@Override
	public Type<ITagChangedHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ITagChangedHandler handler) {
		System.out.println(this.getClass().getName());
		handler.onTagChanged(this);
	}
}
