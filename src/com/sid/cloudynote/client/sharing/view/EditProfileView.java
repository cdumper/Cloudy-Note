package com.sid.cloudynote.client.sharing.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
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
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sid.cloudynote.client.DataManager;
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

	private String userEmail;
	private Images images;
	private Presenter presenter;
	private ClickListener saveHandler = new ClickListener(){
		@Override
		public void onClick(Widget sender) {
			presenter.saveUserProfile();
		}
	};
	
	private ClickListener addFriendHandler = new ClickListener(){
		@Override
		public void onClick(Widget sender) {
			presenter.addFriend();
		}
	};
	
	private static EditProfileViewUiBinder uiBinder = GWT
			.create(EditProfileViewUiBinder.class);

	interface EditProfileViewUiBinder extends UiBinder<Widget, EditProfileView> {
	}

	public interface Images extends ClientBundle {
		@Source("../../resources/images/User-Profile-200.png")
		ImageResource defaultUserProfileImage();
	}
	
	interface Style extends CssResource {
	}
	
	public EditProfileView() {
		initWidget(uiBinder.createAndBindUi(this));
		images = GWT.create(Images.class);
		initialWidgets();
	}

	public EditProfileView(String email) {
		initWidget(uiBinder.createAndBindUi(this));
		images = GWT.create(Images.class);
		initialWidgets();
		this.userEmail = email;
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
		this.imageProfile.setResource(images.defaultUserProfileImage());		
	}

	@Override
	public void presentMyProfile(String fullName, String nickName,
			String email) {
		this.fullName.setText(fullName);
		this.nickName.setText(nickName);
		this.email.setText(email);
		this.fullName.setEnabled(true);
		this.nickName.setEnabled(true);
		this.uploadButton.setVisible(true);
		this.saveButton.setText("Save");
		this.saveButton.removeClickListener(addFriendHandler);
		this.saveButton.addClickListener(saveHandler);
		this.saveButton.setVisible(true);
	}
	

	@Override
	public void viewOthersProfile(String fullName, String nickName, String email) {
		this.fullName.setText(fullName);
		this.nickName.setText(nickName);
		this.email.setText(email);
		this.fullName.setEnabled(false);
		this.nickName.setEnabled(false);
		this.uploadButton.setVisible(false);
		this.saveButton.setText("Add Friend");
		this.saveButton.removeClickListener(saveHandler);
		this.saveButton.addClickListener(addFriendHandler);
		if (DataManager.getAllFriends().containsKey(email)) {
			this.saveButton.setVisible(false);
		}
	}

	@Override
	public void showUploadDialog(String title, String url) {
		DialogBox dialog = new DialogBox();
		dialog.setText(title);
		dialog.setAnimationEnabled(true);
		dialog.setGlassEnabled(true);
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

	@Override
	public String getUser() {
		return userEmail;
	}
	
	public void setUser(String email) {
		this.userEmail = email;
	}
}
