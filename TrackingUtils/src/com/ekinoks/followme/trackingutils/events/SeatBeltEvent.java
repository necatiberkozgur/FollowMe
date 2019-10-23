package com.ekinoks.followme.trackingutils.events;

public class SeatBeltEvent extends Event {

	private boolean isPlugged;

	public SeatBeltEvent(boolean isPlugged, String timeStamp, String deviceID, int eventID) {

		this.isPlugged = isPlugged;
		this.setTimeStamp(timeStamp);
		this.setDeviceID(deviceID);
		this.setEventID(eventID);
		this.setType("SeatBeltEvent");
	}

	public SeatBeltEvent() {

	}

	@Override
	public String toString() {

		return (String) ("Plug status: " + isPlugged);
	}

	public boolean isPlugged() {

		return isPlugged;
	}

	public void setPlugged(boolean isPlugged) {

		this.isPlugged = isPlugged;
	}

}
