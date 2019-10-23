package com.ekinoks.followme.trackingutils.events;

public class LocationEvent extends Event {

	private double latitude;
	private double longitude;
	private double height;

	public LocationEvent(double latitude, double longitude, double height, String timeStamp, String deviceID,
			int eventID) {

		this.latitude = latitude;
		this.longitude = longitude;
		this.height = height;
		this.setTimeStamp(timeStamp);
		this.setDeviceID(deviceID);
		this.setEventID(eventID);
		this.setType("LocationEvent");
	}

	public LocationEvent() {

	}

	public double getLatitude() {

		return latitude;
	}

	@Override
	public String toString() {

		return (String) ("Latitude = " + latitude + "\n Longitude = " + longitude);
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

}
