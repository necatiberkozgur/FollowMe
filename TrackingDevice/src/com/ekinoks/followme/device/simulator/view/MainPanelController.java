package com.ekinoks.followme.device.simulator.view;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.ekinoks.followme.device.simulator.model.DeviceModel;

public class MainPanelController implements IPanelEventListener {

	private MainPanel mainPanel;
	private DeviceModel deviceModel;
	private LoginViewController loginViewController;
	private DeviceViewController deviceViewController;

	public MainPanelController(DeviceModel deviceModel) {

		mainPanel = new MainPanel();
		this.deviceModel = deviceModel;
		loginViewController = new LoginViewController(this.deviceModel);
		deviceViewController = new DeviceViewController(this.deviceModel);
		mainPanel.add(loginViewController.getView());
		loginViewController.addLoginListener(this);
		deviceViewController.addLogoutListener(this);
		this.deviceModel.addConnectionListener(this);

	}

	public MainPanel getView() {

		return mainPanel;
	}

	@Override
	public void onLogin() {

		mainPanel.remove(loginViewController.getView());
		deviceViewController.getView().getDeviceIdTextField().setText(deviceModel.getUsername());
		mainPanel.add(deviceViewController.getView());
		mainPanel.revalidate();
		mainPanel.repaint();
	}

	@Override
	public void onLogout() {

		mainPanel.remove(deviceViewController.getView());
		mainPanel.add(loginViewController.getView());
		mainPanel.revalidate();
		mainPanel.repaint();
	}

	@Override
	public void serverFailPopup() {

		JFrame frame = new JFrame();

		JOptionPane.showMessageDialog(frame, "Failed to connect to server, try again later!", "Error",
				JOptionPane.ERROR_MESSAGE);
	}

	@Override
	public void invalidPasswordPopup() {

		JFrame frame = new JFrame();

		JOptionPane.showMessageDialog(frame, "Invalid password!", "Error", JOptionPane.ERROR_MESSAGE);
	}

	@Override
	public void invalidUsernamePopup() {

		JFrame frame = new JFrame();

		JOptionPane.showMessageDialog(frame, "Invalid username, this user does not exists!", "Error",
				JOptionPane.ERROR_MESSAGE);
	}

	@Override
	public void databaseFailPopup() {

		JFrame frame = new JFrame();

		JOptionPane.showMessageDialog(frame, "Failed to connect to database, try again later!", "Error",
				JOptionPane.ERROR_MESSAGE);
	}

	public DeviceViewController getDeviceViewController() {

		return deviceViewController;
	}

}
