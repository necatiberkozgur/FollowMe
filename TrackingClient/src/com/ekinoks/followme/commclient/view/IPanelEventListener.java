package com.ekinoks.followme.commclient.view;

public interface IPanelEventListener {

	void onLogin();

	void onLogout();

	void serverFailPopup();

	void invalidPasswordPopup();

	void invalidUsernamePopup();

	void databaseFailPopup();

	void failPopup();

}
