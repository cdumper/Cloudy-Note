package com.sid.cloudynote.client;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasWidgets;
import com.sid.cloudynote.client.event.NoteChangedEvent;
import com.sid.cloudynote.client.event.NotebookChangedEvent;
import com.sid.cloudynote.client.presenter.Presenter;

public class AppController implements Presenter, ValueChangeHandler<String> {
	private final HandlerManager eventBus;
	private Cloudy_Note cn;

	public AppController(Cloudy_Note cn, HandlerManager eventBus) {
		this.cn = cn;
		this.eventBus = eventBus;
		bind();
	}

	private void bind() {
		eventBus.addHandler(NotebookChangedEvent.TYPE,cn.notebookListView);
		eventBus.addHandler(NoteChangedEvent.TYPE,cn.noteListView);
	}

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		// TODO Auto-generated method stub
		System.out.println("onValueChange fired");
	}

	@Override
	public void go(final HasWidgets container) {
		this.cn.go(container);
	}
}
