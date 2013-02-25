package com.sid.cloudynote.client.sharing.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.sid.cloudynote.client.presenter.Presenter;
import com.sid.cloudynote.client.sharing.presenter.GroupPresenter;
import com.sid.cloudynote.client.sharing.presenter.SharingNoteListPresenter;
import com.sid.cloudynote.client.sharing.presenter.SharingSearchPresenter;

public class SharingView extends Composite implements Presenter{

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

	private HandlerManager eventBus;

	public SharingView() {
		initWidget(uiBinder.createAndBindUi(this));
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
		SharingNoteListPresenter noteListPresenter = new SharingNoteListPresenter(
				this.noteListView, eventBus);
		SharingSearchPresenter searchPresenter = new SharingSearchPresenter(this.searchView,
				eventBus);
		GroupPresenter groupPresenter = new GroupPresenter(this.groupView, eventBus);
//		NotePresenter notePresenter = new NotePresenter(
//				this.noteView.asWidget(), eventBus);

		this.noteListView.setPresenter(noteListPresenter);
		this.searchView.setPresenter(searchPresenter);
		this.groupView.setPresenter(groupPresenter);
//		this.noteView.setPresenter(notePresenter);

		noteListPresenter.go(noteListView.getContainer());
		searchPresenter.go(searchView.getContainer());
		groupPresenter.go(groupView.getContainer());
//		notePresenter.go(noteView.getContainer());
	}
}
