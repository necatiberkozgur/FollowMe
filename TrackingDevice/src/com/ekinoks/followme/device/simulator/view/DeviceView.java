package com.ekinoks.followme.device.simulator.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class DeviceView extends JPanel {

	private JButton btnGenerateLocationEvent;
	private JButton btnGenerateSeatBeltEvent;
	private JButton btnGenerateEngineEvent;
	private JButton btnGenerateCloseRequest;
	private JTextField deviceIDTextField;

	public DeviceView() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 214, 202, 13, 0 };
		gridBagLayout.rowHeights = new int[] { 25, 25, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		btnGenerateLocationEvent = new JButton("Generate Location Event");
		GridBagConstraints gbc_btnGenerateLocationEvent = new GridBagConstraints();
		gbc_btnGenerateLocationEvent.insets = new Insets(0, 0, 5, 5);
		gbc_btnGenerateLocationEvent.gridx = 0;
		gbc_btnGenerateLocationEvent.gridy = 1;
		add(btnGenerateLocationEvent, gbc_btnGenerateLocationEvent);

		btnGenerateSeatBeltEvent = new JButton("Generate Seat Belt Event");
		GridBagConstraints gbc_btnGenerateSeatBeltEvent = new GridBagConstraints();
		gbc_btnGenerateSeatBeltEvent.insets = new Insets(0, 0, 5, 5);
		gbc_btnGenerateSeatBeltEvent.gridx = 1;
		gbc_btnGenerateSeatBeltEvent.gridy = 1;
		add(btnGenerateSeatBeltEvent, gbc_btnGenerateSeatBeltEvent);

		btnGenerateEngineEvent = new JButton("Generate Engine Event");
		GridBagConstraints gbc_btnGenerateEngineEvent = new GridBagConstraints();
		gbc_btnGenerateEngineEvent.insets = new Insets(0, 0, 5, 0);
		gbc_btnGenerateEngineEvent.gridx = 2;
		gbc_btnGenerateEngineEvent.gridy = 1;
		add(btnGenerateEngineEvent, gbc_btnGenerateEngineEvent);

		btnGenerateCloseRequest = new JButton("Close connection");
		GridBagConstraints gbc_btnGenerateCloseRequest = new GridBagConstraints();
		gbc_btnGenerateCloseRequest.insets = new Insets(0, 0, 5, 5);
		gbc_btnGenerateCloseRequest.gridx = 1;
		gbc_btnGenerateCloseRequest.gridy = 2;
		add(btnGenerateCloseRequest, gbc_btnGenerateCloseRequest);

		deviceIDTextField = new JTextField();
		deviceIDTextField.setColumns(10);
		GridBagConstraints gbc_deviceIDTextField = new GridBagConstraints();
		gbc_deviceIDTextField.insets = new Insets(0, 0, 0, 5);
		gbc_deviceIDTextField.gridx = 1;
		gbc_deviceIDTextField.gridy = 3;
		add(deviceIDTextField, gbc_deviceIDTextField);
	}

	public JButton getBtnGenerateLocationEvent() {

		return btnGenerateLocationEvent;
	}

	public JButton getBtnGenerateEngineEvent() {

		return btnGenerateEngineEvent;
	}

	public JButton getBtnGenerateSeatBeltEvent() {

		return btnGenerateSeatBeltEvent;
	}

	public JButton getBtnGenerateCloseRequest() {

		return btnGenerateCloseRequest;
	}

	public JTextField getDeviceIdTextField() {
		return deviceIDTextField;
	}

	public void setDeviceIdTextField(JTextField deviceIdTextField) {
		this.deviceIDTextField = deviceIdTextField;
	}

}
