package com.sid.cloudynote.client.sharing.view.interfaces;

import com.google.gwt.user.client.ui.Widget;
import com.sid.cloudynote.shared.User;

public interface IEditProfileView {
	public interface Presenter {
		void saveUserProfile();
		void onClickUpload();
		void changeProfileImage(String filename, String blobkey);
		void presentProfileImage(User user);
		void addFriend();
	}
	Widget asWidget();
	void setPresenter(Presenter presenter);
	String getFullName();
	void presentMyProfile(String fullName, String nickName, String email);
	void viewOthersProfile(String fullName, String nickName, String email);
	void showUploadDialog(String title, String url);
	String getNickName();
	void setProfileImageUrl(String url);
	void presentDefaultUserProfileImage();
	void showSuccessfulDialog();
	void showErrorDialog(String errorMessage);
	String getUser();
}
