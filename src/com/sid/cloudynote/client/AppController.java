package com.sid.cloudynote.client;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasWidgets;
import com.sid.cloudynote.client.presenter.Presenter;
import com.sid.cloudynote.client.view.AppView;
import com.sid.cloudynote.shared.User;

public class AppController implements Presenter, ValueChangeHandler<String> {
	public static final int PERSONAL_PAGE = 1;
	public static final int SHARING_PAGE = 2;
	public static final int FRIENDS_PAGE = 3;
	public static final int ADMIN_PAGE = 4;
	private int pageState = PERSONAL_PAGE;
	private static AppController singleton;
	private final HandlerManager eventBus;
	private User loginInfo;
	private AppView appView;
	
	public User getLoginInfo() {
		return loginInfo;
	}

	public AppController(HandlerManager eventBus, User login) {
		AppController.singleton = this;
		this.loginInfo = login;
		this.eventBus = eventBus;
	}
	
	public void setPageState(int pageState) {
		this.pageState = pageState;
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
		
		if (this.pageState == PERSONAL_PAGE) {
			this.appView.showMyNotes(container);
		} else if (this.pageState == SHARING_PAGE) {
			this.appView.showOthersNotes(container);
		} else if (this.pageState == FRIENDS_PAGE) {
			this.appView.showFriends(container);
		} else if (this.pageState == ADMIN_PAGE) {
			this.appView.showAdmin(container);
		}
	}
}
