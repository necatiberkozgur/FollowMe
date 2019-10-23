package com.ekinoks.followme.commserver.model;

public class MessageDispatcher {

	private ClientCommunicationServer clientCommunicationServer;

	private DeviceCommunicationServer deviceCommunicationServer;

	public MessageDispatcher(ClientCommunicationServer clientCommunicationServer,
			DeviceCommunicationServer deviceCommunicationServer) {

		this.clientCommunicationServer = clientCommunicationServer;
		this.deviceCommunicationServer = deviceCommunicationServer;
	}

	public void onSendDeviceMessage(Object message) {

		System.out.println("message forwarded from device to client");
		clientCommunicationServer.getSentQueue().enqueue(message);
	}

	public void onSendClientMessage(Object message) {

		System.out.println("messaage forwarded from client to device");
		deviceCommunicationServer.getSentQueue().enqueue(message);
	}
}
