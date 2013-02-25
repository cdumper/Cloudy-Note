package com.sid.cloudynote.client.presenter;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasWidgets;
import com.sid.cloudynote.client.sharing.view.SharingSearchView;
import com.sid.cloudynote.client.view.interfaces.ISharingSearchView;

public class SharingSearchPresenter implements Presenter, ISharingSearchView.Presenter{
	private final HandlerManager eventBus;
	private final SharingSearchView view;

	public SharingSearchPresenter(SharingSearchView view, HandlerManager eventBus) {
		super();
		this.eventBus = eventBus;
		this.view = view;
	}

	@Override
	public void go(HasWidgets container) {
		container.clear();
		container.add(view.asWidget());
	}

	@Override
	public void onSearchMade() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSortBy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onView() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSettings() {
		// TODO Auto-generated method stub
		
	}
}
