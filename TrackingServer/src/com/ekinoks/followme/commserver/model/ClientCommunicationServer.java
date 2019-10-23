package com.ekinoks.followme.commserver.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import com.ekinoks.followme.commserver.serverutils.DatabaseCommunicator;
import com.ekinoks.followme.commserver.serverutils.UserCommunication;
import com.ekinoks.followme.trackingutils.communication.Serialization;
import com.ekinoks.followme.trackingutils.communication.UserLog;
import com.ekinoks.followme.trackingutils.events.Event;
import com.ekinoks.followme.trackingutils.events.EventWrapper;

public class ClientCommunicationServer implements IClientDatabaseListener, IClientCommunicationServer {

	private final AtomicBoolean running = new AtomicBoolean(false);

	private Map<String, Socket> namedSocketList = Collections.synchronizedMap(new HashMap<>());
	private Map<String, HashSet<String>> deviceToClientList = Collections.synchronizedMap(new HashMap<>());
	private Map<String, HashSet<String>> clientToDeviceList = Collections.synchronizedMap(new HashMap<>());
	private Map<String, UserLog> deviceList = Collections.synchronizedMap(new HashMap<String, UserLog>());

	private ServerDatabaseCommunicationHandler serverDatabaseCommunicationHandler = new ServerDatabaseCommunicationHandler();

	private int portNumber = 0;
	private MessageQueue receivedMessageQueue;
	private MessageQueue sentMessageQueue;

	public ClientCommunicationServer(MessageQueue sentMessageQueue, MessageQueue receivedMessageQueue) {

		this.receivedMessageQueue = receivedMessageQueue;
		this.sentMessageQueue = sentMessageQueue;
		this.sentMessageQueue.addReceivedMessageListener(this::writeOutputStream);
		this.portNumber = 8085;
	}

	public void start() {

		Thread serverThread = new Thread(this::run, "Client Communication Server Starter Thread");
		serverThread.start();
	}

	public void run() {

		if (running.compareAndSet(false, true) == true) {

			try {

				Thread communicationThread = new Thread(this::startCommuniCation,
						"Client Communication Server Communication Starter Thread");
				communicationThread.start();

			} catch (RuntimeException e) {

				throw new RuntimeException();
			}
		}
	}

	public MessageQueue getSentQueue() {

		return this.sentMessageQueue;
	}

	public MessageQueue getReceivedQueue() {

		return this.receivedMessageQueue;
	}

	@Override
	public void onGetPairUpdates() {

		fetchPairedUsers();

		for (String name : namedSocketList.keySet()) {

			dispatchAssignedDevices(name);
		}
	}

	@Override
	public void onSubmitDevice(UserLog log) {

		if (deviceList.containsKey(log.getUserID()) == false) {

			deviceList.put(log.getUserID(), log);

		} else {

			deviceList.replace(log.getUserID(), log);
		}

		invokeClientForDeviceLog(log);
	}

	@Override
	public void onGetClientUpdates(String username, int state) {

		if (state == 100) {

			banClient(username);
			removeNamedSocket(username);

		}
	}

	private void startCommuniCation() {

		try (ServerSocket serverSocket = new ServerSocket(portNumber)) {

			fetchPairedUsers();

			while (true) {

				Socket socket = serverSocket.accept();

				new Thread(() -> {

					readInputStream(socket);
				}).start();

			}

		} catch (IOException e1) {

			System.out.println("Server socket error: " + e1.getMessage());
			e1.printStackTrace();
		}
	}

	private void writeOutputStream(Object message) {

		OutputStream outputStream;
		String senderName = "";

		if (message instanceof Event) {

			senderName = new EventWrapper((Event) message).getDeviceID();

			System.out.println(senderName);

			Set<String> assignedClients = deviceToClientList.get(senderName);

			if (assignedClients != null) {

				for (String assignedClient : assignedClients) {

					if (namedSocketList.containsKey(assignedClient) == false) {

						continue;
					}

					Socket socket = namedSocketList.get(assignedClient);

					if (socket.isClosed() == true) {

						continue;
					}

					try {

						outputStream = socket.getOutputStream();

						if (message != null) {

							outputStream.write(Serialization.serializeMessage(message));

						} else {

							System.out.println("No messages to show");
						}

					} catch (Exception e) {

						// TODO NECO log the exception
						e.printStackTrace();
					}

				}

			}
		}

	}

	private void readInputStream(Socket socket) {

		byte[] buffer = new byte[1024];

		InputStream inputStream;

		String name = "";

		try {

			inputStream = socket.getInputStream();

			while (running.get() == true && inputStream.read(buffer) != -1) {

				ByteBuffer bufferedMessage = ByteBuffer.wrap(buffer);
				char messageType = bufferedMessage.getChar();

				if (messageType == '$') {

					receivedMessageQueue.enqueue(Serialization.deserializeMessage(buffer));

				} else if (messageType == '#') {

					name = Serialization.deserializeStringMessage(buffer);
					System.out.println(name);
					addNamedSocket(name, socket);
					dispatchAssignedDevices(name);

				} else if (messageType == '?') {

					int msgCode = bufferedMessage.getInt();
					List<Object> queryResult;
					List<Object> deserializedQuery;

					deserializedQuery = Serialization.deserializeDatabaseRequest(buffer);

					queryResult = DatabaseCommunicator.processDatabaseOperation(msgCode, deserializedQuery);

					UserCommunication.sendQueryMessage(socket, queryResult);
				}
			}

			socket.close();

		} catch (IOException e) {

			e.printStackTrace();

		} finally {

			removeNamedSocket(name);
		}
	}

	private void fetchPairedUsers() {

		deviceToClientList.clear();
		clientToDeviceList.clear();

		ArrayList<String[]> fetchedPairs = new ArrayList<String[]>();

		fetchedPairs = serverDatabaseCommunicationHandler.getPairedUsers();

		for (String[] pair : fetchedPairs) {

			if (deviceToClientList.containsKey(pair[0]) == true) {

				deviceToClientList.get(pair[0]).add(pair[1]);

			} else {

				deviceToClientList.put(pair[0], new HashSet<String>());
				deviceToClientList.get(pair[0]).add(pair[1]);
			}

			if (clientToDeviceList.containsKey(pair[1]) == true) {

				clientToDeviceList.get(pair[1]).add(pair[0]);

			} else {

				clientToDeviceList.put(pair[1], new HashSet<String>());
				clientToDeviceList.get(pair[1]).add(pair[0]);
			}
		}
	}

	private void addNamedSocket(String username, Socket socket) {

		if (namedSocketList.containsKey(username) == false) {

			namedSocketList.put(username, socket);

		}
	}

	private void removeNamedSocket(String username) {

		if (namedSocketList.containsKey(username) == true) {

			namedSocketList.remove(username);

		}
	}

	private void invokeClientForDeviceLog(UserLog log) {

		if (deviceToClientList.containsKey(log.getUserID()) == true) {

			for (String client : deviceToClientList.get(log.getUserID())) {

				if (namedSocketList.containsKey(client)) {

					try {

						Socket socket = namedSocketList.get(client);
						OutputStream outputStream = socket.getOutputStream();
						outputStream.write(Serialization.serializeMessage(log));

					} catch (IOException e) {

						e.printStackTrace();
					}
				}
			}
		}
	}

	private void dispatchAssignedDevices(String username) {

		if (namedSocketList.containsKey(username) == true) {

			Socket socket = namedSocketList.get(username);

			OutputStream outputStream = null;

			if (socket != null) {

				try {

					outputStream = socket.getOutputStream();

				} catch (IOException e) {

					e.printStackTrace();
				}

				if (clientToDeviceList.get(username) != null) {

					for (String device : clientToDeviceList.get(username)) {

						if (outputStream != null) {

							try {

								outputStream.write(Serialization.serializeMessage(deviceList.get(device)));

							} catch (IOException e) {

								e.printStackTrace();
							}
						}
					}
				}
			}
		}
	}

	private void banClient(String username) {

		if (namedSocketList.containsKey(username) == true) {

			ByteBuffer buffer = ByteBuffer.allocate(1024);
			buffer.putChar('-');

			Socket socket = namedSocketList.get(username);

			try {

				OutputStream outputStream = socket.getOutputStream();
				outputStream.write(buffer.array());

			} catch (IOException e) {

				e.printStackTrace();
			}

		}
	}

}
