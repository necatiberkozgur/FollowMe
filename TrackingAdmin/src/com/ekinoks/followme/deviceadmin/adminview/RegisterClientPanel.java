package com.ekinoks.followme.deviceadmin.adminview;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.ekinoks.ui.icon.IconDefaults;
import com.ekinoks.ui.icon.IconStore;

@SuppressWarnings("serial")
public class RegisterClientPanel extends JPanel {

	private JTextField textFieldUserID;
	private JPasswordField passwordField;
	private JTextField textFieldUserSpecific;
	private JLabel lblUserId;
	private JLabel lblPassword;
	private JLabel lblUserSpecific;
	private JButton btnSave;

	public RegisterClientPanel() {

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 115, 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		lblUserId = new JLabel("User ID");
		GridBagConstraints gbc_lblUserId = new GridBagConstraints();
		gbc_lblUserId.anchor = GridBagConstraints.WEST;
		gbc_lblUserId.insets = new Insets(0, 0, 5, 5);
		gbc_lblUserId.gridx = 0;
		gbc_lblUserId.gridy = 0;
		add(lblUserId, gbc_lblUserId);

		textFieldUserID = new JTextField();
		GridBagConstraints gbc_textFieldUserID = new GridBagConstraints();
		gbc_textFieldUserID.insets = new Insets(0, 0, 5, 5);
		gbc_textFieldUserID.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldUserID.gridx = 1;
		gbc_textFieldUserID.gridy = 0;
		add(getTextFieldUserID(), gbc_textFieldUserID);
		getTextFieldUserID().setColumns(10);

		lblPassword = new JLabel("Password");
		GridBagConstraints gbc_lblPassword = new GridBagConstraints();
		gbc_lblPassword.anchor = GridBagConstraints.WEST;
		gbc_lblPassword.insets = new Insets(0, 0, 5, 5);
		gbc_lblPassword.gridx = 0;
		gbc_lblPassword.gridy = 1;
		add(lblPassword, gbc_lblPassword);

		passwordField = new JPasswordField();
		GridBagConstraints gbc_passwordField = new GridBagConstraints();
		gbc_passwordField.insets = new Insets(0, 0, 5, 5);
		gbc_passwordField.fill = GridBagConstraints.HORIZONTAL;
		gbc_passwordField.gridx = 1;
		gbc_passwordField.gridy = 1;
		add(getPasswordField(), gbc_passwordField);
		lblUserSpecific = new JLabel("Name");

		GridBagConstraints gbc_lblUserSpecific = new GridBagConstraints();
		gbc_lblUserSpecific.anchor = GridBagConstraints.WEST;
		gbc_lblUserSpecific.insets = new Insets(0, 0, 5, 5);
		gbc_lblUserSpecific.gridx = 0;
		gbc_lblUserSpecific.gridy = 2;
		add(lblUserSpecific, gbc_lblUserSpecific);

		textFieldUserSpecific = new JTextField();
		GridBagConstraints gbc_textFieldUserSpecific = new GridBagConstraints();
		gbc_textFieldUserSpecific.insets = new Insets(0, 0, 5, 5);
		gbc_textFieldUserSpecific.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldUserSpecific.gridx = 1;
		gbc_textFieldUserSpecific.gridy = 2;
		add(textFieldUserSpecific, gbc_textFieldUserSpecific);
		textFieldUserSpecific.setColumns(10);

		btnSave = new JButton("");
		getBtnSave().setIcon(IconStore.getStore().getIcon(IconDefaults.save));
		GridBagConstraints gbc_button = new GridBagConstraints();
		gbc_button.insets = new Insets(0, 0, 5, 5);
		gbc_button.gridx = 1;
		gbc_button.gridy = 3;
		add(getBtnSave(), gbc_button);
	}

	public JTextField getTextFieldUserID() {

		return textFieldUserID;
	}

	public JPasswordField getPasswordField() {

		return passwordField;
	}

	public JButton getBtnSave() {

		return btnSave;
	}

	public JTextField getTextFieldUserSpecific() {

		return textFieldUserSpecific;
	}

}
