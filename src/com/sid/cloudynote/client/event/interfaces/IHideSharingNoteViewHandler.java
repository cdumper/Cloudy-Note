package com.sid.cloudynote.client.event.interfaces;

import com.google.gwt.event.shared.EventHandler;
import com.sid.cloudynote.client.event.HideSharingNoteViewEvent;

public interface IHideSharingNoteViewHandler extends EventHandler{
	void onHideSharingNoteView(HideSharingNoteViewEvent event);
}
