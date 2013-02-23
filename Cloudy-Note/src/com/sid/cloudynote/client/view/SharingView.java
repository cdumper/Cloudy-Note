package com.sid.cloudynote.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.sid.cloudynote.client.AppController;
import com.sid.cloudynote.client.presenter.Presenter;

public class SharingView extends Composite implements Presenter{

	private static SharingViewUiBinder uiBinder = GWT
			.create(SharingViewUiBinder.class);

	interface SharingViewUiBinder extends UiBinder<Widget, SharingView> {
	}
	
	@UiField
	DockLayoutPanel dockLayoutPanel;
	@UiField
	SearchView searchView;
	@UiField
	NoteListView noteListView;
	@UiField
	NoteView noteView;

	private HandlerManager eventBus;
	private AppController appViewer;

	public SharingView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void go(HasWidgets container) {
		container.clear();
		container.add(this.dockLayoutPanel);
//		bindPresentersAndViews();
	}
	
	@Override
	public Widget asWidget(){
		return this.dockLayoutPanel;
	}
}
