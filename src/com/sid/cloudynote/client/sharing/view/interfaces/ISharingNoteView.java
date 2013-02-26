package com.sid.cloudynote.client.sharing.view.interfaces;

import com.google.gwt.user.client.ui.Widget;
import com.sid.cloudynote.shared.InfoNote;

public interface ISharingNoteView {
	public interface Presenter {
		void startEditing(InfoNote infoNote);
	}
	
	void presentNote();
	void editNote();
	void setPresenter(Presenter presenter);
	Widget asWidget();
}
