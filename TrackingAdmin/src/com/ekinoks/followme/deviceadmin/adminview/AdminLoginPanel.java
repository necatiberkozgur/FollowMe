package com.ekinoks.followme.deviceadmin.adminview;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class AdminLoginPanel extends JPanel {

	private JButton btnLogin;
	private JTextField nameField;
	private JPasswordField passwordField;
	private JLabel lblUsername;
	private JLabel lblPassword;

	public AdminLoginPanel() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 83, 69, 124, 0 };
		gridBagLayout.rowHeights = new int[] { 91, 19, 19, 25, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		lblUsername = new JLabel("Username");
		GridBagConstraints gbc_lblUsername = new GridBagConstraints();
		gbc_lblUsername.anchor = GridBagConstraints.WEST;
		gbc_lblUsername.insets = new Insets(0, 0, 5, 5);
		gbc_lblUsername.gridx = 1;
		gbc_lblUsername.gridy = 1;
		add(getLblUsername(), gbc_lblUsername);

		nameField = new JTextField(10);
		GridBagConstraints gbc_nameField = new GridBagConstraints();
		gbc_nameField.anchor = GridBagConstraints.NORTHWEST;
		gbc_nameField.insets = new Insets(0, 0, 5, 0);
		gbc_nameField.gridx = 2;
		gbc_nameField.gridy = 1;
		add(getNameField(), gbc_nameField);

		lblPassword = new JLabel("Password");
		GridBagConstraints gbc_lblPassword = new GridBagConstraints();
		gbc_lblPassword.anchor = GridBagConstraints.EAST;
		gbc_lblPassword.insets = new Insets(0, 0, 5, 5);
		gbc_lblPassword.gridx = 1;
		gbc_lblPassword.gridy = 2;
		add(getLblPassword(), gbc_lblPassword);

		passwordField = new JPasswordField(10);
		GridBagConstraints gbc_passwordField = new GridBagConstraints();
		gbc_passwordField.anchor = GridBagConstraints.NORTHWEST;
		gbc_passwordField.insets = new Insets(0, 0, 5, 0);
		gbc_passwordField.gridx = 2;
		gbc_passwordField.gridy = 2;
		add(getPasswordField(), gbc_passwordField);

		btnLogin = new JButton("Login");
		GridBagConstraints gbc_btnLogin = new GridBagConstraints();
		gbc_btnLogin.anchor = GridBagConstraints.NORTH;
		gbc_btnLogin.gridx = 2;
		gbc_btnLogin.gridy = 3;
		add(getBtnLogin(), gbc_btnLogin);

	}

	public JButton getBtnLogin() {

		return btnLogin;
	}

	public JTextField getNameField() {

		return nameField;
	}

	public JPasswordField getPasswordField() {

		return passwordField;
	}

	public JLabel getLblUsername() {

		return lblUsername;
	}

	public JLabel getLblPassword() {

		return lblPassword;
	}

}
