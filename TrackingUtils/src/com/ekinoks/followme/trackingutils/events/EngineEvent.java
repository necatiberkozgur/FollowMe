package com.ekinoks.followme.trackingutils.events;

public class EngineEvent extends Event {

	private boolean isStart;

	public EngineEvent(boolean isStart, String timeStamp, String deviceID, int eventID) {

		this.isStart = isStart;
		this.setTimeStamp(timeStamp);
		this.setDeviceID(deviceID);
		this.setEventID(eventID);
		this.setType("EngineEvent");
	}

	public EngineEvent() {

	}

	@Override
	public String toString() {

		return (String) ("Engine start status: " + isStart);
	}

	public boolean isStart() {

		return isStart;
	}

	public void setStart(boolean isStart) {

		this.isStart = isStart;
	}

}
