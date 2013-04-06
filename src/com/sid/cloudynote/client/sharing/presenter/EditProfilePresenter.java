package com.sid.cloudynote.client.sharing.presenter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.sid.cloudynote.client.AppController;
import com.sid.cloudynote.client.event.UserInfoChangedEvent;
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
		final String userEmail = view.getUser();
		User user = AppController.get().getLoginInfo();
		if (userEmail.equals(user.getEmail())) {
			view.presentMyProfile(user.getFullName(), user.getNickname(), user.getEmail());
			this.presentProfileImage(AppController.get().getLoginInfo());
		} else {
			UserServiceAsync service = GWT.create(UserService.class);
			service.getUser(userEmail, new AsyncCallback<User>(){

				@Override
				public void onFailure(Throwable caught) {
					GWT.log("Failed to get user profile of:"+userEmail);
				}

				@Override
				public void onSuccess(User result) {
					view.viewOthersProfile(result.getFullName(), result.getNickname(), userEmail);
					presentProfileImage(result);
				}
				
			});
		}
	}

	@Override
	public void saveUserProfile() {
		User user = AppController.get().getLoginInfo();
		user.setFullName(view.getFullName());
		user.setNickname(view.getNickName());
		UserServiceAsync service = GWT.create(UserService.class);
		service.modifyUser(user, new AsyncCallback<User>(){

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Failed to save user profile changes");
				view.showErrorDialog(caught.getMessage());
			}

			@Override
			public void onSuccess(User result) {
				view.showSuccessfulDialog();
				eventBus.fireEvent(new UserInfoChangedEvent(result));
			}
			
		});
	}

	@Override
	public void presentProfileImage(User user) {
		if (user.getProfileImageUrl()==null || "".equals(user.getProfileImageUrl().trim())){
			view.presentDefaultUserProfileImage();
		} else {
			view.setProfileImageUrl(user.getProfileImageUrl());
		}
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
		service.modifyUserProfileImage(user, new AsyncCallback<User>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Change profile image failed");
				view.showErrorDialog("Change profile image failed."+caught.getMessage());
			}

			@Override
			public void onSuccess(User result) {
				presentProfileImage(result);
				eventBus.fireEvent(new UserInfoChangedEvent(result));
			}
		});
	}

	@Override
	public void addFriend() {
		// TODO friend request
		UserServiceAsync service = GWT.create(UserService.class);
		service.addFriend(view.getUser(), new AsyncCallback<User>(){

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Failed to add friend:"+view.getUser());
			}

			@Override
			public void onSuccess(User user) {
				if(user == null){
					System.out.println("User does not exist. Invitation has already sent");
				} else {
					System.out.println("Successfully added user "+view.getUser()+" as friend");
					eventBus.fireEvent(new UserInfoChangedEvent(user));
				}
			}
			
		});
	}
}
