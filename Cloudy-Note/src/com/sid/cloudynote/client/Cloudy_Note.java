package com.sid.cloudynote.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.NotStrict;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.sid.cloudynote.client.presenter.NoteListPresenter;
import com.sid.cloudynote.client.presenter.NotebookListPresenter;
import com.sid.cloudynote.client.presenter.Presenter;
import com.sid.cloudynote.client.view.BottomPanel;
import com.sid.cloudynote.client.view.NoteListView;
import com.sid.cloudynote.client.view.NoteViewPanel;
import com.sid.cloudynote.client.view.NotebookListView;
import com.sid.cloudynote.client.view.TopPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Cloudy_Note implements EntryPoint, Presenter {
	interface Binder extends UiBinder<DockLayoutPanel, Cloudy_Note> {
	}

	interface GlobalResources extends ClientBundle {
		@NotStrict
		@Source("global.css")
		CssResource css();
	}

	private static final Binder binder = GWT.create(Binder.class);

	@UiField
	DockLayoutPanel dockLayoutPanel;
	@UiField
	TopPanel topView;
	@UiField
	BottomPanel bottomView;
	@UiField
	NotebookListView notebookListView;
	@UiField
	NoteListView noteListView;
	@UiField
	NoteViewPanel noteView;

	private HandlerManager eventBus;
	private AppController appViewer;

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		//initate ui components
		GWT.<GlobalResources> create(GlobalResources.class).css()
				.ensureInjected();
		dockLayoutPanel = binder.createAndBindUi(this);
		// inject dependencies...
		topView.setNotebookPanel(notebookListView);
		topView.setNotePanel(noteListView);
		topView.setNoteViewPanel(noteView);
		notebookListView.setNotePanel(noteListView);
		noteListView.setNoteViewPanel(noteView);

		eventBus = new HandlerManager(null);
		appViewer = new AppController(this, eventBus);

		appViewer.go(RootLayoutPanel.get());
	}

	@Override
	public void go(HasWidgets container) {
		container.clear();
		container.add(this.dockLayoutPanel);

		NotebookListPresenter notebookListPresenter = new NotebookListPresenter(
				this.notebookListView, eventBus);
		NoteListPresenter noteListPresenter = new NoteListPresenter(
				this.noteListView, eventBus);
		
		this.noteListView.setPresenter(noteListPresenter);
		this.notebookListView.setPresenter(notebookListPresenter);

		notebookListPresenter.go(notebookListView.getContainer());
		noteListPresenter.go(noteListView.getContainer());
	}
}