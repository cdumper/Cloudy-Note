package com.sid.cloudynote.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface INotebookChangedHandler extends EventHandler{
	void onNotebookChanged(NotebookChangedEvent event);
}
