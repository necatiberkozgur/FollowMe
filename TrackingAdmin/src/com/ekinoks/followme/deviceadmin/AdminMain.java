package com.ekinoks.followme.deviceadmin;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;

import org.osgi.framework.BundleContext;

import com.ekinoks.followme.deviceadmin.adminmodel.AdminModel;
import com.ekinoks.followme.deviceadmin.adminservercommunication.AdminServerCommunicationHandler;
import com.ekinoks.followme.deviceadmin.adminview.MainPanelController;
import com.ekinoks.followme.deviceadmin.databaseops.IDatabaseOpsHandler;

public class AdminMain {

	public void activate(BundleContext bc) {

		IDatabaseOpsHandler databaseOpsHandler = IDatabaseOpsHandler.createInstance();
		AdminServerCommunicationHandler adminServerCommunicationHandler = new AdminServerCommunicationHandler();

		AdminModel adminModel = new AdminModel(databaseOpsHandler, adminServerCommunicationHandler);

		JFrame frame = new JFrame("Admin");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());

		frame.add(new MainPanelController(adminModel).getView(), BorderLayout.CENTER);

		frame.setPreferredSize(new Dimension(1200, 600));
		frame.pack();
		frame.setVisible(true);

	}

}
