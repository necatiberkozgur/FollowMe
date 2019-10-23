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
import com.ekinoks.followme.trackingutils.users.ClientUser;
import com.ekinoks.followme.trackingutils.users.IUser;
import com.ekinoks.followme.trackingutils.users.User;
import com.ekinoks.followme.trackingutils.users.UserType;
import com.ekinoks.followme.trackingutils.users.UserWrapper;
import com.ekinoks.ui.components.table.TableColumnPreference;

public class AddClientPanelController implements IClientPanel {

	private AddClientPanel addClientPanel;
	private AdminModel adminModel;
	private AddDevicePanelController addDevicePanelController;
	private Set<IPairPanel> addedPairListeners = Collections.synchronizedSet(new HashSet<IPairPanel>());
	private MainPanelController parentPanel;

	public AddClientPanelController(AdminModel adminModel) {

		this.adminModel = adminModel;
		addClientPanel = new AddClientPanel();

		addClientPanel.getTableClients().setFilterable(true);
		addClientPanel.getTableClients().setSortable(true);
		addClientPanel.getTableClients().setContextMenuVisible(true);
		addClientPanel.getTableClients().getSelectionModel()
				.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		addClientPanel.getTableSelection().setFilterable(true);
		addClientPanel.getTableSelection().setSortable(true);
		addClientPanel.getTableSelection().setContextMenuVisible(true);
		addClientPanel.getTableSelection().getSelectionModel()
				.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		addClientPanel.getBtnDeleteUser().addActionListener(this::onDelete);
		addClientPanel.getBtnAdminLogout().addActionListener(this::onLogout);
		addClientPanel.getBtnNext().addActionListener(this::onNext);
		addClientPanel.getBtnBack().addActionListener(this::onBack);
		addClientPanel.getBtnSelect().addActionListener(this::onSelectUserForPair);
		addClientPanel.getBtnRemoveSelection().addActionListener(this::onRemoveSelection);
		addClientPanel.getBtnPair().addActionListener(this::onPair);
		addClientPanel.getBtnNewButton().addActionListener(this::btnClicked);
		addClientPanel.getBtnBack().setVisible(false);

		addClientPanel.getTableClients().setColumnPreferences(new ArrayList<>(Arrays.asList(
				new TableColumnPreference(0, true, "com.ekinoks.followme.trackingutils.users.IUser.userID"),
				new TableColumnPreference(1, false, "com.ekinoks.followme.trackingutils.users.IUser.userType"),
				new TableColumnPreference(1, true, "com.ekinoks.followme.trackingutils.users.IUser.dateAdded"))));

		addClientPanel.getTableSelection().setColumnPreferences(new ArrayList<>(Arrays.asList(
				new TableColumnPreference(0, true, "com.ekinoks.followme.trackingutils.users.IUser.userID"),
				new TableColumnPreference(1, false, "com.ekinoks.followme.trackingutils.users.IUser.userType"),
				new TableColumnPreference(1, true, "com.ekinoks.followme.trackingutils.users.IUser.dateAdded"))));

		addClientPanel.getTableClients().addMouseListener(new MouseAdapter() {

			public void mousePressed(MouseEvent mouseEvent) {

				JTable table = (JTable) mouseEvent.getSource();
				Point point = mouseEvent.getPoint();
				String detailMessage;

				if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1) {

					detailMessage = ((UserWrapper) addClientPanel.getTableClients().getRow(table.rowAtPoint(point)))
							.getUserSpecificFields();

					printUserSpecificFields(detailMessage);
				}
			}
		});

		addClientPanel.getTableSelection().addMouseListener(new MouseAdapter() {

			public void mousePressed(MouseEvent mouseEvent) {

				JTable table = (JTable) mouseEvent.getSource();
				Point point = mouseEvent.getPoint();

				if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1) {

					IUser selectedUser = addClientPanel.getTableSelection().getRow(table.rowAtPoint(point));
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

		List<IUser> selectedUsers = addClientPanel.getTableClients().getSelectedValues();

		for (IUser selection : selectedUsers) {

			System.out.println(selection.getDateAdded());

			ClientUser deletedUser = new ClientUser("-", "password", selection.getUserID(), LocalDateTime.now());
			adminModel.deleteUser(deletedUser);
		}
	}

	private void onLogout(ActionEvent e) {

		addClientPanel.initialize();
		parentPanel.onSuccessSignOut();
	}

	private void printUserSpecificFields(String str) {

		if (str != null) {

			JFrame frame = new JFrame();
			JOptionPane.showMessageDialog(frame, "Name: " + str, "Details", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	public AddClientPanel getView() {

		return addClientPanel;
	}

	@Override
	public void onAddUser(User u) {

		addClientPanel.getTableClients().addRow(new UserWrapper(u));
	}

	@Override
	public void onDeleteUserFromList(String u) {

		ArrayList<IUser> users = (ArrayList<IUser>) addClientPanel.getTableClients().getAllRows();

		for (IUser user : users) {

			if (user.getUserID().equals(u)) {

				addClientPanel.getTableClients().removeRow(user);
				break;
			}
		}
	}

	public void onNext(ActionEvent e) {

		int currentPage = Integer.parseInt(addClientPanel.getLblPageNum().getText());

		addClientPanel.getTableClients().removeAllRows();
		adminModel.fetchUsers(UserType.Client, currentPage);
		addClientPanel.getLblPageNum().setText((new Integer(currentPage + 1)).toString());

		currentPage = Integer.parseInt(addClientPanel.getLblPageNum().getText());

		if (currentPage > 1) {

			addClientPanel.getBtnBack().setVisible(true);

		} else {

			addClientPanel.getBtnBack().setVisible(false);
		}

		if ((currentPage) * 25 >= adminModel.getClientCount()) {

			addClientPanel.getBtnNext().setVisible(false);

		} else {

			addClientPanel.getBtnNext().setVisible(true);
		}
	}

	public void onBack(ActionEvent e) {

		int currentPage = Integer.parseInt(addClientPanel.getLblPageNum().getText());

		addClientPanel.getTableClients().removeAllRows();

		adminModel.fetchUsers(UserType.Client, currentPage - 2);

		addClientPanel.getLblPageNum().setText((new Integer(currentPage - 1)).toString());

		currentPage = Integer.parseInt(addClientPanel.getLblPageNum().getText());

		if (currentPage > 1) {

			addClientPanel.getBtnBack().setVisible(true);

		} else {

			addClientPanel.getBtnBack().setVisible(false);
		}

		if ((currentPage) * 25 >= adminModel.getClientCount()) {

			addClientPanel.getBtnNext().setVisible(false);

		} else {

			addClientPanel.getBtnNext().setVisible(true);
		}
	}

	public void onSelectUserForPair(ActionEvent e) {

		List<IUser> selected = addClientPanel.getTableClients().getSelectedValues();

		for (IUser user : selected) {

			if (addClientPanel.getTableSelection().getAllRows().contains(user) == false) {

				addClientPanel.getTableSelection().addRow(user);
			}
		}
	}

	public void onRemoveSelection(ActionEvent e) {

		List<IUser> selected = addClientPanel.getTableSelection().getSelectedValues();

		for (IUser user : selected) {

			addClientPanel.getTableSelection().removeRow(user);
		}
	}

	public void onPair(ActionEvent e) {

		List<IUser> selectedClients = new ArrayList<IUser>(addClientPanel.getTableSelection().getAllRows());

		List<IUser> selectedDevices = new ArrayList<IUser>(addDevicePanelController.getSelectedDevices());

		if (selectedClients.isEmpty() == false && selectedDevices.isEmpty() == false) {

			for (IUser device : selectedDevices) {

				for (IUser client : selectedClients) {

					adminModel.addPair(device.getUserID(), client.getUserID());
				}
			}
		}
	}

	public List<IUser> getSelectedClients() {

		return addClientPanel.getTableSelection().getAllRows();

	}

	@Override
	public void onAddPair(IUser user1, IUser user2, int pairCount) {

		dispatchReceivedPairs(user1, user2, pairCount);

	}

	public void setAddDevicePanelController(AddDevicePanelController addDevicePanelController) {

		this.addDevicePanelController = addDevicePanelController;
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

	private void dispatchReceivedPairs(IUser user1, IUser user2, int pairCount) {

		for (IPairPanel pairListener : addedPairListeners) {

			if (user1.getUserID().equals(pairListener.getUserID()))
				pairListener.onAddPairToTable(user2, pairCount);
		}
	}

	private void btnClicked(ActionEvent e) {

		JFrame frame = new JFrame("Add User");
		RegisterClientController registerClientController = new RegisterClientController(adminModel);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		frame.setLocation(addClientPanel.getLocationOnScreen().x + addClientPanel.getWidth() / 2,
				addClientPanel.getLocationOnScreen().y + addClientPanel.getHeight() / 2);

		frame.setAlwaysOnTop(true);
		frame.add(registerClientController.getView());
		frame.pack();
		frame.setVisible(true);
	}

	public MainPanelController getParentPanel() {
		return parentPanel;
	}

	public void setParentPanel(MainPanelController parentPanel) {
		this.parentPanel = parentPanel;
	}
}
