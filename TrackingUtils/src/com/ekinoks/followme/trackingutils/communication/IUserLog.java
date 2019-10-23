package com.ekinoks.followme.trackingutils.communication;

public interface IUserLog {

	String getUserID();

	String getTimeStamp();

	ConnectionStatus getTransaction();
}
