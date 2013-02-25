package com.sid.cloudynote.client.sharing.view.interfaces;

import com.google.gwt.user.client.ui.Widget;

public interface IGroupView {
	public interface Presenter {
		void viewPublic();
		void viewShared();
		void viewGroups();
	}
	
	void setPresenter(Presenter presenter);
	Widget asWidget();
}
