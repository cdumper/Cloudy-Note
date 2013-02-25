package com.sid.cloudynote.client.sharing.view.interfaces;

import com.google.gwt.user.client.ui.Widget;

public interface ISharingSearchView {
	public interface Presenter {
		void onSearchMade();
		void onSortBy();
		void onView();
		void onSettings();
	}

	void setPresenter(Presenter presenter);
	Widget asWidget();
}
