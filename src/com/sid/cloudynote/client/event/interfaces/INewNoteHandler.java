package com.sid.cloudynote.client.event.interfaces;

import com.google.gwt.event.shared.EventHandler;
import com.sid.cloudynote.client.event.NewNoteEvent;

public interface INewNoteHandler extends EventHandler{
	void onNewNote(NewNoteEvent event);
}
