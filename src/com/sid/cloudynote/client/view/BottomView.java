package com.sid.cloudynote.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

public class BottomView extends Composite {

	private static BottomPanelUiBinder uiBinder = GWT
			.create(BottomPanelUiBinder.class);

	interface BottomPanelUiBinder extends UiBinder<Widget, BottomView> {
	}

	public BottomView() {
		initWidget(uiBinder.createAndBindUi(this));
		personalTabButton.setDown(true);
		exploreTabButton.setDown(false);
	}

	@UiField
	ToggleButton personalTabButton;

	@UiField
	ToggleButton exploreTabButton;

	@UiHandler("personalTabButton")
	void onPersonalClick(ClickEvent e) {
		personalTabButton.setDown(true);
		exploreTabButton.setDown(false);
//		showPersonal();
	}

	@UiHandler("exploreTabButton")
	void onExploreClick(ClickEvent e) {
		personalTabButton.setDown(false);
		exploreTabButton.setDown(true);
//		showSharing();
	}

//	private void showPersonal() {
//		if (!AppController.get().isPersonal()) {
//			AppController.get().setPersonal(true);
//			AppController.get().go(RootLayoutPanel.get());
//		}
//	}

//	private void showSharing() {
//		if (AppController.get().isPersonal()) {
//			AppController.get().setPersonal(false);
//			AppController.get().go(RootLayoutPanel.get());
//		}
//	}
}
