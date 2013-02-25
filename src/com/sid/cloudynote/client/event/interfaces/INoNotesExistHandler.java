package com.sid.cloudynote.client.event.interfaces;

import com.google.gwt.event.shared.EventHandler;
import com.sid.cloudynote.client.event.NoNotesExistEvent;

public interface INoNotesExistHandler extends EventHandler{
	void onNotesExistEvent(NoNotesExistEvent event);
}
