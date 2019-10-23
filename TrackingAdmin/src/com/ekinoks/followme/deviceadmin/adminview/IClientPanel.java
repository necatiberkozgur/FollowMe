package com.ekinoks.followme.deviceadmin.adminview;

import com.ekinoks.followme.trackingutils.users.IUser;
import com.ekinoks.followme.trackingutils.users.User;

public interface IClientPanel {

	void onAddUser(User u);

	void onDeleteUserFromList(String u);

	void onAddPair(IUser client, IUser device, int pairCount);
}
