package com.ekinoks.followme.trackingutils.events;

public class EventWrapper implements IEvent {

	private EventType eventType;
	private Event event;

	private String timeStamp;
	private String deviceID;
	private int eventID;

	public EventWrapper(Event event) {

		eventType = EventType.valueOf(event.getType());
		this.timeStamp = event.getTimeStamp();
		this.deviceID = event.getDeviceID();
		this.eventID = event.getEventID();
		this.event = event;
	}

	public EventType getEventType() {

		return eventType;
	}

	public String getTimeStamp() {

		return timeStamp;
	}

	public String getDeviceID() {

		return deviceID;
	}

	public int getEventID() {

		return eventID;
	}

	public String eventSpecificFields() {

		return event.toString();
	}
}
