package com.ekinoks.followme.deviceadmin.adminview;

import java.awt.event.ActionEvent;

import com.ekinoks.followme.deviceadmin.adminmodel.AdminModel;
import com.ekinoks.followme.trackingutils.pbkdf2.ISecurePassword;
import com.ekinoks.followme.trackingutils.users.ClientUser;

public class RegisterClientController {

	RegisterClientPanel registerClientPanel;
	AdminModel adminModel;

	public RegisterClientController(AdminModel adminModel) {

		this.adminModel = adminModel;
		registerClientPanel = new RegisterClientPanel();
		registerClientPanel.getBtnSave().addActionListener(this::onSave);
	}

	public void onSave(ActionEvent e) {

		String userID = registerClientPanel.getTextFieldUserID().getText();
		String username = registerClientPanel.getTextFieldUserSpecific().getText();
		String password = ISecurePassword
				.createSecurePassword(new String(registerClientPanel.getPasswordField().getPassword()));
		adminModel.addUser(new ClientUser(username, password, userID));
	}

	public RegisterClientPanel getView() {

		return this.registerClientPanel;
	}
}
