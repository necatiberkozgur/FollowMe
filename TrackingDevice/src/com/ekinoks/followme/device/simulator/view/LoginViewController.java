package com.ekinoks.followme.device.simulator.view;

import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.ekinoks.followme.device.simulator.model.DeviceModel;

public class LoginViewController {

	private LoginView loginView;
	private DeviceModel deviceModel;
	private Set<IPanelEventListener> loginListeners = new HashSet<IPanelEventListener>();

	public LoginViewController(DeviceModel deviceModel) {

		this.deviceModel = deviceModel;
		loginView = new LoginView();
		loginView.getBtnLogin().addActionListener(this::onLogin);
		loginView.getPasswordField().addActionListener(this::onLogin);

	}

	@SuppressWarnings("unused")
	private void invokeLoginListeners() {

		for (IPanelEventListener iPanelEventListener : loginListeners) {

			iPanelEventListener.onLogin();
		}
	}

	private void onLogin(ActionEvent e) {

		if (loginView.getTextField().getText().length() != 0
				&& loginView.getPasswordField().getPassword().length != 0) {

			String enteredUsername = loginView.getTextField().getText();
			String enteredPassword = new String(loginView.getPasswordField().getPassword());

			deviceModel.login(enteredUsername, enteredPassword);

		} else {

			emptyFieldPopup();
			clearLoginFields();
		}

	}

	private void emptyFieldPopup() {

		JFrame frame = new JFrame();

		JOptionPane.showMessageDialog(frame, "Username / Password fields cannot be empty!", "Error",
				JOptionPane.ERROR_MESSAGE);
	}

	private void clearLoginFields() {

		loginView.getTextField().setText("");
		loginView.getPasswordField().setText("");
	}

	public void addLoginListener(IPanelEventListener listener) {

		if (loginListeners.contains(listener) == false) {

			loginListeners.add(listener);
		}
	}

	public void removeLoginListener(IPanelEventListener listener) {

		if (loginListeners.contains(listener) == true) {

			loginListeners.remove(listener);
		}
	}

	public LoginView getView() {

		return loginView;
	}
}
