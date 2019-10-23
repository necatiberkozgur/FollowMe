package com.ekinoks.followme.deviceadmin.adminview;

import com.ekinoks.followme.trackingutils.users.IUser;

public interface IPairPanel {

	void onAddPairToTable(IUser u, int totalEntries);

	void onRemovePairFromTable(String username);

	String getUserID();

}
