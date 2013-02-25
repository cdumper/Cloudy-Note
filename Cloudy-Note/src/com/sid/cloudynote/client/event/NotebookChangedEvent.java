package com.sid.cloudynote.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.sid.cloudynote.client.event.interfaces.INotebookChangedHandler;

public class NotebookChangedEvent extends GwtEvent<INotebookChangedHandler> {

	public static final Type<INotebookChangedHandler> TYPE = new Type<INotebookChangedHandler>();

	@Override
	protected void dispatch(INotebookChangedHandler handler) {
		handler.onNotebookChanged(this);
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<INotebookChangedHandler> getAssociatedType() {
		return TYPE;
	}
}