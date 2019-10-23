package com.ekinoks.followme.commserver.model;

import com.ekinoks.followme.trackingutils.communication.UserLog;

public interface IClientCommunicationServer {

	void onSubmitDevice(UserLog log);

}
