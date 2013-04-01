package com.sid.cloudynote.client.event.interfaces;

import com.google.gwt.event.shared.EventHandler;
import com.sid.cloudynote.client.event.UserInfoChangedEvent;

public interface IUserInfoChangedHandler extends EventHandler{
	void onUserInfoChanged(UserInfoChangedEvent event);
}
