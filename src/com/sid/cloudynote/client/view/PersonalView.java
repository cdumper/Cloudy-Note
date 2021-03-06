package com.sid.cloudynote.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.sid.cloudynote.client.presenter.NoteListPresenter;
import com.sid.cloudynote.client.presenter.NotePresenter;
import com.sid.cloudynote.client.presenter.NotebookListPresenter;
import com.sid.cloudynote.client.presenter.Presenter;
import com.sid.cloudynote.client.view.AppView.GlobalResources;

public class PersonalView extends Composite implements Presenter {

	private static PersonalViewUiBinder uiBinder = GWT
			.create(PersonalViewUiBinder.class);

	interface PersonalViewUiBinder extends UiBinder<Widget, PersonalView> {
	}

	

	@UiField
	DockLayoutPanel dockLayoutPanel;
//	@UiField
//	public SearchView searchView;
	@UiField
	public NotebookListView notebookListView;
	@UiField
	public NoteListView noteListView;
	@UiField
	public NoteView noteView;
	
	public PersonalView() {
		GWT.<GlobalResources> create(GlobalResources.class).css()
				.ensureInjected();
		initWidget(uiBinder.createAndBindUi(this));
	}

	private HandlerManager eventBus;

	public void setEventBus(HandlerManager eventBus) {
		this.eventBus = eventBus;
		bindPresentersAndViews();
	}

	@Override
	public void go(HasWidgets container) {
		container.clear();
		container.add(dockLayoutPanel);
	}

	private void bindPresentersAndViews() {
		NotebookListPresenter notebookListPresenter = new NotebookListPresenter(
				this.notebookListView, eventBus);
		NoteListPresenter noteListPresenter = new NoteListPresenter(
				this.noteListView, eventBus);
		NotePresenter notePresenter = new NotePresenter(
				this.noteView, eventBus);

		this.noteListView.setPresenter(noteListPresenter);
		this.notebookListView.setPresenter(notebookListPresenter);
		this.noteView.setPresenter(notePresenter);

		notePresenter.go(noteView.getContainer());
		notebookListPresenter.go(notebookListView.getContainer());
		noteListPresenter.go(noteListView.getContainer());
	}
	
	public Widget asWidget(){
		return this.dockLayoutPanel;
	}
}
