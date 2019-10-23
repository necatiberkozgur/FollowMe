package com.ekinoks.followme.deviceadmin.adminview;

import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.ekinoks.followme.deviceadmin.adminmodel.AdminModel;

public class AdminLoginPanelController {

	private Set<IPanelEventListener> successfulLoginListeners = new HashSet<>();

	private AdminLoginPanel adminLoginPanel;
	private AdminModel model;

	public AdminLoginPanelController(AdminModel model) {

		this.model = model;

		adminLoginPanel = new AdminLoginPanel();
		adminLoginPanel.getPasswordField().addActionListener(this::onAdminLogin);
		adminLoginPanel.getBtnLogin().addActionListener(this::onAdminLogin);
	}

	private void onAdminLogin(ActionEvent e) {

		String enteredUsername = adminLoginPanel.getNameField().getText();
		String enteredPassword = new String(adminLoginPanel.getPasswordField().getPassword());
		String username = model.getUsernameAdmin();
		String password = model.getPasswordAdmin();

		if (username.equals(enteredUsername) && password.equals(enteredPassword)) {

			invokeSuccessfulLoginListeners();
			if (model.getServerConnectionStatus() == false) {

				model.startServerCommunication();
			}

			JFrame frame = new JFrame();
			JOptionPane.showMessageDialog(frame, "Login Successful.", "Login Details", JOptionPane.INFORMATION_MESSAGE);

		} else if (enteredPassword.equals("") || enteredUsername.equals("")) {

			JFrame frame = new JFrame();
			JOptionPane.showMessageDialog(frame, "Username/Password field cannot be empty.", "Error!",
					JOptionPane.WARNING_MESSAGE);

		} else {

			JFrame frame = new JFrame();
			JOptionPane.showMessageDialog(frame, "Invalid username/password.", "Login Details",
					JOptionPane.ERROR_MESSAGE);
		}

		adminLoginPanel.getNameField().setText("");
		adminLoginPanel.getPasswordField().setText("");
	}

	public AdminLoginPanel getView() {

		return adminLoginPanel;
	}

	public void addSuccessfulLoginListener(IPanelEventListener listener) {

		if (successfulLoginListeners.contains(listener) == false) {

			successfulLoginListeners.add(listener);
		}
	}

	public void removeSuccessfulLoginListener(IPanelEventListener listener) {

		if (successfulLoginListeners.contains(listener) == true) {

			successfulLoginListeners.remove(listener);
		}
	}

	private void invokeSuccessfulLoginListeners() {

		for (IPanelEventListener listener : successfulLoginListeners) {

			listener.onSuccessSignin();
		}
	}

}
