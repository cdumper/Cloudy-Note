package com.sid.cloudynote.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.sid.cloudynote.client.event.interfaces.IViewSharedHandler;
import com.sid.cloudynote.shared.User;

public class ViewSharedNotesEvent extends GwtEvent<IViewSharedHandler>{
	private User user;
	public ViewSharedNotesEvent(User user) {
		super();
		this.user = user;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public static final Type<IViewSharedHandler> TYPE = new Type<IViewSharedHandler>();
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<IViewSharedHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(IViewSharedHandler handler) {
		handler.onViewSharedNotes(this);
	}
}
