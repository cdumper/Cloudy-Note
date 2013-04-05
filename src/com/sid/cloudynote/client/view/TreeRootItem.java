package com.sid.cloudynote.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;
import com.sid.cloudynote.client.service.NotebookService;
import com.sid.cloudynote.client.service.NotebookServiceAsync;
import com.sid.cloudynote.shared.Notebook;

public class TreeRootItem extends Composite{

	private static TreeRootItemUiBinder uiBinder = GWT
			.create(TreeRootItemUiBinder.class);

	interface TreeRootItemUiBinder extends UiBinder<Widget, TreeRootItem> {
	}

	public TreeRootItem() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public TreeRootItem(ImageResource image, String text) {
		initWidget(uiBinder.createAndBindUi(this));
		this.text.setText(text);
	}

	@UiField
	Label text;
}
