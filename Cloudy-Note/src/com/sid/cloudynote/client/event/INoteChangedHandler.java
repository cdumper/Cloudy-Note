package com.sid.cloudynote.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface INoteChangedHandler extends EventHandler{
	void onNoteChanged(NoteChangedEvent event);
}
