package com.ekinoks.followme.trackingutils.users;

import java.time.LocalDateTime;

public class ClientUser extends User {

	private String userName;

	public ClientUser(String userName, String password, String userID) {

		this.setPassword(password);
		this.setUserID(userID);
		this.userName = userName;
		this.setType(UserType.Client);
	}

	public ClientUser(String userName, String password, String userID, LocalDateTime dateAdded) {

		this.setDateAdded(dateAdded);
		this.setPassword(password);
		this.setUserID(userID);
		this.userName = userName;
		this.setType(UserType.Client);
	}

	public String getUserName() {

		return userName;
	}

	public void setUserName(String userName) {

		this.userName = userName;
	}
}
