package com.sid.cloudynote.client.event;

import java.util.List;

import com.google.gwt.event.shared.GwtEvent;
import com.sid.cloudynote.client.event.interfaces.IPresentNotesHandler;
import com.sid.cloudynote.shared.InfoNote;

public class PresentNotesEvent extends GwtEvent<IPresentNotesHandler>{
	private String message;
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	private List<InfoNote> notes;
	public List<InfoNote> getNotes() {
		return notes;
	}

	public void setNotes(List<InfoNote> notes) {
		this.notes = notes;
	}

	public PresentNotesEvent(String message, List<InfoNote> notes) {
		super();
		this.message = message;
		this.notes = notes;
	}

	public static final Type<IPresentNotesHandler> TYPE = new Type<IPresentNotesHandler>();
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<IPresentNotesHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(IPresentNotesHandler handler) {
		System.out.println(this.getClass().getName());
		handler.onPresentNotes(this);
	}

}
