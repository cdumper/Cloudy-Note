package com.sid.cloudynote.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.sid.cloudynote.client.event.interfaces.IViewSharedNoteHandler;
import com.sid.cloudynote.shared.InfoNote;

public class ViewSharedNoteEvent extends GwtEvent<IViewSharedNoteHandler>{
	private InfoNote note;
	public InfoNote getNote() {
		return note;
	}

	public void setNote(InfoNote note) {
		this.note = note;
	}

	public ViewSharedNoteEvent(InfoNote note) {
		super();
		this.note = note;
	}

	public static final Type<IViewSharedNoteHandler> TYPE = new Type<IViewSharedNoteHandler>();
	
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<IViewSharedNoteHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(IViewSharedNoteHandler handler) {
		System.out.println(this.getClass().getName());
		handler.onViewSharedNote(this);
	}

}
