package com.sid.cloudynote.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface INoteSelectionChangedHandler extends EventHandler{
	void onNoteSelectionChanged(NoteSelectionChangedEvent event);
}
