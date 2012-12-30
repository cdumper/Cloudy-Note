package com.sid.cloudynote.client.view;

import com.axeiya.gwtckeditor.client.CKEditor;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class EditPanel extends ResizeComposite{

	private static EditPanelUiBinder uiBinder = GWT
			.create(EditPanelUiBinder.class);

	@UiField
	CKEditor ckeditor;
	
	@UiField
	TitleBar titleBar;
	
	interface EditPanelUiBinder extends UiBinder<Widget, EditPanel> {
	}

	public EditPanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}
}
