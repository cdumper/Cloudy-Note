package com.sid.cloudynote.client.event.interfaces;

import com.google.gwt.event.shared.EventHandler;
import com.sid.cloudynote.client.event.ViewSharedNotesEvent;

public interface IViewSharedHandler extends EventHandler{
	void onViewSharedNotes(ViewSharedNotesEvent event);
}
