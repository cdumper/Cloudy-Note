package com.sid.cloudynote.client.view.interfaces;

import java.util.List;

import com.google.gwt.user.client.ui.Widget;
import com.sid.cloudynote.shared.InfoNote;

public interface INonEditableNoteView {
	public interface Presenter {

		void presentAttachmentLinks(final List<String> keys);
		
		void onClickEdit(InfoNote note);
		
		void shareThroughEmail();
		
		void deleteNote(InfoNote note);
	}

	void presentNote(InfoNote note);

	void setPresenter(Presenter presenter);

	void presentAttachmentLink(String fileName, String key);

	Widget asWidget();
}
