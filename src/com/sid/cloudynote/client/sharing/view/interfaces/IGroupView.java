package com.sid.cloudynote.client.sharing.view.interfaces;

import java.util.List;

import com.google.gwt.user.client.ui.Widget;
import com.sid.cloudynote.shared.Group;

public interface IGroupView {
	public interface Presenter {
		void viewPublic();
		void viewShared();
		void loadGroupList();
		void onGroupItemSelected(Group group);
	}
	
	void setPresenter(Presenter presenter);
	Widget asWidget();
	void setGroupList(List<Group> groups);
}
