package com.ekinoks.followme.trackingutils.events;

public interface IEvent {

	EventType getEventType();

	String getTimeStamp();

	String getDeviceID();

	int getEventID();
}
