package com.sid.cloudynote.client.event.interfaces;

import com.google.gwt.event.shared.EventHandler;
import com.sid.cloudynote.client.event.ViewGroupNotesEvent;

public interface IViewGroupNotesHandler extends EventHandler{
	void onViewGroups(ViewGroupNotesEvent event);
}
