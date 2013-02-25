package com.sid.cloudynote.client.event.interfaces;

import com.google.gwt.event.shared.EventHandler;
import com.sid.cloudynote.client.event.TagChangedEvent;

public interface ITagChangedHandler extends EventHandler{
	void onTagChanged(TagChangedEvent event);
}
