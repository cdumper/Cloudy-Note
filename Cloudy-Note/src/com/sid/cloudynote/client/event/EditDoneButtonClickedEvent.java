package com.sid.cloudynote.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class EditDoneButtonClickedEvent extends GwtEvent<IEditDoneButtonClickedHandler>{
	public static final Type<IEditDoneButtonClickedHandler> TYPE = new Type<IEditDoneButtonClickedHandler>();
	
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<IEditDoneButtonClickedHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(IEditDoneButtonClickedHandler handler) {
		handler.onEditDoneButtonClicked(this);
	}

}
