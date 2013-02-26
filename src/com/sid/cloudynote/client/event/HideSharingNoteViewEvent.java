package com.sid.cloudynote.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.sid.cloudynote.client.event.interfaces.IHideSharingNoteViewHandler;

public class HideSharingNoteViewEvent extends GwtEvent<IHideSharingNoteViewHandler> {
	public static final Type<IHideSharingNoteViewHandler> TYPE = new Type<IHideSharingNoteViewHandler>();
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<IHideSharingNoteViewHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(IHideSharingNoteViewHandler handler) {
		handler.onHideSharingNoteView(this);
	}

}
