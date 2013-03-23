package com.sid.cloudynote.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.sid.cloudynote.client.event.interfaces.IViewGroupNotesHandler;
import com.sid.cloudynote.shared.Group;

public class ViewGroupNotesEvent extends GwtEvent<IViewGroupNotesHandler>{
	public static final Type<IViewGroupNotesHandler> TYPE = new Type<IViewGroupNotesHandler>();
	private Group group;

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public ViewGroupNotesEvent(Group group) {
		super();
		this.group = group;
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<IViewGroupNotesHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(IViewGroupNotesHandler handler) {
		System.out.println(this.getClass().getName());
		handler.onViewGroups(this);
	}
}
