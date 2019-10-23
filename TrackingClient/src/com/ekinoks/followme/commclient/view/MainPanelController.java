package com.ekinoks.followme.commclient.view;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.ekinoks.followme.commclient.model.ClientModel;

public class MainPanelController implements IPanelEventListener {

	private MainPanel mainPanel = new MainPanel();
	private ClientModel clientModel;
	private LoginViewController loginViewController;
	private ClientViewController clientViewController;
	private UserLogController userLogController;

	public MainPanelController(ClientModel clientModel) {

		this.clientModel = clientModel;
		loginViewController = new LoginViewController(this.clientModel);
		clientViewController = new ClientViewController(this.clientModel);
		userLogController = new UserLogController(this.clientModel);
		loginViewController.addLoginListener(this);
		clientViewController.addLogoutListener(this);
		mainPanel.add(loginViewController.getView());
		this.clientModel.addConnectionListener(this);
		this.clientModel.getCommunicator().addOnlineUserListener(userLogController);

	}

	public MainPanel getView() {

		return mainPanel;
	}

	@Override
	public void onLogin() {

		mainPanel.remove(loginViewController.getView());
		mainPanel.add(userLogController.getView(), BorderLayout.CENTER);
		mainPanel.add(clientViewController.getView(), BorderLayout.EAST);
		mainPanel.revalidate();
		mainPanel.repaint();
	}

	@Override
	public void onLogout() {

		clientModel.stopComm();
		clientModel.setUserID("");
		clientModel.startComm();
		mainPanel.removeAll();
		mainPanel.add(loginViewController.getView());
		mainPanel.revalidate();
		mainPanel.repaint();
	}

	public ClientViewController getClientViewController() {

		return this.clientViewController;
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

	@Override
	public void failPopup() {

		JFrame frame = new JFrame();

		JOptionPane.showMessageDialog(frame, "Failed to login, try again later!", "Error", JOptionPane.ERROR_MESSAGE);
	}

}
