package com.ekinoks.followme.commclient.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

@SuppressWarnings("serial")
public class EventComponent extends JPanel {

	JLabel lblType;
	JLabel lblDeviceName;
	JLabel lblEventType;
	JLabel lblEventTime;
	private JSeparator separator;

	public EventComponent() {
		setMinimumSize(new Dimension(270, 170));
		setMaximumSize(new Dimension(270, 170));

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 37, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };

		setLayout(gridBagLayout);

		separator = new JSeparator();
		GridBagConstraints gbc_separator = new GridBagConstraints();
		gbc_separator.insets = new Insets(0, 0, 5, 5);
		gbc_separator.gridx = 0;
		gbc_separator.gridy = 0;
		add(separator, gbc_separator);

		lblType = new JLabel();
		GridBagConstraints gbc_lblType = new GridBagConstraints();
		gbc_lblType.anchor = GridBagConstraints.SOUTHWEST;
		gbc_lblType.insets = new Insets(0, 0, 5, 5);
		gbc_lblType.gridx = 1;
		gbc_lblType.gridy = 1;
		add(lblType, gbc_lblType);

		lblDeviceName = new JLabel("Device Name");
		GridBagConstraints gbc_lblDeviceName = new GridBagConstraints();
		gbc_lblDeviceName.anchor = GridBagConstraints.SOUTHEAST;
		gbc_lblDeviceName.insets = new Insets(0, 0, 5, 0);
		gbc_lblDeviceName.gridx = 2;
		gbc_lblDeviceName.gridy = 1;
		add(lblDeviceName, gbc_lblDeviceName);

		lblEventType = new JLabel("Event Type");
		GridBagConstraints gbc_lblEventType = new GridBagConstraints();
		gbc_lblEventType.anchor = GridBagConstraints.EAST;
		gbc_lblEventType.insets = new Insets(0, 0, 5, 0);
		gbc_lblEventType.gridx = 2;
		gbc_lblEventType.gridy = 2;
		add(lblEventType, gbc_lblEventType);

		lblEventTime = new JLabel("Event Time");
		GridBagConstraints gbc_lblEventTime = new GridBagConstraints();
		gbc_lblEventTime.anchor = GridBagConstraints.EAST;
		gbc_lblEventTime.insets = new Insets(0, 0, 5, 0);
		gbc_lblEventTime.gridx = 2;
		gbc_lblEventTime.gridy = 3;
		add(lblEventTime, gbc_lblEventTime);

	}

	public void setLblName(String username) {

		this.lblDeviceName.setText(username);
	}

	public void setLblEventType(String eventType) {

		this.lblEventType.setText(eventType);
	}

	public void setLblEventTime(String timeStamp) {

		this.lblEventTime.setText(timeStamp);
	}

	public void setIcon(Icon icon) {

		this.lblType.setIcon(icon);
	}

}
