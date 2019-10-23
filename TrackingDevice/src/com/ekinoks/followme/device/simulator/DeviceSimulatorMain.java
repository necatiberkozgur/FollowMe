package com.ekinoks.followme.device.simulator;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;

import org.osgi.framework.BundleContext;

import com.ekinoks.followme.device.simulator.communication.IDeviceServerCommunicationHandler;
import com.ekinoks.followme.device.simulator.model.DeviceModel;
import com.ekinoks.followme.device.simulator.view.MainPanelController;

public class DeviceSimulatorMain {

	public void activate(BundleContext bc) {

		IDeviceServerCommunicationHandler communicationHandler = IDeviceServerCommunicationHandler.createInstance();

		DeviceModel applicationModel = new DeviceModel(communicationHandler);

		MainPanelController mainPanelController = new MainPanelController(applicationModel);

		JFrame frame = new JFrame("Device");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());

		frame.add(mainPanelController.getView(), BorderLayout.NORTH);

		frame.setPreferredSize(new Dimension(1000, 300));
		frame.pack();
		frame.setVisible(true);

	}
}
