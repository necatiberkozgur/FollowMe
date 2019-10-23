package com.ekinoks.followme.deviceadmin.adminview;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.ekinoks.followme.trackingutils.users.IUser;
import com.ekinoks.ui.components.listtable.ListTable;

@SuppressWarnings("serial")
public class ShowPairsPanel extends JPanel {

	private JScrollPane scrollPanePaired;
	private ListTable<IUser> listPairs;
	private JButton btnNext;
	private JLabel label;
	private JButton btnPrev;
	private JButton btnUnpair;

	public ShowPairsPanel() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 182, 105, 168, 0 };
		gridBagLayout.rowHeights = new int[] { 170, 0, 50, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		scrollPanePaired = new JScrollPane();
		getScrollPanePaired().setBorder(BorderFactory.createTitledBorder("Paired users"));
		GridBagConstraints gbc_scrollPanePaired = new GridBagConstraints();
		gbc_scrollPanePaired.gridwidth = 3;
		gbc_scrollPanePaired.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPanePaired.fill = GridBagConstraints.BOTH;
		gbc_scrollPanePaired.gridx = 0;
		gbc_scrollPanePaired.gridy = 0;
		add(getScrollPanePaired(), gbc_scrollPanePaired);

		setBtnPrev(new JButton("Prev"));
		GridBagConstraints gbc_btnPrev = new GridBagConstraints();
		gbc_btnPrev.insets = new Insets(0, 0, 5, 5);
		gbc_btnPrev.gridx = 0;
		gbc_btnPrev.gridy = 1;
		add(getBtnPrev(), gbc_btnPrev);

		label = new JLabel("1");
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.insets = new Insets(0, 0, 5, 5);
		gbc_label.gridx = 1;
		gbc_label.gridy = 1;
		add(label, gbc_label);

		btnNext = new JButton("Next");
		GridBagConstraints gbc_btnNext = new GridBagConstraints();
		gbc_btnNext.insets = new Insets(0, 0, 5, 0);
		gbc_btnNext.gridx = 2;
		gbc_btnNext.gridy = 1;
		add(getBtnNext(), gbc_btnNext);

		btnUnpair = new JButton("Unpair");
		GridBagConstraints gbc_btnUnpair = new GridBagConstraints();
		gbc_btnUnpair.insets = new Insets(0, 0, 0, 5);
		gbc_btnUnpair.gridx = 1;
		gbc_btnUnpair.gridy = 2;
		add(getBtnUnpair(), gbc_btnUnpair);

		listPairs = new ListTable<>(IUser.class, Messages::getString);
		getScrollPanePaired().setViewportView(listPairs);

	}

	public ListTable<IUser> getListPairs() {

		return listPairs;
	}

	public JScrollPane getScrollPanePaired() {

		return scrollPanePaired;
	}

	public JButton getBtnNext() {

		return btnNext;
	}

	public JButton getBtnPrev() {

		return btnPrev;
	}

	public JLabel getLblPageNubmer() {

		return label;

	}

	public void setBtnPrev(JButton btnPrev) {

		this.btnPrev = btnPrev;
	}

	public JButton getBtnUnpair() {

		return btnUnpair;
	}

}
