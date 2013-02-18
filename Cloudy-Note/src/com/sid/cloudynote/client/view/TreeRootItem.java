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
		this.image.setResource(image);
		this.text.setText(text);
//		this.dropdown.setSize("5px", "5px");
		
		Image img = new Image("https://ssl.gstatic.com/ui/v1/zippy/arrow_down.png");

        dropdown = new PushButton(img);

        dropdown.getElement().getStyle().setPaddingLeft(5,  Unit.PX);
        dropdown.getElement().getStyle().setPaddingRight(5,  Unit.PX);
        dropdown.getElement().getStyle().setPaddingTop(3,  Unit.PX);
        dropdown.getElement().getStyle().setPaddingBottom(7,  Unit.PX);

//        dropdown.addClickHandler(this);
        dropdown.removeStyleName("gwt-PushButton");

//        textBox.getElement().getStyle().setBorderWidth(0, Unit.PX);


//        HorizontalPanel panel = new HorizontalPanel();
//        panel.add(textBox);
//        panel.add(dropdown);

//        panel.getElement().getStyle().setBorderWidth(1, Unit.PX);
//        panel.getElement().getStyle().setBorderColor("black");
//        panel.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);

        // All composites must call initWidget() in their constructors.
//        initWidget(panel);
	}

	@UiHandler("dropdown")
	void onClick(ClickEvent e) {
		NotebookServiceAsync service = (NotebookServiceAsync) GWT
				.create(NotebookService.class);
		AsyncCallback<Void> callback = new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("getNotebooksList falied!");
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(Void result) {
				GWT.log("Notebook added successfully!");
			}

		};
		service.add(new Notebook(), callback);
	}

	@UiField
	Image image;
	@UiField
	Label text;
	@UiField
	PushButton dropdown;

}
