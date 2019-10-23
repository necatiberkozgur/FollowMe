package com.ekinoks.followme.deviceadmin.adminview;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import com.ekinoks.followme.deviceadmin.adminmodel.AdminModel;
import com.ekinoks.followme.trackingutils.users.DeviceUser;
import com.ekinoks.followme.trackingutils.users.IUser;
import com.ekinoks.followme.trackingutils.users.User;
import com.ekinoks.followme.trackingutils.users.UserType;
import com.ekinoks.followme.trackingutils.users.UserWrapper;
import com.ekinoks.ui.components.table.TableColumnPreference;

public class AddDevicePanelController implements IDevicePanel {

	private AddDevicePanel addDevicePanel;
	private AdminModel adminModel;
	private AddClientPanelController addClientPanelController;
	private Set<IPairPanel> addedPairListeners = Collections.synchronizedSet(new HashSet<IPairPanel>());
	private MainPanelController parentPanel;

	public AddDevicePanelController(AdminModel adminModel) {

		this.adminModel = adminModel;
		addDevicePanel = new AddDevicePanel();

		addDevicePanel.getTableDevices().setFilterable(true);
		addDevicePanel.getTableDevices().setSortable(true);
		addDevicePanel.getTableDevices().setContextMenuVisible(true);
		addDevicePanel.getTableDevices().getSelectionModel()
				.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		addDevicePanel.getTableSelection().setFilterable(true);
		addDevicePanel.getTableSelection().setSortable(true);
		addDevicePanel.getTableSelection().setContextMenuVisible(true);
		addDevicePanel.getTableSelection().getSelectionModel()
				.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		addDevicePanel.getBtnDeleteUser().addActionListener(this::onDelete);
		addDevicePanel.getBtnAdminLogout().addActionListener(this::onLogout);
		addDevicePanel.getBtnBack().addActionListener(this::onBack);
		addDevicePanel.getBtnNext().addActionListener(this::onNext);
		addDevicePanel.getBtnSelectForPair().addActionListener(this::onSelectUserForPair);
		addDevicePanel.getBtnRemoveSelection().addActionListener(this::onRemoveSelection);
		addDevicePanel.getBtnAddUser().addActionListener(this::onAddUser);
		addDevicePanel.getBtnPair().addActionListener(this::onPair);
		addDevicePanel.getBtnBack().setVisible(false);

		addDevicePanel.getTableDevices().setColumnPreferences(new ArrayList<>(Arrays.asList(
				new TableColumnPreference(0, true, "com.ekinoks.followme.trackingutils.users.IUser.userID"),
				new TableColumnPreference(1, false, "com.ekinoks.followme.trackingutils.users.IUser.userType"),
				new TableColumnPreference(1, true, "com.ekinoks.followme.trackingutils.users.IUser.dateAdded"))));

		addDevicePanel.getTableSelection().setColumnPreferences(new ArrayList<>(Arrays.asList(
				new TableColumnPreference(0, true, "com.ekinoks.followme.trackingutils.users.IUser.userID"),
				new TableColumnPreference(1, false, "com.ekinoks.followme.trackingutils.users.IUser.userType"),
				new TableColumnPreference(1, true, "com.ekinoks.followme.trackingutils.users.IUser.dateAdded"))));

		addDevicePanel.getTableDevices().addMouseListener(new MouseAdapter() {

			public void mousePressed(MouseEvent mouseEvent) {

				JTable table = (JTable) mouseEvent.getSource();
				Point point = mouseEvent.getPoint();
				String detailMessage;

				if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1) {

					detailMessage = ((UserWrapper) addDevicePanel.getTableDevices().getRow(table.rowAtPoint(point)))
							.getUserSpecificFields();

					printUserSpecificFields(detailMessage);
				}
			}
		});

		addDevicePanel.getTableSelection().addMouseListener(new MouseAdapter() {

			public void mousePressed(MouseEvent mouseEvent) {

				JTable table = (JTable) mouseEvent.getSource();
				Point point = mouseEvent.getPoint();

				if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1) {

					IUser selectedUser = addDevicePanel.getTableSelection().getRow(table.rowAtPoint(point));
					ShowPairsController pairWindow = new ShowPairsController(adminModel, selectedUser);
					addAddedPairListener(pairWindow);
					adminModel.requestPairs(selectedUser, 0);
					JFrame frame = new JFrame("Pairs");

					frame.addWindowListener(new WindowAdapter() {

						@Override
						public void windowClosed(WindowEvent e) {

							removeAddedPairListener(pairWindow);
						}
					});

					frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					frame.setLayout(new BorderLayout());
					frame.add(pairWindow.getView());
					frame.pack();
					frame.setVisible(true);

				}
			}
		});

	}

	private void onDelete(ActionEvent e) {

		List<IUser> selectedUsers = addDevicePanel.getTableDevices().getSelectedValues();

		for (IUser selection : selectedUsers) {

			DeviceUser deletedUser = new DeviceUser("-", "password", selection.getUserID(), LocalDateTime.now());
			adminModel.deleteUser(deletedUser);
		}

	}

	private void onLogout(ActionEvent e) {

		addDevicePanel.initialize();
		parentPanel.onSuccessSignOut();
	}

	private void printUserSpecificFields(String str) {

		if (str != null) {

			JFrame frame = new JFrame();
			JOptionPane.showMessageDialog(frame, "Manufacturer: " + str, "Details", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	public AddDevicePanel getView() {

		return addDevicePanel;
	}

	@Override
	public void onAddUser(User u) {

		addDevicePanel.getTableDevices().addRow(new UserWrapper(u));
		addDevicePanel.revalidate();
		addDevicePanel.repaint();
	}

	@Override
	public void onDeleteUserFromList(String u) {

		for (IUser user : addDevicePanel.getTableDevices().getAllRows()) {

			if (user.getUserID().equals(u)) {

				addDevicePanel.getTableDevices().removeRow(user);
				break;
			}
		}
	}

	public void onNext(ActionEvent e) {

		int currentPage = Integer.parseInt(addDevicePanel.getLblPageNum().getText());

		addDevicePanel.getTableDevices().removeAllRows();
		adminModel.fetchUsers(UserType.Device, currentPage);
		addDevicePanel.getLblPageNum().setText((new Integer(currentPage + 1)).toString());

		currentPage = Integer.parseInt(addDevicePanel.getLblPageNum().getText());

		if (currentPage > 1) {

			addDevicePanel.getBtnBack().setVisible(true);

		} else {

			addDevicePanel.getBtnBack().setVisible(false);
		}

		if ((currentPage) * 25 >= adminModel.getDeviceCount()) {

			addDevicePanel.getBtnNext().setVisible(false);

		} else {

			addDevicePanel.getBtnNext().setVisible(true);
		}
	}

	public void onBack(ActionEvent e) {

		int currentPage = Integer.parseInt(addDevicePanel.getLblPageNum().getText());

		addDevicePanel.getTableDevices().removeAllRows();

		adminModel.fetchUsers(UserType.Device, currentPage - 2);

		addDevicePanel.getLblPageNum().setText((new Integer(currentPage - 1)).toString());

		currentPage = Integer.parseInt(addDevicePanel.getLblPageNum().getText());

		if (currentPage > 1) {

			addDevicePanel.getBtnBack().setVisible(true);

		} else {

			addDevicePanel.getBtnBack().setVisible(false);
		}

		if ((currentPage) * 10 >= adminModel.getDeviceCount()) {

			addDevicePanel.getBtnNext().setVisible(false);

		} else {

			addDevicePanel.getBtnNext().setVisible(true);
		}
	}

	public void onSelectUserForPair(ActionEvent e) {

		List<IUser> selected = addDevicePanel.getTableDevices().getSelectedValues();

		for (IUser user : selected) {

			if (addDevicePanel.getTableSelection().getAllRows().contains(user) == false) {

				addDevicePanel.getTableSelection().addRow(user);
			}

		}
	}

	public void onPair(ActionEvent e) {

		List<IUser> selectedDevices = new ArrayList<IUser>(addDevicePanel.getTableSelection().getAllRows());

		List<IUser> selectedClients = new ArrayList<IUser>(addClientPanelController.getSelectedClients());

		if (selectedClients.isEmpty() == false && selectedDevices.isEmpty() == false) {

			for (IUser device : selectedDevices) {

				for (IUser client : selectedClients) {

					adminModel.addPair(device.getUserID(), client.getUserID());
				}
			}
		}
	}

	public void onRemoveSelection(ActionEvent e) {

		List<IUser> selected = addDevicePanel.getTableSelection().getSelectedValues();

		for (IUser user : selected) {

			addDevicePanel.getTableSelection().removeRow(user);
		}
	}

	public List<IUser> getSelectedDevices() {

		return addDevicePanel.getTableSelection().getAllRows();
	}

	public void setAddClientPanelController(AddClientPanelController addClientPanelController) {

		this.addClientPanelController = addClientPanelController;
	}

	@Override
	public void onAddPair(IUser user1, IUser user2, int pairCount) {

		dispatchReceivedPairs(user1, user2, pairCount);
	}

	public void addAddedPairListener(IPairPanel listener) {

		if (addedPairListeners.contains(listener) == false) {

			addedPairListeners.add(listener);
		}
	}

	public void removeAddedPairListener(IPairPanel listener) {

		if (addedPairListeners.contains(listener) == true) {

			addedPairListeners.remove(listener);
		}
	}

	public void onAddUser(ActionEvent e) {

		JFrame frame = new JFrame("Add User");
		RegisterDeviceController registerDeviceController = new RegisterDeviceController(adminModel);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setLocation(addDevicePanel.getLocationOnScreen().x + addDevicePanel.getWidth() / 2,
				addDevicePanel.getLocationOnScreen().y + addDevicePanel.getHeight() / 2);
		frame.setAlwaysOnTop(true);
		frame.add(registerDeviceController.getView());
		frame.pack();
		frame.setVisible(true);
	}

	private void dispatchReceivedPairs(IUser user1, IUser user2, int pairCount) {

		for (IPairPanel pairListener : addedPairListeners) {

			if (pairListener.getUserID().equals(user1.getUserID())) {

				pairListener.onAddPairToTable(user2, pairCount);
			}
		}
	}

	public MainPanelController getParentPanel() {
		return parentPanel;
	}

	public void setParentPanel(MainPanelController parentPanel) {
		this.parentPanel = parentPanel;
	}
}
