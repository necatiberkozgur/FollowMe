package com.ekinoks.followme.trackingutils.users;

import java.time.LocalDateTime;

public class UserWrapper implements IUser {

	private String UserID;
	private LocalDateTime dateAdded;
	private String password;
	private UserType userType;
	private User user;

	public UserWrapper(User user) {

		this.UserID = user.getUserID();
		this.password = user.getPassword();
		this.userType = user.getType();
		this.dateAdded = user.getDateAdded();
		this.user = user;
	}

	@Override
	public String getUserID() {

		return this.UserID;
	}

	public String getPassword() {

		return this.password;
	}

	@Override
	public UserType getUserType() {

		return this.userType;
	}

	@Override
	public LocalDateTime getDateAdded() {

		return this.dateAdded;
	}

	public String getUserSpecificFields() {

		if (user instanceof ClientUser) {

			return ((ClientUser) user).getUserName();

		} else if (user instanceof DeviceUser) {

			return ((DeviceUser) user).getManufacturer();

		} else {

			return null;
		}
	}
}
