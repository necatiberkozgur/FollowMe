package com.ekinoks.followme.commclient;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;

import org.osgi.framework.BundleContext;

import com.ekinoks.followme.commclient.model.ClientModel;
import com.ekinoks.followme.commclient.view.MainPanelController;

public class ClientMain {

	public void activate(BundleContext bc) {

		ClientModel model = new ClientModel();

		MainPanelController mainView = new MainPanelController(model);

		model.getCommunicator().addReceivedMessageListener(mainView.getClientViewController()::onReadMessage);
		model.getCommunicator().addErrorListener(mainView.getClientViewController()::onPromptErrorMessage);

		JFrame frame = new JFrame("Client");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.add(mainView.getView(), BorderLayout.NORTH);
		frame.setPreferredSize(new Dimension(1100, 400));
		frame.pack();
		frame.setVisible(true);
	}
}
