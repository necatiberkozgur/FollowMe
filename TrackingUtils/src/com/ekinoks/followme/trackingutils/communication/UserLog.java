package com.ekinoks.followme.trackingutils.communication;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.ekinoks.followme.trackingutils.users.UserType;

public class UserLog implements IUserLog {

	private String userID;
	private UserType userType;
	private ConnectionStatus transaction;
	private String timeStamp;

	public UserLog(String userID, UserType userType, ConnectionStatus transaction) {

		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		Date date = new Date(System.currentTimeMillis());
		timeStamp = formatter.format(date);

		this.userID = userID;
		this.userType = userType;
		this.transaction = transaction;
	}

	public UserLog(String userID, UserType userType, ConnectionStatus transaction, String timeStamp) {

		this.timeStamp = timeStamp;
		this.userID = userID;
		this.userType = userType;
		this.transaction = transaction;
	}

	public String getTimeStamp() {

		return timeStamp;
	}

	public ConnectionStatus getTransaction() {

		return transaction;
	}

	public void setTransaction(ConnectionStatus transaction) {

		this.transaction = transaction;
	}

	public UserType getUserType() {

		return userType;
	}

	public void setUserType(UserType userType) {

		this.userType = userType;
	}

	public String getUserID() {

		return userID;
	}

	public void setUserID(String userID) {

		this.userID = userID;
	}
}
