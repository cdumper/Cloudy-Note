package com.sid.cloudynote.client.event.interfaces;

import com.google.gwt.event.shared.EventHandler;
import com.sid.cloudynote.client.event.PresentNotesEvent;

public interface IPresentNotesHandler extends EventHandler{
	void onPresentNotes(PresentNotesEvent event);
}
