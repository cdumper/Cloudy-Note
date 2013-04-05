package com.sid.cloudynote.client.sharing.view.interfaces;

import java.util.List;

import com.google.gwt.user.client.ui.Widget;
import com.sid.cloudynote.shared.Group;
import com.sid.cloudynote.shared.InfoNote;

public interface ISharingView {
	public interface Presenter {
		void viewPublicNotes();

		void viewSharedWithMeNotes();

		void loadGroupList();

		void onGroupItemSelected(Group group);

		void onClickEdit();
		
		void onClickDone();

		void backToList();
		
		void searchNotes(String text);

		void viewUserProfile(String email);
	}

	void setPresenter(Presenter presenter);

	Widget asWidget();

	void setGroupList(List<Group> groups);

	void setNoteList(List<InfoNote> notes);

	void presentNote(InfoNote note);

	void editNote(InfoNote note);
	
	void showAccessDeniedPanel();
	
	InfoNote getSeletedNote();
	
	void setSelectedNote(InfoNote note);
	
	InfoNote getModifiedNoteData();

	void updateNote(InfoNote result);
}
