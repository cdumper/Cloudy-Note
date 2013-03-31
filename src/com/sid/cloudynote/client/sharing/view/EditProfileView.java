package com.sid.cloudynote.client.sharing.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.sid.cloudynote.client.AppController;
import com.sid.cloudynote.client.sharing.view.interfaces.IEditProfileView;
import com.sid.cloudynote.client.view.Container;
import com.sid.cloudynote.shared.User;

public class EditProfileView extends Composite implements IEditProfileView{
	@UiField
	Style style;
	@UiField
	Container container;
	@UiField
	HTMLPanel content;
	@UiField
	Button saveButton;
	@UiField
	TextBox fullName;
	@UiField
	TextBox nickName;
	@UiField
	TextBox email;
	
	@UiHandler("saveButton")
	void onSave(ClickEvent e){
		presenter.saveUserProfile();
	}
	private Presenter presenter;
	private static EditProfileViewUiBinder uiBinder = GWT
			.create(EditProfileViewUiBinder.class);

	interface EditProfileViewUiBinder extends UiBinder<Widget, EditProfileView> {
	}
	
	interface Style extends CssResource {}

	public EditProfileView() {
		initWidget(uiBinder.createAndBindUi(this));
		initialWidgets();
	}

	private void initialWidgets() {
		User user = AppController.get().getLoginInfo();
		this.nickName.setText(user.getNickname());
		this.email.setText(user.getEmail());
		this.email.setEnabled(false);
	}

	public EditProfileView(String firstName) {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	
	@Override
	public Widget asWidget() {
		return this.content;
	}
	
	public Container getContainer() {
		return this.container;
	}
}
