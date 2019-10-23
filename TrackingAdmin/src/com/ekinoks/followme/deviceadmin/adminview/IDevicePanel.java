package com.ekinoks.followme.deviceadmin.adminview;

import com.ekinoks.followme.trackingutils.users.IUser;
import com.ekinoks.followme.trackingutils.users.User;

public interface IDevicePanel {

	void onAddUser(User u);

	void onDeleteUserFromList(String u);

	void onAddPair(IUser user1, IUser user2, int pairCount);
}