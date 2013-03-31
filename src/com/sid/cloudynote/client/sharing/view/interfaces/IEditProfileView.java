package com.sid.cloudynote.client.sharing.view.interfaces;

import com.google.gwt.user.client.ui.Widget;

public interface IEditProfileView {
	public interface Presenter {

		void saveUserProfile();
		
	}
	Widget asWidget();
	void setPresenter(Presenter presenter);
}
