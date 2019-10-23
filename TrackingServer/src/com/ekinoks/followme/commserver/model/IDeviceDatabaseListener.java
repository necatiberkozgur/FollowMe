package com.ekinoks.followme.commserver.model;

public interface IDeviceDatabaseListener {

	void onGetDeviceUpdates(String username, int state);
}
