package com.ekinoks.followme.deviceadmin.adminview;

import java.awt.event.ActionEvent;

import com.ekinoks.followme.deviceadmin.adminmodel.AdminModel;
import com.ekinoks.followme.trackingutils.users.IUser;
import com.ekinoks.followme.trackingutils.users.UserType;

public class ShowPairsController implements IPairPanel {

	private ShowPairsPanel showPairsPanel;
	private String userID;
	private IUser user;
	private AdminModel adminModel;
	private int totalPages;

	public ShowPairsController(AdminModel adminModel, IUser user) {

		this.adminModel = adminModel;
		this.user = user;
		this.userID = user.getUserID();
		showPairsPanel = new ShowPairsPanel();
		showPairsPanel.getBtnNext().addActionListener(this::onNext);
		showPairsPanel.getBtnPrev().addActionListener(this::onBack);
		showPairsPanel.getBtnPrev().setVisible(false);
		showPairsPanel.getBtnUnpair().addActionListener(this::onUnpair);
		showPairsPanel.getListPairs().setFilterable(true);
		showPairsPanel.getListPairs().setSortable(true);
		showPairsPanel.getListPairs().setContextMenuVisible(true);

	}

	public ShowPairsPanel getView() {

		return showPairsPanel;
	}

	public String getUserID() {

		return userID;
	}

	@Override
	public void onAddPairToTable(IUser u, int totalEntries) {

		this.totalPages = totalEntries / 5;

		showPairsPanel.getListPairs().addRow(u);
		if (totalEntries <= 5) {

			showPairsPanel.getBtnNext().setVisible(false);
		}
	}

	@Override
	public void onRemovePairFromTable(String username) {

	}

	public void onNext(ActionEvent e) {

		int currentPage;
		currentPage = Integer.parseInt(showPairsPanel.getLblPageNubmer().getText());
		showPairsPanel.getLblPageNubmer().setText((new Integer(currentPage + 1).toString()));
		showPairsPanel.getListPairs().removeAllRows();
		showPairsPanel.getBtnPrev().setVisible(true);
		adminModel.requestPairs(user, currentPage);

		if (currentPage + 1 >= totalPages) {

			showPairsPanel.getBtnNext().setVisible(false);
		}
	}

	public void onBack(ActionEvent e) {

		int currentPage;
		currentPage = Integer.parseInt(showPairsPanel.getLblPageNubmer().getText());
		showPairsPanel.getLblPageNubmer().setText((new Integer(currentPage - 1).toString()));
		showPairsPanel.getListPairs().removeAllRows();
		showPairsPanel.getBtnPrev().setVisible(true);
		adminModel.requestPairs(user, currentPage - 2);

		if (currentPage + 1 >= totalPages) {

			showPairsPanel.getBtnNext().setVisible(false);

		} else {

			showPairsPanel.getBtnNext().setVisible(false);
		}

		if (currentPage - 1 == 1) {

			showPairsPanel.getBtnPrev().setVisible(false);
		}
	}

	public void onUnpair(ActionEvent e) {

		if (UserType.Client.equals(user.getUserType())) {

			for (IUser userSelected : showPairsPanel.getListPairs().getSelectedValues()) {

				adminModel.deletePair(user.getUserID(), userSelected.getUserID());
				showPairsPanel.getListPairs().getSelectedValues().remove(userSelected);
			}
		}
	}
}
