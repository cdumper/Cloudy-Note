package com.sid.cloudynote.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.sid.cloudynote.client.event.interfaces.INoteSelectionChangedHandler;
import com.sid.cloudynote.shared.InfoNote;

public class NoteSelectionChangedEvent extends GwtEvent<INoteSelectionChangedHandler>{
	public static final Type<INoteSelectionChangedHandler> TYPE = new Type<INoteSelectionChangedHandler>();
	private InfoNote clickedItem;
	public InfoNote getClickedItem() {
		return clickedItem;
	}

	public void setClickedItem(InfoNote clickedItem) {
		this.clickedItem = clickedItem;
	}

	public NoteSelectionChangedEvent(InfoNote clickedItem) {
		super();
		this.clickedItem = clickedItem;
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<INoteSelectionChangedHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(INoteSelectionChangedHandler handler) {
		System.out.println(this.getClass().getName());
		handler.onNoteSelectionChanged(this);
	}

}
