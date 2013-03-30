package com.sid.cloudynote.client.sharing.presenter;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasWidgets;
import com.sid.cloudynote.client.presenter.Presenter;
import com.sid.cloudynote.client.sharing.view.SharingSearchView;
import com.sid.cloudynote.client.sharing.view.interfaces.ISharingSearchView;

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
		// TODO others note search event
		
	}

	@Override
	public void onSortBy() {
		// TODO others note sort event
		
	}

	@Override
	public void onView() {
		// TODO others note view event
		
	}

	@Override
	public void onSettings() {
		// TODO others note setting event
		
	}
}
