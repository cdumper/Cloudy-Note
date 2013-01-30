package com.sid.cloudynote.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface INewNoteHandler extends EventHandler{
	void onNewNote(NewNoteEvent event);
}
