package com.ekinoks.followme.commclient.view;

import java.util.ArrayList;
import java.util.Arrays;

import com.ekinoks.followme.commclient.model.ClientModel;
import com.ekinoks.followme.trackingutils.communication.UserLog;
import com.ekinoks.ui.components.table.TableColumnPreference;

public class UserLogController implements IUserLogController {

	private ClientModel clientModel;
	private UserLogPanel userLogPanel;

	public UserLogController(ClientModel clientModel) {

		userLogPanel = new UserLogPanel();
		this.clientModel = clientModel;

		userLogPanel.getLogTable().setFilterable(true);
		userLogPanel.getLogTable().setSortable(true);

		userLogPanel.getLogTable().setContextMenuVisible(true);
		userLogPanel.getLogTable().setCellSelectionEnabled(false);

		userLogPanel.getLogTable().setColumnPreferences(new ArrayList<>(Arrays.asList(
				new TableColumnPreference(0, true, "com.ekinoks.followme.trackingutils.communication.UserLog.userID"),
				new TableColumnPreference(1, true,
						"com.ekinoks.followme.trackingutils.communication.UserLog.transaction"),
				new TableColumnPreference(2, true,
						"com.ekinoks.followme.trackingutils.communication.UserLog.timeStamp"))));

		userLogPanel.getLogTable().addRows(clientModel.getAssignedUsers());
	}

	@Override
	public void onGetUpdates(UserLog log) {

		userLogPanel.getLogTable().removeAllRows();
		userLogPanel.getLogTable().addRows(clientModel.getAssignedUsers());
	}

	public UserLogPanel getView() {

		return this.userLogPanel;
	}
}
