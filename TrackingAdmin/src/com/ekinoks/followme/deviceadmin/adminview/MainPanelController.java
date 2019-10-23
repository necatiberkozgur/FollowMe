package com.ekinoks.followme.deviceadmin.adminview;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.ekinoks.followme.deviceadmin.adminmodel.AdminModel;

public class MainPanelController implements IPanelEventListener {

	private MainPanel mainPanel;
	private AdminLoginPanelController adminLoginPanelController;
	private TabbedMenuController tabbedMenuController;
	private AdminModel model;

	public MainPanelController(AdminModel model) {

		this.model = model;
		mainPanel = new MainPanel();
		adminLoginPanelController = new AdminLoginPanelController(this.model);
		tabbedMenuController = new TabbedMenuController(this.model);
		this.model.addClientListener(tabbedMenuController.getAddClientPanelController());
		this.model.addDeviceListener(tabbedMenuController.getAddDevicePanelController());
		tabbedMenuController.getAddClientPanelController().setParentPanel(this);
		tabbedMenuController.getAddDevicePanelController().setParentPanel(this);
		adminLoginPanelController.addSuccessfulLoginListener(this);
		model.addPanelListener(this);
		mainPanel.add(adminLoginPanelController.getView(), BorderLayout.CENTER);
	}

	@Override
	public void onSuccessSignin() {

		model.registerToServer();
		mainPanel.remove(adminLoginPanelController.getView());
		mainPanel.add(tabbedMenuController.getView(), BorderLayout.CENTER);
		mainPanel.revalidate();
		mainPanel.repaint();

	}

	@Override
	public void onSuccessSignOut() {

		model.finishServerCommunication();
		model.startServerCommunication();
		mainPanel.removeAll();
		mainPanel.add(adminLoginPanelController.getView(), BorderLayout.CENTER);

		mainPanel.revalidate();
		mainPanel.repaint();
	}

	@Override
	public void onLostConnection() {

		mainPanel.removeAll();
		mainPanel.add(adminLoginPanelController.getView(), BorderLayout.CENTER);
		JFrame frame = new JFrame();
		JOptionPane.showMessageDialog(frame, "Connection closed!", "Info", JOptionPane.INFORMATION_MESSAGE);
		mainPanel.repaint();
	}

	@Override
	public void onUserExists() {

		JFrame frame = new JFrame();
		JOptionPane.showMessageDialog(frame, "User already exists!", "Info", JOptionPane.INFORMATION_MESSAGE);

	}

	public MainPanel getView() {

		return mainPanel;
	}

}
