package com.ekinoks.followme.commclient.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.ekinoks.followme.trackingutils.communication.UserLog;
import com.ekinoks.ui.components.listtable.ListTable;

@SuppressWarnings("serial")
public class UserLogPanel extends JPanel {

	private ListTable<UserLog> logTable;
	private JScrollPane scrollPane;

	public UserLogPanel() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		scrollPane = new JScrollPane();
		scrollPane.setMaximumSize(new Dimension(170, 32767));
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		add(scrollPane, gbc_scrollPane);

		logTable = new ListTable<UserLog>(UserLog.class, s -> s.substring(s.lastIndexOf('.') + 1));
		scrollPane.setViewportView(logTable);
	}

	public ListTable<UserLog> getLogTable() {

		return logTable;
	}

	public JScrollPane getScrollPane() {

		return scrollPane;
	}
}
