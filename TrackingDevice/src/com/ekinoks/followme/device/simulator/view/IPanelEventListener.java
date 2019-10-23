package com.ekinoks.followme.device.simulator.view;

public interface IPanelEventListener {

	void onLogin();

	void onLogout();

	void serverFailPopup();

	void invalidPasswordPopup();

	void invalidUsernamePopup();

	void databaseFailPopup();
}
