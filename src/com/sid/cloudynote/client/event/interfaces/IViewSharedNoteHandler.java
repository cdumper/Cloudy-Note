package com.sid.cloudynote.client.event.interfaces;

import com.google.gwt.event.shared.EventHandler;
import com.sid.cloudynote.client.event.ViewSharedNoteEvent;

public interface IViewSharedNoteHandler extends EventHandler{
	void onViewSharedNote(ViewSharedNoteEvent event);
}
