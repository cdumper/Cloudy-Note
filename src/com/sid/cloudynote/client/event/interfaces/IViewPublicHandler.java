package com.sid.cloudynote.client.event.interfaces;

import com.google.gwt.event.shared.EventHandler;
import com.sid.cloudynote.client.event.ViewPublicNotesEvent;

public interface IViewPublicHandler extends EventHandler{
	void onViewPublicNotes(ViewPublicNotesEvent event);
}
