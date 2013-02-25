package com.sid.cloudynote.client.sharing.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.sid.cloudynote.client.sharing.view.interfaces.ISharingSearchView;
import com.sid.cloudynote.client.view.Container;

public class SharingSearchView extends ResizeComposite implements ISharingSearchView{

	private static SharingSearchViewUiBinder uiBinder = GWT
			.create(SharingSearchViewUiBinder.class);
	@UiTemplate("SharingSearchView.ui.xml")
	interface SharingSearchViewUiBinder extends
			UiBinder<Widget, SharingSearchView> {
	}

	public SharingSearchView() {
		initWidget(uiBinder.createAndBindUi(this));
		sortBy.addItem("Sort by");
		view.addItem("View");
		settings.addItem("Settings");
	}
	
	private Presenter presenter;

	@UiField
	Container container;
	public Container getContainer() {
		return container;
	}

	@UiField
	HorizontalPanel content;
	@UiField
	TextBox searchField;
	@UiField
	ListBox sortBy;
	@UiField
	ListBox view;
	@UiField
	ListBox settings;
	
	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	
	@Override
	public Widget asWidget(){
		return this.content;
	}
}
