package com.sid.cloudynote.client.sharing.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.sid.cloudynote.client.event.HideSharingNoteViewEvent;
import com.sid.cloudynote.client.event.ViewSharedNoteEvent;
import com.sid.cloudynote.client.event.interfaces.IHideSharingNoteViewHandler;
import com.sid.cloudynote.client.event.interfaces.IViewSharedNoteHandler;
import com.sid.cloudynote.client.presenter.Presenter;
import com.sid.cloudynote.client.sharing.presenter.GroupPresenter;
import com.sid.cloudynote.client.sharing.presenter.SharingNoteListPresenter;
import com.sid.cloudynote.client.sharing.presenter.SharingNotePresenter;
import com.sid.cloudynote.client.sharing.presenter.SharingSearchPresenter;
import com.sid.cloudynote.shared.InfoNote;

public class SharingView extends Composite implements Presenter, IViewSharedNoteHandler, IHideSharingNoteViewHandler{
	SharingNoteListPresenter noteListPresenter;
	SharingSearchPresenter searchPresenter;
	GroupPresenter groupPresenter;
	SharingNotePresenter notePresenter;
	private static SharingViewUiBinder uiBinder = GWT
			.create(SharingViewUiBinder.class);

	interface SharingViewUiBinder extends UiBinder<Widget, SharingView> {
	}
	
	@UiField
	DockLayoutPanel dockLayoutPanel;
	@UiField
	public SharingSearchView searchView;
	@UiField
	public GroupView groupView;
	@UiField
	public SharingNoteListView noteListView;
	@UiField
	public SharingNoteView noteView;

	private HandlerManager eventBus;

	public SharingView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public SharingView(HandlerManager eventBus) {
		this();
		this.eventBus = eventBus;
		bindPresentersAndViews();
	}
	
	@Override
	public void go(HasWidgets container) {
		container.clear();
		container.add(this.dockLayoutPanel);
	}
	
	@Override
	public Widget asWidget(){
		return this.dockLayoutPanel;
	}

	public void seteEventBus(HandlerManager eventBus) {
		this.eventBus = eventBus;
		bindPresentersAndViews();
	}
	
	private void bindPresentersAndViews() {
		noteListPresenter = new SharingNoteListPresenter(
				this.noteListView, eventBus);
		searchPresenter = new SharingSearchPresenter(this.searchView,
				eventBus);
		groupPresenter = new GroupPresenter(this.groupView, eventBus);
		notePresenter = new SharingNotePresenter(this.noteView, eventBus);

		this.noteListView.setPresenter(noteListPresenter);
		this.searchView.setPresenter(searchPresenter);
		this.groupView.setPresenter(groupPresenter);
		this.noteView.setPresenter(notePresenter);

		noteListPresenter.go(noteListView.getContainer());
		searchPresenter.go(searchView.getContainer());
		groupPresenter.go(groupView.getContainer());
		notePresenter.go(noteView.getContainer());
	}

	/** handle the viewSharedNoteEvent to change the UI of sharingView
	 *  and present the clicked note.
	 */
	@Override
	public void onViewSharedNote(ViewSharedNoteEvent event) {
		viewNote(event.getNote());
	}

	private void viewNote(InfoNote note) {
		this.noteView.setNote(note);
		this.noteView.presentNote();
	}

	@Override
	public void onHideSharingNoteView(HideSharingNoteViewEvent event) {
		// TODO onHideSharingNoteView in sharingView
	}
}
