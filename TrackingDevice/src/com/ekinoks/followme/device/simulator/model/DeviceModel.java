package com.ekinoks.followme.device.simulator.model;

import java.io.IOException;

import com.ekinoks.followme.device.simulator.communication.IDeviceServerCommunicationHandler;
import com.ekinoks.followme.device.simulator.view.IPanelEventListener;
import com.ekinoks.followme.trackingutils.events.EngineEvent;
import com.ekinoks.followme.trackingutils.events.LocationEvent;
import com.ekinoks.followme.trackingutils.events.SeatBeltEvent;

public class DeviceModel {

	private IDeviceServerCommunicationHandler communicationHandler;

	public DeviceModel(IDeviceServerCommunicationHandler communicationHandler) {

		this.communicationHandler = communicationHandler;
	}

	public boolean sendEvent(LocationEvent locationEvent) {

		try {

			communicationHandler.sendLocationEvent(locationEvent);

			return true;

		} catch (IOException e) {

			e.printStackTrace();
		}

		return false;
	}

	public boolean sendEvent(EngineEvent engineEvent) {

		try {

			communicationHandler.sendEngineEvent(engineEvent);

			return true;

		} catch (IOException e) {

			e.printStackTrace();

		}

		return false;
	}

	public boolean sendEvent(SeatBeltEvent seatBeltEvent) {

		try {

			communicationHandler.sendSeatBeltEvent(seatBeltEvent);
			return true;

		} catch (IOException e) {

			e.printStackTrace();

		}

		return false;
	}

	public void close() {

		try {

			communicationHandler.finishConnection();

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	public void connect() {

		try {

			communicationHandler.startConnection();

		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public boolean getConnectionStatus() {

		return communicationHandler.getConnectionStatus();
	}

	public String getUsername() {

		return communicationHandler.getDeviceName();
	}

	public void setUsername(String username) {

		communicationHandler.setDeviceName(username);
	}

	public void addConnectionListener(IPanelEventListener listener) {

		communicationHandler.addConnectionListener(listener);
	}

	public void login(String username, String password) {

		communicationHandler.sendLoginRequest(username, password);
	}

}
