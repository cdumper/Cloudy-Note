package com.sid.cloudynote.client.sharing.presenter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.sid.cloudynote.client.AppController;
import com.sid.cloudynote.client.presenter.Presenter;
import com.sid.cloudynote.client.service.UploadService;
import com.sid.cloudynote.client.service.UploadServiceAsync;
import com.sid.cloudynote.client.service.UserService;
import com.sid.cloudynote.client.service.UserServiceAsync;
import com.sid.cloudynote.client.sharing.view.interfaces.IEditProfileView;
import com.sid.cloudynote.shared.User;

public class EditProfilePresenter implements Presenter, IEditProfileView.Presenter{
	private IEditProfileView view;
	private HandlerManager eventBus;
	
	public EditProfilePresenter(IEditProfileView view, HandlerManager eventBus) {
		super();
		this.view = view;
		this.eventBus = eventBus;
	}

	@Override
	public void go(HasWidgets container) {
		container.clear();
		loadUserProfile();
		container.add(view.asWidget());
	}

	private void loadUserProfile() {
		User user = AppController.get().getLoginInfo();
		view.presentUserProfile(user.getFullName(), user.getNickname(), user.getEmail());
		this.presentProfileImage();
	}

	@Override
	public void saveUserProfile() {
		User user = AppController.get().getLoginInfo();
		user.setFullName(view.getFullName());
		user.setNickname(view.getNickName());
		UserServiceAsync service = GWT.create(UserService.class);
		service.modifyUser(user, new AsyncCallback<Void>(){

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Failed to save user profile changes");
				view.showErrorDialog(caught.getMessage());
			}

			@Override
			public void onSuccess(Void result) {
				view.showSuccessfulDialog();
			}
			
		});
	}

	@Override
	public void presentProfileImage() {
		UserServiceAsync service = GWT.create(UserService.class);
		service.getUserProfile(AppController.get().getLoginInfo().getEmail(),
				new AsyncCallback<String>() {
					@Override
					public void onFailure(Throwable caught) {
						GWT.log("Failed to retrieve profile image");
						view.showErrorDialog(caught.getMessage());
					}

					@Override
					public void onSuccess(String result) {
						if(result == null) {
							view.presentDefaultUserProfileImage();
						} else {
							view.setProfileImageUrl(result);
						}
					}
				});
	}

	@Override
	public void onClickUpload() {
		UploadServiceAsync service = GWT.create(UploadService.class);
		service.createUploadURL(new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Failed to create upload url");
			}

			@Override
			public void onSuccess(String result) {
				view.showUploadDialog("Change Profile Image", result);
			}
		});
	}

	@Override
	public void changeProfileImage(String filename, String blobkey) {
		User user = AppController.get().getLoginInfo();
		user.setProfileImage(blobkey);
		UserServiceAsync service = GWT.create(UserService.class);
		service.modifyUser(user, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Change profile image failed");
				view.showErrorDialog("Change profile image failed."+caught.getMessage());
			}

			@Override
			public void onSuccess(Void result) {
				presentProfileImage();
			}
		});
	}
}
