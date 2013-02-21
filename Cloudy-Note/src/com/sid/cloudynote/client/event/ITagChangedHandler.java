package com.sid.cloudynote.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface ITagChangedHandler extends EventHandler{
	void onTagChanged(TagChangedEvent event);
}
