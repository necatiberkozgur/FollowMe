package com.ekinoks.followme.deviceadmin.adminmodel;

import com.ekinoks.followme.deviceadmin.adminservercommunication.AdminServerCommunicationHandler;
import com.ekinoks.followme.deviceadmin.adminview.IClientPanel;
import com.ekinoks.followme.deviceadmin.adminview.IDevicePanel;
import com.ekinoks.followme.deviceadmin.adminview.IPanelEventListener;
import com.ekinoks.followme.deviceadmin.databaseops.IDatabaseOpsHandler;
import com.ekinoks.followme.trackingutils.users.IUser;
import com.ekinoks.followme.trackingutils.users.User;
import com.ekinoks.followme.trackingutils.users.UserType;

public class AdminModel {

	private IDatabaseOpsHandler databaseOpsHandler;
	private AdminServerCommunicationHandler adminServerCommunicationHandler;

	public AdminModel(IDatabaseOpsHandler databaseOpsHandler,
			AdminServerCommunicationHandler adminServerCommunicationHandler) {

		this.adminServerCommunicationHandler = adminServerCommunicationHandler;
		this.databaseOpsHandler = databaseOpsHandler;
	}

	public void addUser(User user) {

		adminServerCommunicationHandler.addUser(user);
	}

	public void deleteUser(User user) {

		adminServerCommunicationHandler.removeUser(user);
	}

	public String getUsernameAdmin() {

		return databaseOpsHandler.getUsernameAdmin();
	}

	public String getPasswordAdmin() {

		return databaseOpsHandler.getPasswordAdmin();
	}

	public void deletePair(String client, String device) {

		adminServerCommunicationHandler.removePair(device, client);
	}

	public void deletePairFromDatabaseByDevice(String device) {

		adminServerCommunicationHandler.deleteDevicesAllPairs(device);
	}

	public void deletePairFromDatabaseByClient(String client) {

		adminServerCommunicationHandler.deleteClientsAllPairs(client);
	}

	public void addPair(String usernameDevice, String usernameClient) {

		adminServerCommunicationHandler.addPair(usernameDevice, usernameClient);
	}

	public void startServerCommunication() {

		adminServerCommunicationHandler.startConnection();
	}

	public void finishServerCommunication() {

		adminServerCommunicationHandler.finishConnection();
	}

	public boolean getServerConnectionStatus() {

		return adminServerCommunicationHandler.getConnectionStatus();
	}

	public void addClientListener(IClientPanel client) {

		adminServerCommunicationHandler.addClientListener(client);
	}

	public void addDeviceListener(IDevicePanel device) {

		adminServerCommunicationHandler.addDeviceListener(device);
	}

	public void registerToServer() {

		adminServerCommunicationHandler.sendRegisterMessage();
	}

	public void fetchUsers(UserType type, int pageNumber) {

		adminServerCommunicationHandler.fetchUsers(type, pageNumber);
	}

	public int getDeviceCount() {

		return adminServerCommunicationHandler.getDeviceCount();
	}

	public void setDeviceCount(int deviceCount) {

		adminServerCommunicationHandler.setDeviceCount(deviceCount);
		;
	}

	public int getClientCount() {

		return adminServerCommunicationHandler.getClientCount();
	}

	public void setClientCount(int clientCount) {

		adminServerCommunicationHandler.setClientCount(clientCount);
	}

	public void requestPairs(IUser user, int page) {

		adminServerCommunicationHandler.requestPair(user, page);
	}

	public void addPanelListener(IPanelEventListener listener) {

		adminServerCommunicationHandler.addPanelListener(listener);
	}

}
