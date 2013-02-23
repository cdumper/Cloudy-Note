package com.sid.cloudynote.client;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasWidgets;
import com.sid.cloudynote.client.presenter.Presenter;
import com.sid.cloudynote.client.view.AppView;
import com.sid.cloudynote.shared.LoginInfo;

public class AppController implements Presenter, ValueChangeHandler<String> {
	private static AppController singleton;
	private final HandlerManager eventBus;
	private LoginInfo loginInfo;
	boolean isPersonal = true;
	public boolean isPersonal() {
		return isPersonal;
	}

	public void setPersonal(boolean isPersonal) {
		this.isPersonal = isPersonal;
	}
	private AppView appView;

	public AppController(HandlerManager eventBus, LoginInfo login) {
		AppController.singleton = this;
		this.loginInfo = login;
		this.eventBus = eventBus;
	}
	
	public static AppController get() {
		return singleton;
	}

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		// TODO appController history controll part
		System.out.println("onValueChange fired");
	}

	@Override
	public void go(HasWidgets container) {
		if (appView == null) {
			appView = new AppView(eventBus,loginInfo);
		}
		
		appView.go(container);
	}
}
