package com.ekinoks.followme.commclient.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.ekinoks.followme.trackingutils.events.EventWrapper;

@SuppressWarnings("serial")
public class ClientView extends JPanel {

	private JButton btnSignOut;
	private JButton btnClear;
	private JScrollPane scrollPane;
	private JList<EventWrapper> eventList;
	private DefaultListModel<EventWrapper> listModel;

	public ClientView() {

		listModel = new DefaultListModel<EventWrapper>();
		eventList = new JList<EventWrapper>(listModel);

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 109, 49, 82, 131, 0 };
		gridBagLayout.rowHeights = new int[] { 25, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		btnClear = new JButton("Clear Messages");

		GridBagConstraints gbc_btnClear = new GridBagConstraints();
		gbc_btnClear.anchor = GridBagConstraints.SOUTH;
		gbc_btnClear.gridx = 0;
		gbc_btnClear.gridy = 0;
		gbc_btnClear.insets = new Insets(0, 0, 5, 5);
		add(btnClear, gbc_btnClear);

		btnSignOut = new JButton("Sign Out");
		GridBagConstraints gbc_btnSignOut = new GridBagConstraints();
		gbc_btnSignOut.anchor = GridBagConstraints.SOUTH;
		gbc_btnSignOut.insets = new Insets(0, 0, 5, 0);
		gbc_btnSignOut.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnSignOut.gridx = 3;
		gbc_btnSignOut.gridy = 0;
		add(btnSignOut, gbc_btnSignOut);

		scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridwidth = 4;
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 3;
		add(scrollPane, gbc_scrollPane);

		scrollPane.setViewportView(eventList);
	}

	public DefaultListModel<EventWrapper> getListModel() {
		return listModel;
	}

	public JButton getBtnSignOut() {

		return this.btnSignOut;
	}

	public JButton getBtnClear() {

		return btnClear;
	}

	public JList<EventWrapper> getEventList() {

		return eventList;
	}

}
