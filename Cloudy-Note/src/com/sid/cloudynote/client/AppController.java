package com.sid.cloudynote.client;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasWidgets;
import com.sid.cloudynote.client.event.INotebookChangedHandler;
import com.sid.cloudynote.client.event.NotebookChangedEvent;
import com.sid.cloudynote.client.presenter.Presenter;

public class AppController implements Presenter, ValueChangeHandler {
	private final HandlerManager eventBus;
	private HasWidgets container;

	public AppController(HandlerManager eventBus) {
		this.eventBus = eventBus;
		bind();
	}

	private void bind() {
		eventBus.addHandler(NotebookChangedEvent.TYPE,
				new INotebookChangedHandler() {
					@Override
					public void onNotebookChanged(NotebookChangedEvent event) {
						// TODO Auto-generated method stub
						
					}
				});
	}

	@Override
	public void onValueChange(ValueChangeEvent event) {
		// TODO Auto-generated method stub
		System.out.println("onValueChange fired");
	}

	@Override
	public void go(HasWidgets container) {
		// TODO Auto-generated method stub
		this.container = container;
	}

}
