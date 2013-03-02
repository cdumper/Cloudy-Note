package com.sid.cloudynote.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.sid.cloudynote.client.event.interfaces.IGroupsChangedHandler;

public class GroupsChangedEvent extends GwtEvent<IGroupsChangedHandler>{
	public static final Type<IGroupsChangedHandler> TYPE = new Type<IGroupsChangedHandler>();
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<IGroupsChangedHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(IGroupsChangedHandler handler) {
		handler.onGroupsChanged(this);
	}

}
