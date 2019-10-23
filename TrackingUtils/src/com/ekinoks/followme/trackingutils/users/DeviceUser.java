package com.ekinoks.followme.trackingutils.users;

import java.time.LocalDateTime;

public class DeviceUser extends User {

	private String manufacturer;

	public DeviceUser(String manufacturer, String password, String userID) {

		this.setUserID(userID);
		this.setPassword(password);
		this.manufacturer = manufacturer;
		this.setType(UserType.Device);
	}

	public DeviceUser(String manufacturer, String password, String userID, LocalDateTime dateAdded) {

		this.setDateAdded(dateAdded);
		this.setUserID(userID);
		this.setPassword(password);
		this.manufacturer = manufacturer;
		this.setType(UserType.Device);
	}

	public String getManufacturer() {

		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {

		this.manufacturer = manufacturer;
	}
}
