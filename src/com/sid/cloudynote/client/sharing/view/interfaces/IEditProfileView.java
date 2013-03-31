package com.sid.cloudynote.client.sharing.view.interfaces;

import com.google.gwt.user.client.ui.Widget;

public interface IEditProfileView {
	public interface Presenter {
		void saveUserProfile();
		void presentProfileImage();
		void onClickUpload();
		void changeProfileImage(String filename, String blobkey);
	}
	Widget asWidget();
	void setPresenter(Presenter presenter);
	String getFullName();
	void presentUserProfile(String fullName, String nickName, String email);
	void showUploadDialog(String title, String url);
	String getNickName();
	void setProfileImageUrl(String url);
	void presentDefaultUserProfileImage();
	void showSuccessfulDialog();
	void showErrorDialog(String errorMessage);
}
