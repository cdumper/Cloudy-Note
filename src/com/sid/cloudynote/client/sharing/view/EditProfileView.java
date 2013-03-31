package com.sid.cloudynote.client.sharing.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sid.cloudynote.client.sharing.view.interfaces.IEditProfileView;
import com.sid.cloudynote.client.view.Container;

public class EditProfileView extends Composite implements IEditProfileView {
	@UiField
	Style style;
	@UiField
	Container container;
	@UiField
	HTMLPanel content;
	@UiField
	Button saveButton;
	@UiField
	HTMLPanel imageProfilePanel;
	@UiField
	Image imageProfile;
	@UiField
	Button uploadButton;
	@UiField
	TextBox fullName;
	@UiField
	TextBox nickName;
	@UiField
	TextBox email;

	@UiHandler("saveButton")
	void onSave(ClickEvent e) {
		presenter.saveUserProfile();
	}

	private Images images;
	private Presenter presenter;
	private static EditProfileViewUiBinder uiBinder = GWT
			.create(EditProfileViewUiBinder.class);

	interface EditProfileViewUiBinder extends UiBinder<Widget, EditProfileView> {
	}

	public interface Images extends ClientBundle, Tree.Resources {
		@Source("../../resources/images/user.png")
		ImageResource defaultUserProfileImage();
	}
	
	interface Style extends CssResource {
	}

	public EditProfileView() {
		initWidget(uiBinder.createAndBindUi(this));
		images = GWT.create(Images.class);
		initialWidgets();
	}

	private void initialWidgets() {
		this.email.setEnabled(false);
		this.uploadButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				presenter.onClickUpload();
			}
		});
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

	private void createUploadDialog(final DialogBox dialog, String url) {
		final FormPanel form = new FormPanel();
		form.setEncoding(FormPanel.ENCODING_MULTIPART);
		form.setMethod(FormPanel.METHOD_POST);
		form.setAction(url);

		VerticalPanel panel = new VerticalPanel();
		panel.setSpacing(10);
		form.setWidget(panel);

		final FileUpload upload = new FileUpload();
		upload.setName("myFile");
		panel.add(upload);

		HorizontalPanel buttonPanel = new HorizontalPanel();
		panel.add(buttonPanel);

		buttonPanel.add(new Button("Cancel", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				dialog.hide();
			}
		}));

		buttonPanel.add(new Button("Submit", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				form.submit();
			}
		}));

		form.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) {
				presenter.changeProfileImage(upload.getFilename(), event.getResults());
				dialog.hide();
			}
		});
		dialog.add(form);
	}

	@Override
	public String getFullName() {
		return this.fullName.getText();
	}

	@Override
	public String getNickName() {
		return this.nickName.getText();
	}

	@Override
	public void setProfileImageUrl(String url) {
		this.imageProfile.setUrl(url);
	}

	@Override
	public void presentDefaultUserProfileImage() {
		this.imageProfile = new Image(images.defaultUserProfileImage());		
	}

	@Override
	public void presentUserProfile(String fullName, String nickName,
			String email) {
		this.fullName.setText(fullName);
		this.nickName.setText(nickName);
		this.email.setText(email);
	}

	@Override
	public void showUploadDialog(String title, String url) {
		DialogBox dialog = new DialogBox();
		dialog.setTitle(title);
		createUploadDialog(dialog, url);
		dialog.center();
	}

	@Override
	public void showSuccessfulDialog() {
		this.showMessage("Successfully updated user profile!");
	}

	@Override
	public void showErrorDialog(String errorMessage) {
		this.showMessage(errorMessage);
	}
	
	private void showMessage(String text){
		final DialogBox dialog = new DialogBox();
		HTMLPanel content = new HTMLPanel("");
		content.add(new Label(text));
		content.add(new Button("OK",new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				dialog.hide();
			}
			
		}));
		dialog.add(content);
		dialog.center();
	}
}
