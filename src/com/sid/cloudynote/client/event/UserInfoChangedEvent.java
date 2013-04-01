package com.sid.cloudynote.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.sid.cloudynote.client.event.interfaces.IUserInfoChangedHandler;
import com.sid.cloudynote.shared.User;

public class UserInfoChangedEvent extends GwtEvent<IUserInfoChangedHandler>{
	private User user;
	public UserInfoChangedEvent(User user) {
		super();
		this.user = user;
	}

	public static final Type<IUserInfoChangedHandler> TYPE = new Type<IUserInfoChangedHandler>();
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<IUserInfoChangedHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(IUserInfoChangedHandler handler) {
		System.out.println(this.getClass().getName());
		handler.onUserInfoChanged(this);
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
