package com.ekinoks.followme.commclient.view;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;

import com.ekinoks.followme.commclient.model.ClientModel;
import com.ekinoks.followme.trackingutils.events.EngineEvent;
import com.ekinoks.followme.trackingutils.events.EventWrapper;
import com.ekinoks.followme.trackingutils.events.LocationEvent;
import com.ekinoks.followme.trackingutils.events.SeatBeltEvent;

public class ClientViewController {

	private ClientView view;
	private ClientModel clientModel;
	private Set<IPanelEventListener> logoutListeners = new HashSet<IPanelEventListener>();
	private EventListCellRenderer cellRenderer = new EventListCellRenderer();

	public ClientViewController(ClientModel clientModel) {

		this.clientModel = clientModel;
		view = new ClientView();

		view.getBtnSignOut().addActionListener(this::onSignOut);
		view.getBtnSignOut().setVisible(false);

		view.getBtnClear().addActionListener(this::onClearMessages);
		view.getBtnClear().setVisible(false);

		view.getBtnSignOut().setVisible(true);
		view.getEventList().setSelectionBackground(Color.CYAN);
		view.getEventList().setSelectionForeground(Color.BLACK);
		view.getEventList().setCellRenderer(cellRenderer);
		view.getEventList().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		view.getEventList().setVisibleRowCount(5);

		view.getEventList().getSelectionModel().addListSelectionListener(e -> {

			System.out.println(e);
		});
	}

	public ClientView getView() {

		return view;
	}

	private void onSignOut(ActionEvent e) {

		clientModel.stopComm();

		if (clientModel.getCommunicator().getConnectionStatus() == false) {

			invokeLogoutListeners();

		} else {

			failLogoutPopup();
		}
	}

	private void invokeLogoutListeners() {

		for (IPanelEventListener iPanelEventListener : logoutListeners) {

			iPanelEventListener.onLogout();
		}
	}

	private void failLogoutPopup() {

		JFrame frame = new JFrame();

		JOptionPane.showMessageDialog(frame, "Failed to logout, try again later!", "Error", JOptionPane.ERROR_MESSAGE);
	}

	public void addLogoutListener(IPanelEventListener panelEventListener) {

		if (logoutListeners.contains(panelEventListener) == false) {

			logoutListeners.add(panelEventListener);
		}
	}

	public void removeLogoutListener(IPanelEventListener panelEventListener) {

		if (logoutListeners.contains(panelEventListener) == true) {

			logoutListeners.remove(panelEventListener);
		}
	}

	public void onClearMessages(ActionEvent e) {

		view.getListModel().clear();
		view.getEventList().removeAll();
		view.getBtnClear().setVisible(false);

		view.getEventList().revalidate();
		view.getEventList().repaint();
	}

	public void onPromptErrorMessage(Object message) {

		if (clientModel.getCommunicator().getConnectionStatus() == false) {

			view.getBtnSignOut().setVisible(false);
		}

		JFrame frame = new JFrame();

		JOptionPane.showMessageDialog(frame, (String) message, "Info", JOptionPane.INFORMATION_MESSAGE);
	}

	public void onReadMessage(Object message) {

		if (message instanceof EngineEvent) {

			EventWrapper event = new EventWrapper((EngineEvent) message);
			view.getListModel().addElement(event);

		} else if (message instanceof LocationEvent) {

			EventWrapper event = new EventWrapper((LocationEvent) message);
			view.getListModel().addElement(event);

		} else if (message instanceof SeatBeltEvent) {

			EventWrapper event = new EventWrapper((SeatBeltEvent) message);
			view.getListModel().addElement(event);

		}
		if (view.getListModel().isEmpty() == false) {

			view.getBtnClear().setVisible(true);
		}

		view.getEventList().revalidate();
		view.getEventList().repaint();
	}

	public void printEventSpecificFields(EventWrapper tableEntry) {

		JFrame frame = new JFrame();

		JOptionPane.showMessageDialog(frame, tableEntry.eventSpecificFields(), tableEntry.getEventType() + " Details",
				JOptionPane.INFORMATION_MESSAGE);

	}

}
