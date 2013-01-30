package com.sid.cloudynote.client.view;

import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RequiresResize;

public class Container extends HTMLPanel implements RequiresResize{
	public Container(String html) {
		super(html);
	}

	@Override
	public void onResize() {
	}
}
