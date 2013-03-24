package com.sid.cloudynote.client.presenter;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.sid.cloudynote.client.DataManager;
import com.sid.cloudynote.client.event.EditNoteEvent;
import com.sid.cloudynote.client.event.NoteChangedEvent;
import com.sid.cloudynote.client.event.NotebookChangedEvent;
import com.sid.cloudynote.client.service.BlobService;
import com.sid.cloudynote.client.service.BlobServiceAsync;
import com.sid.cloudynote.client.service.InfoNoteService;
import com.sid.cloudynote.client.service.InfoNoteServiceAsync;
import com.sid.cloudynote.client.view.interfaces.INonEditableNoteView;
import com.sid.cloudynote.shared.InfoNote;

public class NonEditableNotePresenter implements Presenter, INonEditableNoteView.Presenter{
	private INonEditableNoteView view;
	private HandlerManager eventBus;
	
	public NonEditableNotePresenter(INonEditableNoteView view,
			HandlerManager eventBus) {
		super();
		this.view = view;
		this.eventBus = eventBus;
	}

	@Override
	public void presentAttachmentLinks(List<String> keys) {
		// TODO NonEditableNotePresenter presentAttachmentLinks
		BlobServiceAsync service = GWT.create(BlobService.class);
		service.getBlobFileName(keys, new AsyncCallback<List<String>>() {
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("get blob file name failed!");
			}

			@Override
			public void onSuccess(List<String> result) {
				for (int i=0;i<result.size();i++) {
//					view.presentAttachmentLink(result.get(i),keys.get(i));
				}
			}
		});
	}

	@Override
	public void onClickEdit(InfoNote note) {
		eventBus.fireEvent(new EditNoteEvent(note));
	}

	@Override
	public void shareThroughEmail() {
		// TODO shareThroughEmail
		
	}

	@Override
	public void deleteNote(final InfoNote note) {
		InfoNoteServiceAsync service = GWT.create(InfoNoteService.class);
		service.delete(note, new AsyncCallback<Void>(){

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Failed to delete note:" + note.getTitle());
			}

			@Override
			public void onSuccess(Void result) {
				GWT.log("Successully deleted note:" + note.getTitle());
				eventBus.fireEvent(new NotebookChangedEvent());
				eventBus.fireEvent(new NoteChangedEvent(DataManager
						.getCurrentNotebook()));
			}
		});
	}

	@Override
	public void go(HasWidgets container) {
		container.clear();
		container.add(this.view.asWidget());
	}

}
