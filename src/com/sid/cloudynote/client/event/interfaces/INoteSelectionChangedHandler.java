package com.sid.cloudynote.client.event.interfaces;

import com.google.gwt.event.shared.EventHandler;
import com.sid.cloudynote.client.event.NoteSelectionChangedEvent;

public interface INoteSelectionChangedHandler extends EventHandler{
	void onNoteSelectionChanged(NoteSelectionChangedEvent event);
}
