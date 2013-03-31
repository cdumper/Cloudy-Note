package com.sid.cloudynote.client.sharing.presenter;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasWidgets;
import com.sid.cloudynote.client.presenter.Presenter;
import com.sid.cloudynote.client.sharing.view.interfaces.IEditProfileView;

public class EditProfilePresenter implements Presenter, IEditProfileView.Presenter{
	private IEditProfileView view;
	private HandlerManager eventBus;
	
	public EditProfilePresenter(IEditProfileView view, HandlerManager eventBus) {
		super();
		this.view = view;
		this.eventBus = eventBus;
	}

	@Override
	public void go(HasWidgets container) {
		container.clear();
		container.add(view.asWidget());
	}

	@Override
	public void saveUserProfile() {
		// TODO Auto-generated method stub
		
	}
}
