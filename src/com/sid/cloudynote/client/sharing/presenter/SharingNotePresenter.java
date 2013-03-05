package com.sid.cloudynote.client.sharing.presenter;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.sid.cloudynote.client.event.HideSharingNoteViewEvent;
import com.sid.cloudynote.client.presenter.Presenter;
import com.sid.cloudynote.client.service.InfoNoteService;
import com.sid.cloudynote.client.service.InfoNoteServiceAsync;
import com.sid.cloudynote.client.sharing.view.SharingNoteView;
import com.sid.cloudynote.client.sharing.view.interfaces.ISharingNoteView;
import com.sid.cloudynote.shared.InfoNote;

public class SharingNotePresenter implements Presenter,
		ISharingNoteView.Presenter {
	private SharingNoteView view;
	private HandlerManager eventBus;

	public SharingNotePresenter(SharingNoteView view, HandlerManager eventBus) {
		super();
		this.view = view;
		this.eventBus = eventBus;
	}

	@Override
	public void go(HasWidgets container) {
		container.clear();
		container.add(this.view.asWidget());
	}

	/**
	 * Handle the event that user clicks to edit the note First to check if the
	 * current user has the permission to edit or not. If yes then edit the
	 * note, if not return an error message
	 */
	@Override
	public void startEditing(InfoNote infoNote) {
		InfoNoteServiceAsync service =GWT.create(InfoNoteService.class);
		service.verifyEditAccess(infoNote, new AsyncCallback<Boolean>(){

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Failed to verify the edit access");
			}

			@Override
			public void onSuccess(Boolean result) {
				if (result) {
					view.editNote();
				} else {
					view.showAccessDeniedPanel();
				}
			}
		});
	}

	public void presentNote() {
		view.presentNote();
	}

	@Override
	public void backToList() {
		eventBus.fireEvent(new HideSharingNoteViewEvent());
	}
}
