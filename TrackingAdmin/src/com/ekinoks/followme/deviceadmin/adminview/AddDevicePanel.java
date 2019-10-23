package com.ekinoks.followme.deviceadmin.adminview;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import com.ekinoks.followme.trackingutils.users.IUser;
import com.ekinoks.ui.components.listtable.ListTable;
import com.ekinoks.ui.icon.IconDefaults;
import com.ekinoks.ui.icon.IconStore;

@SuppressWarnings("serial")
public class AddDevicePanel extends JPanel {

	private JButton buttonNext;
	private JButton btnPair;
	private JButton btnDeleteUser;
	private JButton btnSaveUser;
	private JButton btnAdminLogout;
	private JButton buttonBack;
	private JLabel lblPageNum;
	private JButton btnSelect;
	private JButton btnRemoveSelection;
	private ListTable<IUser> tableSelection;
	private ListTable<IUser> tableDevices;

	public AddDevicePanel() {

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, 1.0 };
		gridBagLayout.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.gridwidth = 2;
		gbc_panel.gridheight = 2;
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		add(panel, gbc_panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 210, 210 };
		gbl_panel.rowHeights = new int[] { 43, 0, 0 };
		gbl_panel.columnWeights = new double[] { 1.0, 1.0 };
		gbl_panel.rowWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		panel.setLayout(gbl_panel);

		JPanel panelToolBar = new JPanel();
		GridBagConstraints gbc_panelToolBar = new GridBagConstraints();
		gbc_panelToolBar.gridwidth = 2;
		gbc_panelToolBar.insets = new Insets(0, 0, 5, 0);
		gbc_panelToolBar.fill = GridBagConstraints.BOTH;
		gbc_panelToolBar.gridx = 0;
		gbc_panelToolBar.gridy = 0;
		panel.add(panelToolBar, gbc_panelToolBar);
		GridBagLayout gbl_panelToolBar = new GridBagLayout();
		gbl_panelToolBar.columnWidths = new int[] { 0 };
		gbl_panelToolBar.rowHeights = new int[] { 0 };
		gbl_panelToolBar.columnWeights = new double[] { 0.0 };
		gbl_panelToolBar.rowWeights = new double[] { 0.0 };
		panelToolBar.setLayout(gbl_panelToolBar);

		JToolBar toolBar = new JToolBar();
		GridBagConstraints gbc_toolBar = new GridBagConstraints();
		gbc_toolBar.anchor = GridBagConstraints.SOUTH;
		gbc_toolBar.insets = new Insets(5, 5, 0, 0);
		gbc_toolBar.gridx = 0;
		gbc_toolBar.gridy = 0;
		toolBar.setFloatable(false);
		panelToolBar.add(toolBar, gbc_toolBar);

		btnPair = new JButton("");
		btnPair.setToolTipText("Pair selected users(s).");
		btnPair.setIcon(IconStore.getStore().getIcon(IconDefaults.attach));
		toolBar.add(btnPair);

		btnDeleteUser = new JButton("");
		btnDeleteUser.setToolTipText("Delete selected user(s).");
		btnDeleteUser.setIcon(IconStore.getStore().getIcon(IconDefaults.trash));
		toolBar.add(btnDeleteUser);

		btnSaveUser = new JButton("");
		btnDeleteUser.setToolTipText("Add new user.");
		btnSaveUser.setIcon(IconStore.getStore().getIcon(IconDefaults.save));
		toolBar.add(btnSaveUser);

		btnAdminLogout = new JButton("");
		btnAdminLogout.setToolTipText("Log out.");
		toolBar.add(btnAdminLogout);
		btnAdminLogout.setIcon(IconStore.getStore().getIcon(IconDefaults.exit));

		JPanel panelDevice = new JPanel();
		GridBagConstraints gbc_panelDevice = new GridBagConstraints();
		gbc_panelDevice.insets = new Insets(0, 0, 0, 5);
		gbc_panelDevice.fill = GridBagConstraints.BOTH;
		gbc_panelDevice.gridx = 0;
		gbc_panelDevice.gridy = 1;
		panel.add(panelDevice, gbc_panelDevice);
		GridBagLayout gbl_panelDevice = new GridBagLayout();
		gbl_panelDevice.columnWidths = new int[] { 0 };
		gbl_panelDevice.rowHeights = new int[] { 0, 40 };
		gbl_panelDevice.columnWeights = new double[] { 1.0 };
		gbl_panelDevice.rowWeights = new double[] { 1.0, 0.0 };
		panelDevice.setLayout(gbl_panelDevice);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBorder(BorderFactory.createTitledBorder("Current Devices"));
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.insets = new Insets(5, 5, 5, 5);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		panelDevice.add(scrollPane, gbc_scrollPane);

		JPanel panelDeviceButtons = new JPanel();
		GridBagConstraints gbc_panelDeviceButtons = new GridBagConstraints();
		gbc_panelDeviceButtons.fill = GridBagConstraints.BOTH;
		gbc_panelDeviceButtons.gridx = 0;
		gbc_panelDeviceButtons.gridy = 1;
		panelDevice.add(panelDeviceButtons, gbc_panelDeviceButtons);
		GridBagLayout gbl_panelDeviceButtons = new GridBagLayout();
		gbl_panelDeviceButtons.columnWidths = new int[] { 0, 0, 0 };
		gbl_panelDeviceButtons.rowHeights = new int[] { 0 };
		gbl_panelDeviceButtons.columnWeights = new double[] { 0.0, 0.0, 0.0 };
		gbl_panelDeviceButtons.rowWeights = new double[] { 0.0 };
		panelDeviceButtons.setLayout(gbl_panelDeviceButtons);

		buttonBack = new JButton("");
		buttonBack.setIcon(IconStore.getStore().getIcon(IconDefaults.imlec_sola));
		GridBagConstraints gbc_btnBack = new GridBagConstraints();
		gbc_btnBack.insets = new Insets(5, 5, 5, 5);
		gbc_btnBack.gridx = 0;
		gbc_btnBack.gridy = 0;
		panelDeviceButtons.add(buttonBack, gbc_btnBack);

		lblPageNum = new JLabel("1");
		GridBagConstraints gbc_lblPageNum = new GridBagConstraints();
		gbc_lblPageNum.insets = new Insets(5, 5, 5, 5);
		gbc_lblPageNum.gridx = 1;
		gbc_lblPageNum.gridy = 0;
		panelDeviceButtons.add(lblPageNum, gbc_lblPageNum);

		buttonNext = new JButton("");
		buttonNext.setIcon(IconStore.getStore().getIcon(IconDefaults.imlec_saga));
		GridBagConstraints gbc_btnNext = new GridBagConstraints();
		gbc_btnNext.insets = new Insets(5, 5, 5, 5);
		gbc_btnNext.gridx = 2;
		gbc_btnNext.gridy = 0;
		panelDeviceButtons.add(buttonNext, gbc_btnNext);

		JPanel panelSelection = new JPanel();
		GridBagConstraints gbc_panelSelection = new GridBagConstraints();
		gbc_panelSelection.fill = GridBagConstraints.BOTH;
		gbc_panelSelection.gridx = 1;
		gbc_panelSelection.gridy = 1;
		panel.add(panelSelection, gbc_panelSelection);
		GridBagLayout gbl_panelSelection = new GridBagLayout();
		gbl_panelSelection.columnWidths = new int[] { 0 };
		gbl_panelSelection.rowHeights = new int[] { 0, 40 };
		gbl_panelSelection.columnWeights = new double[] { 1.0 };
		gbl_panelSelection.rowWeights = new double[] { 1.0, 0.0 };
		panelSelection.setLayout(gbl_panelSelection);

		JScrollPane scrollPaneSelection = new JScrollPane();

		scrollPaneSelection.setBorder(BorderFactory.createTitledBorder("Selected Devices"));
		GridBagConstraints gbc_scrollPaneSelection = new GridBagConstraints();
		gbc_scrollPaneSelection.insets = new Insets(5, 5, 5, 5);
		gbc_scrollPaneSelection.fill = GridBagConstraints.BOTH;
		gbc_scrollPaneSelection.gridx = 0;
		gbc_scrollPaneSelection.gridy = 0;
		panelSelection.add(scrollPaneSelection, gbc_scrollPaneSelection);

		JPanel panelSelectionButtons = new JPanel();
		GridBagConstraints gbc_panelSelectionButtons = new GridBagConstraints();
		gbc_panelSelectionButtons.fill = GridBagConstraints.BOTH;
		gbc_panelSelectionButtons.gridx = 0;
		gbc_panelSelectionButtons.gridy = 1;
		panelSelection.add(panelSelectionButtons, gbc_panelSelectionButtons);
		GridBagLayout gbl_panelSelectionButtons = new GridBagLayout();
		gbl_panelSelectionButtons.columnWidths = new int[] { 0, 0 };
		gbl_panelSelectionButtons.rowHeights = new int[] { 0 };
		gbl_panelSelectionButtons.columnWeights = new double[] { 0.0, 0.0 };
		gbl_panelSelectionButtons.rowWeights = new double[] { 0.0 };
		panelSelectionButtons.setLayout(gbl_panelSelectionButtons);

		btnSelect = new JButton("Select");
		btnSelect.setPreferredSize(new Dimension(88, 25));
		btnSelect.setMaximumSize(new Dimension(88, 25));
		btnSelect.setMinimumSize(new Dimension(88, 25));
		GridBagConstraints gbc_btnSelectUserForPair = new GridBagConstraints();
		gbc_btnSelectUserForPair.insets = new Insets(5, 5, 5, 5);
		gbc_btnSelectUserForPair.gridx = 0;
		gbc_btnSelectUserForPair.gridy = 0;
		panelSelectionButtons.add(btnSelect, gbc_btnSelectUserForPair);

		btnRemoveSelection = new JButton("Remove");
		GridBagConstraints gbc_btnRemove = new GridBagConstraints();
		gbc_btnRemove.insets = new Insets(5, 5, 5, 5);
		gbc_btnRemove.gridx = 1;
		gbc_btnRemove.gridy = 0;
		panelSelectionButtons.add(btnRemoveSelection, gbc_btnRemove);

		tableDevices = new ListTable<IUser>(IUser.class, Messages::getString);
		scrollPane.setViewportView(getTableDevices());
		tableSelection = new ListTable<IUser>(IUser.class, Messages::getString);
		scrollPaneSelection.setViewportView(getTableSelection());
	}

	public JButton getBtnAddUser() {

		return btnSaveUser;
	}

	public ListTable<IUser> getTableSelection() {

		return tableSelection;
	}

	public JButton getBtnSelectForPair() {

		return btnSelect;
	}

	public JButton getBtnRemoveSelection() {

		return btnRemoveSelection;
	}

	public JButton getBtnSaveUser() {

		return btnSaveUser;
	}

	public JButton getBtnDeleteUser() {

		return btnDeleteUser;
	}

	public JButton getBtnAdminLogout() {

		return btnAdminLogout;
	}

	public ListTable<IUser> getTableDevices() {

		return tableDevices;
	}

	public JButton getBtnBack() {

		return buttonBack;
	}

	public JLabel getLblPageNum() {

		return lblPageNum;
	}

	public JButton getBtnNext() {

		return buttonNext;
	}

	public JButton getBtnPair() {
		return btnPair;
	}

	public void initialize() {

		tableDevices.removeAllRows();
		tableSelection.removeAllRows();
		lblPageNum.setText("1");
		buttonBack.setVisible(false);
		buttonNext.setVisible(true);
	}
}
