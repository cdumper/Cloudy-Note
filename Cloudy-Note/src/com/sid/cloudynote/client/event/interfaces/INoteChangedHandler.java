package com.sid.cloudynote.client.event.interfaces;

import com.google.gwt.event.shared.EventHandler;
import com.sid.cloudynote.client.event.NoteChangedEvent;

public interface INoteChangedHandler extends EventHandler{
	void onNoteChanged(NoteChangedEvent event);
}
