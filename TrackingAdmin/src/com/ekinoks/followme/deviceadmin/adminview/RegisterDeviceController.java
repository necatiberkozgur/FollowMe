package com.ekinoks.followme.deviceadmin.adminview;

import java.awt.event.ActionEvent;

import com.ekinoks.followme.deviceadmin.adminmodel.AdminModel;
import com.ekinoks.followme.trackingutils.pbkdf2.ISecurePassword;
import com.ekinoks.followme.trackingutils.users.DeviceUser;

public class RegisterDeviceController {

	private RegisterDevicePanel registerDevicePanel;
	private AdminModel adminModel;

	public RegisterDeviceController(AdminModel adminModel) {

		this.adminModel = adminModel;
		registerDevicePanel = new RegisterDevicePanel();
		registerDevicePanel.getBtnSave().addActionListener(this::onSave);
	}

	public RegisterDevicePanel getView() {

		return this.registerDevicePanel;
	}

	public void onSave(ActionEvent e) {

		String userID = registerDevicePanel.getTextFieldUserID().getText();
		String username = registerDevicePanel.getTextFieldUserSpecific().getText();
		String password = ISecurePassword
				.createSecurePassword(new String(registerDevicePanel.getPasswordField().getPassword()));
		adminModel.addUser(new DeviceUser(username, password, userID));
	}
}
