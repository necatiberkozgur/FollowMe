package com.ekinoks.followme.device.simulator.communication;

import java.io.IOException;
import java.net.UnknownHostException;

import com.ekinoks.followme.device.simulator.view.IPanelEventListener;
import com.ekinoks.followme.trackingutils.events.EngineEvent;
import com.ekinoks.followme.trackingutils.events.LocationEvent;
import com.ekinoks.followme.trackingutils.events.SeatBeltEvent;

public interface IDeviceServerCommunicationHandler {

	// Factory method
	static IDeviceServerCommunicationHandler createInstance() {

		return new DeviceServerCommunicationHandler();
	}

	void sendSeatBeltEvent(SeatBeltEvent event) throws IOException;

	void sendLocationEvent(LocationEvent event) throws IOException;

	void sendEngineEvent(EngineEvent event) throws IOException;

	void finishConnection() throws IOException;

	public void startConnection() throws IOException, UnknownHostException;

	public boolean getConnectionStatus();

	String getDeviceName();

	void setDeviceName(String deviceName);

	void addConnectionListener(IPanelEventListener listener);

	void sendLoginRequest(String username, String password);
}
