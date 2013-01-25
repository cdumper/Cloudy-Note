package com.sid.cloudynote.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

public class BottomPanel extends Composite{

	private static BottomPanelUiBinder uiBinder = GWT
			.create(BottomPanelUiBinder.class);

	interface BottomPanelUiBinder extends UiBinder<Widget, BottomPanel> {
	}

	public BottomPanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField
	ToggleButton personalTabButton;
	
	@UiField
	ToggleButton exploreTabButton;

	@UiHandler("personalTabButton")
	void onPersonalClick(ClickEvent e) {
		Window.alert("Personal!");
	}
	
	@UiHandler("exploreTabButton")
	void onExploreClick(ClickEvent e) {
		Window.alert("Explore!");
	}
}
