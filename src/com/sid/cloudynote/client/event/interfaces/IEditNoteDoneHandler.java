package com.sid.cloudynote.client.event.interfaces;

import com.google.gwt.event.shared.EventHandler;
import com.sid.cloudynote.client.event.EditNoteDoneEvent;

public interface IEditNoteDoneHandler extends EventHandler{
	void onEditNoteDone(EditNoteDoneEvent event);
}
