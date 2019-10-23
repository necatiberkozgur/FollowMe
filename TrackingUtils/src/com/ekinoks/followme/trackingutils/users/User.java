package com.ekinoks.followme.trackingutils.users;

import java.time.LocalDateTime;

public class User {

	private String userID;
	private String password;
	private UserType type;
	private LocalDateTime dateAdded;

	public String getUserID() {

		return userID;
	}

	public void setUserID(String userID) {

		this.userID = userID;
	}

	public String getPassword() {

		return password;
	}

	public void setPassword(String password) {

		this.password = password;
	}

	public UserType getType() {

		return type;
	}

	public void setType(UserType type) {

		this.type = type;
	}

	public LocalDateTime getDateAdded() {

		return dateAdded;
	}

	public void setDateAdded(LocalDateTime dateAdded) {

		this.dateAdded = dateAdded;
	}

}
