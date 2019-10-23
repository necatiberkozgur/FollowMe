package com.ekinoks.followme.device.simulator.view;

import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.ekinoks.followme.device.simulator.model.DeviceModel;
import com.ekinoks.followme.trackingutils.events.EngineEvent;
import com.ekinoks.followme.trackingutils.events.LocationEvent;
import com.ekinoks.followme.trackingutils.events.SeatBeltEvent;

public class DeviceViewController {

	private DeviceView view;
	private DeviceModel applicationModel;
	private Set<IPanelEventListener> logoutListeners = new HashSet<IPanelEventListener>();

	public DeviceViewController(DeviceModel applicationModel) {

		this.applicationModel = applicationModel;
		view = new DeviceView();
		view.getDeviceIdTextField().setText(applicationModel.getUsername());
		view.getBtnGenerateEngineEvent().addActionListener(this::onGenerateEngineEvent);
		view.getBtnGenerateLocationEvent().addActionListener(this::onGenerateLocationEvent);
		view.getBtnGenerateSeatBeltEvent().addActionListener(this::onGenerateSeatBeltEvent);
		view.getBtnGenerateCloseRequest().addActionListener(this::onGenerateCloseRequest);
	}

	private void onGenerateEngineEvent(ActionEvent e) {

		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		Date date = new Date(System.currentTimeMillis());

		applicationModel.sendEvent(new EngineEvent(true, formatter.format(date), applicationModel.getUsername(),
				((int) Math.floor(Math.random() * 1000))));
	}

	private void onGenerateLocationEvent(ActionEvent e) {

		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		Date date = new Date(System.currentTimeMillis());

		applicationModel.sendEvent(new LocationEvent(Math.random() * 100, Math.random() * 100, Math.random() * 100,
				formatter.format(date), applicationModel.getUsername(), ((int) Math.floor(Math.random() * 1000))));

	}

	private void onGenerateSeatBeltEvent(ActionEvent e) {

		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		Date date = new Date(System.currentTimeMillis());

		applicationModel.sendEvent(new SeatBeltEvent(true, formatter.format(date), applicationModel.getUsername(),
				((int) Math.floor(Math.random() * 1000))));
	}

	private void onGenerateCloseRequest(ActionEvent e) {

		applicationModel.close();
		invokeLogoutListeners();
		applicationModel.setUsername("");
		applicationModel.connect();
	}

	private void invokeLogoutListeners() {

		for (IPanelEventListener iPanelEventListener : logoutListeners) {

			iPanelEventListener.onLogout();
		}
	}

	public void addLogoutListener(IPanelEventListener panelEventListener) {

		if (logoutListeners.contains(panelEventListener) == false) {

			logoutListeners.add(panelEventListener);
		}
	}

	public void removeLogoutListener(IPanelEventListener panelEventListener) {

		if (logoutListeners.contains(panelEventListener) == true) {

			logoutListeners.remove(panelEventListener);
		}
	}

	public DeviceView getView() {

		return view;
	}

}
