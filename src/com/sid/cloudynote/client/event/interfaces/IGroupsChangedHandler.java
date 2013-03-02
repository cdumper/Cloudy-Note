package com.sid.cloudynote.client.event.interfaces;

import com.google.gwt.event.shared.EventHandler;
import com.sid.cloudynote.client.event.GroupsChangedEvent;

public interface IGroupsChangedHandler extends EventHandler{
	void onGroupsChanged(GroupsChangedEvent event);
}
