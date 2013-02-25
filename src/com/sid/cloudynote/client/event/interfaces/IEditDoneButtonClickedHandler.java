package com.sid.cloudynote.client.event.interfaces;

import com.google.gwt.event.shared.EventHandler;
import com.sid.cloudynote.client.event.EditDoneButtonClickedEvent;

public interface IEditDoneButtonClickedHandler extends EventHandler{
	void onEditDoneButtonClicked(EditDoneButtonClickedEvent event);
}
