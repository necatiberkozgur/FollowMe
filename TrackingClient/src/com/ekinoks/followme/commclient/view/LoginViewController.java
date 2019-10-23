package com.ekinoks.followme.commclient.view;

import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.ekinoks.followme.commclient.model.ClientModel;

public class LoginViewController {

	private LoginView loginView;
	private Set<IPanelEventListener> loginListeners = new HashSet<IPanelEventListener>();
	private ClientModel clientModel;

	public LoginViewController(ClientModel clientModel) {

		this.clientModel = clientModel;
		loginView = new LoginView();
		loginView.getBtnLogin().addActionListener(this::onLogin);
		loginView.getPasswordField().addActionListener(this::onLogin);
	}

	private void onLogin(ActionEvent e) {

		if (loginView.getTextField().getText().length() != 0
				&& loginView.getPasswordField().getPassword().length != 0) {

			String enteredUsername = loginView.getTextField().getText();
			String enteredPassword = new String(loginView.getPasswordField().getPassword());

			clientModel.login(enteredUsername, enteredPassword);

		} else {

			emptyFieldPopup();
			clearLoginFields();
		}

	}

	@SuppressWarnings("unused")
	private void invokeLoginListeners() {

		for (IPanelEventListener iPanelEventListener : loginListeners) {

			iPanelEventListener.onLogin();
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
