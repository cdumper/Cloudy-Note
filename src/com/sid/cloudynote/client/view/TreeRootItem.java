package com.sid.cloudynote.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

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
