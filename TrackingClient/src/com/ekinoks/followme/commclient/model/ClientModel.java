package com.ekinoks.followme.commclient.model;

import java.util.List;

import com.ekinoks.followme.commclient.communication.ClientServerCommunicationHandler;
import com.ekinoks.followme.commclient.view.IPanelEventListener;
import com.ekinoks.followme.trackingutils.communication.UserLog;

public class ClientModel {

	private ClientServerCommunicationHandler comm;

	public ClientModel() {

		comm = new ClientServerCommunicationHandler();
	}

	public ClientServerCommunicationHandler getCommunicator() {

		return this.comm;
	}

	public void startComm() {

		comm.start();
	}

	public void stopComm() {

		comm.stop();
	}

	public String getUserID() {

		return comm.getUserID();
	}

	public void setUserID(String userID) {

		comm.setUserID(userID);
	}

	public List<UserLog> getAssignedUsers() {

		return comm.getAssignedUsers();
	}

	public void addConnectionListener(IPanelEventListener listener) {

		comm.addConnectionListener(listener);
	}

	public void login(String username, String password) {

		comm.sendLoginRequest(username, password);
	}
}
