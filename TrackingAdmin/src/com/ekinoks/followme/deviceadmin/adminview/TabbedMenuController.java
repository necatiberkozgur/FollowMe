package com.ekinoks.followme.deviceadmin.adminview;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.ekinoks.followme.deviceadmin.adminmodel.AdminModel;
import com.ekinoks.ui.icon.IconDefaults;
import com.ekinoks.ui.icon.IconStore;

public class TabbedMenuController {

	private TabbedMenu tabbedMenu;
	private AddClientPanelController addClientPanelController;
	private AddDevicePanelController addDevicePanelController;
	private AdminModel model;
	private JPanel btnPanel;
	private JButton btnPair;

	public TabbedMenuController(AdminModel model) {

		this.model = model;
		tabbedMenu = new TabbedMenu();
		btnPanel = new JPanel();
		btnPair = new JButton("");
		btnPair.setIcon(IconStore.getStore().getIcon(IconDefaults.attach));
		btnPanel.add(btnPair);
		addClientPanelController = new AddClientPanelController(this.model);
		addDevicePanelController = new AddDevicePanelController(this.model);

		this.model.addClientListener(getAddClientPanelController());
		this.model.addDeviceListener(getAddDevicePanelController());
		this.getAddClientPanelController().setAddDevicePanelController(getAddDevicePanelController());
		this.getAddDevicePanelController().setAddClientPanelController(getAddClientPanelController());

		this.tabbedMenu.add("Client", getAddClientPanelController().getView());
		this.tabbedMenu.add("Device", getAddDevicePanelController().getView());
		this.tabbedMenu.setAlignmentX(1.0f);
		this.tabbedMenu.setAlignmentY(0.0f);
	}

	public TabbedMenu getView() {

		return this.tabbedMenu;
	}

	public AddClientPanelController getAddClientPanelController() {

		return addClientPanelController;
	}

	public AddDevicePanelController getAddDevicePanelController() {

		return addDevicePanelController;
	}

}
