package com.sid.cloudynote.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface IEditNoteHandler extends EventHandler{
	void onEditNote(EditNoteEvent event);
}
