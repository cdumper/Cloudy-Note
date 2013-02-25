package com.sid.cloudynote.client.event.interfaces;

import com.google.gwt.event.shared.EventHandler;
import com.sid.cloudynote.client.event.NotebookChangedEvent;

public interface INotebookChangedHandler extends EventHandler{
	void onNotebookChanged(NotebookChangedEvent event);
}
