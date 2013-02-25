package com.sid.cloudynote.client.event.interfaces;

import com.google.gwt.event.shared.EventHandler;
import com.sid.cloudynote.client.event.EditNoteEvent;

public interface IEditNoteHandler extends EventHandler{
	void onEditNote(EditNoteEvent event);
}
