package com.ekinoks.followme.commserver;

import org.osgi.framework.BundleContext;

import com.ekinoks.followme.commserver.model.AdminCommunicationServer;
import com.ekinoks.followme.commserver.model.ClientCommunicationServer;
import com.ekinoks.followme.commserver.model.DeviceCommunicationServer;
import com.ekinoks.followme.commserver.model.MessageDispatcher;
import com.ekinoks.followme.commserver.model.MessageQueue;

public class CommServerMain {

	public void activate(BundleContext bc) {

		MessageQueue clientSentQueue = new MessageQueue();
		MessageQueue clientReceivedQueue = new MessageQueue();
		MessageQueue deviceSentQueue = new MessageQueue();
		MessageQueue deviceReceivedQueue = new MessageQueue();

		ClientCommunicationServer clientCommunicationServer = new ClientCommunicationServer(clientSentQueue,
				clientReceivedQueue);

		DeviceCommunicationServer deviceCommunicationServer = new DeviceCommunicationServer(deviceSentQueue,
				deviceReceivedQueue);

		AdminCommunicationServer adminCommunicationServer = new AdminCommunicationServer();

		MessageDispatcher messageDispatcher = new MessageDispatcher(clientCommunicationServer,
				deviceCommunicationServer);

		clientCommunicationServer.getReceivedQueue().addReceivedMessageListener(messageDispatcher::onSendClientMessage);
		deviceCommunicationServer.getReceivedQueue().addReceivedMessageListener(messageDispatcher::onSendDeviceMessage);
		deviceCommunicationServer.addDeviceListener(clientCommunicationServer);
		adminCommunicationServer.addDatabaseUpdateListener(clientCommunicationServer);
		adminCommunicationServer.addDeviceUpdateListener(deviceCommunicationServer);

		Thread clientThread = new Thread(clientCommunicationServer::run);
		clientThread.start();

		Thread deviceThread = new Thread(deviceCommunicationServer::run);
		deviceThread.start();

		Thread adminTherad = new Thread(adminCommunicationServer::run);
		adminTherad.start();

	}
}