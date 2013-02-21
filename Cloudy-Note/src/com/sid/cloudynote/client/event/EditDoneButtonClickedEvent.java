package com.sid.cloudynote.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.sid.cloudynote.shared.InfoNote;

public class EditDoneButtonClickedEvent extends GwtEvent<IEditDoneButtonClickedHandler>{
	private InfoNote note;
	public InfoNote getNote() {
		return note;
	}

	public void setNote(InfoNote note) {
		this.note = note;
	}

	public EditDoneButtonClickedEvent(InfoNote note) {
		super();
		this.note = note;
	}

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
