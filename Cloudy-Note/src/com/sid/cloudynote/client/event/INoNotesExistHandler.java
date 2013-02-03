package com.sid.cloudynote.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface INoNotesExistHandler extends EventHandler{
	void onNotesExistEvent(NoNotesExistEvent event);
}
