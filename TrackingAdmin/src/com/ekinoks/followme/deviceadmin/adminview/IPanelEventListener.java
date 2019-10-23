package com.ekinoks.followme.deviceadmin.adminview;

public interface IPanelEventListener {

	void onSuccessSignin();

	void onSuccessSignOut();

	void onLostConnection();

	void onUserExists();
}
