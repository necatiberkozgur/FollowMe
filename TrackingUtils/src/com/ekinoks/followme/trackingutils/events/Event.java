package com.ekinoks.followme.trackingutils.events;

public class Event {

	private String timeStamp;
	private String deviceID;
	private int eventID;
	private String type;

	public int getEventID() {

		return eventID;
	}

	public void setEventID(int eventID) {

		this.eventID = eventID;
	}

	public String getDeviceID() {

		return deviceID;
	}

	public void setDeviceID(String deviceID) {

		this.deviceID = deviceID;
	}

	public String getTimeStamp() {

		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {

		this.timeStamp = timeStamp;
	}

	public String getType() {

		return type;
	}

	public void setType(String type) {

		this.type = type;
	}

}
