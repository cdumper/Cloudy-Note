package com.sid.cloudynote.client.sharing.presenter;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.sid.cloudynote.client.AppController;
import com.sid.cloudynote.client.event.ViewPublicNotesEvent;
import com.sid.cloudynote.client.event.ViewSharedNotesEvent;
import com.sid.cloudynote.client.presenter.Presenter;
import com.sid.cloudynote.client.sharing.view.interfaces.IGroupView;

public class GroupPresenter implements Presenter, IGroupView.Presenter{
	private final HandlerManager eventBus;
	private Widget view;
	
	public GroupPresenter(Widget view, HandlerManager eventBus) {
		super();
		this.eventBus = eventBus;
		this.view = view;
	}

	@Override
	public void viewPublic() {
		// TODO Auto-generated method stub
		eventBus.fireEvent(new ViewPublicNotesEvent());
	}

	@Override
	public void viewShared() {
		// TODO Auto-generated method stub
		eventBus.fireEvent(new ViewSharedNotesEvent(AppController.get().getLoginInfo()));
	}

	@Override
	public void viewGroups() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void go(HasWidgets container) {
		container.clear();
		container.add(view.asWidget());
	}
}
