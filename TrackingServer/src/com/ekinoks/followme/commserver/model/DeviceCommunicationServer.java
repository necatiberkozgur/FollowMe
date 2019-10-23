package com.ekinoks.followme.commserver.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import com.ekinoks.followme.commserver.serverutils.DatabaseCommunicator;
import com.ekinoks.followme.commserver.serverutils.UserCommunication;
import com.ekinoks.followme.trackingutils.communication.ConnectionStatus;
import com.ekinoks.followme.trackingutils.communication.Serialization;
import com.ekinoks.followme.trackingutils.communication.UserLog;
import com.ekinoks.followme.trackingutils.users.DeviceUser;
import com.ekinoks.followme.trackingutils.users.UserType;

public class DeviceCommunicationServer implements IDeviceDatabaseListener {

	private final AtomicBoolean running = new AtomicBoolean(false);

	private Set<Socket> socketList = Collections.synchronizedSet(new HashSet<Socket>());

	private Set<IClientCommunicationServer> deviceServerListeners = new HashSet<IClientCommunicationServer>();

	private Map<String, Socket> namedSocketList = Collections.synchronizedMap(new HashMap<String, Socket>());

	private Map<String, UserLog> deviceList = Collections.synchronizedMap(new HashMap<String, UserLog>());

	private int portNumber = 0;

	private MessageQueue receivedMessageQueue;
	private MessageQueue sentMessageQueue;
	private ExecutorService executorService = Executors.newFixedThreadPool(10);

	private ServerDatabaseCommunicationHandler serverDatabaseCommunicationHandler = new ServerDatabaseCommunicationHandler();

	public DeviceCommunicationServer(MessageQueue sentMessageQueue, MessageQueue receivedMessageQueue) {

		this.receivedMessageQueue = receivedMessageQueue;
		this.sentMessageQueue = sentMessageQueue;
		this.sentMessageQueue.addReceivedMessageListener(this::writeOutputStream);
		this.portNumber = 8086;
	}

	public void run() {

		if (running.compareAndSet(false, true) == true) {

			try {

				Thread communicationThread = new Thread(this::startCommuniCation);
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

	public void addDeviceListener(IClientCommunicationServer listener) {

		if (deviceServerListeners.contains(listener) == false) {

			deviceServerListeners.add(listener);

		}
	}

	public void removeDeviceListener(IClientCommunicationServer listener) {

		if (deviceServerListeners.contains(listener) == true) {

			deviceServerListeners.remove(listener);

		}
	}

	@Override
	public void onGetDeviceUpdates(String username, int state) {

		if (state == 101) {

			if (deviceList.containsKey(username) == false) {

				deviceList.put(username, new UserLog(username, UserType.Device, ConnectionStatus.OFFLINE, ""));
			}

		} else {

			if (deviceList.containsKey(username) == true) {

				deviceList.remove(username);
				banDevice(username);
				removeNamedSocket(username);

			}
		}
	}

	private void startCommuniCation() {

		System.out.println("Comm Server Starting...");

		initializeDeviceList();

		try (ServerSocket serverSocket = new ServerSocket(portNumber)) {

			while (true) {

				Socket socket = serverSocket.accept();

				addSocket(socket);

				executorService.submit(() -> {

					readInputStream(socket);
				});
			}

		} catch (IOException e1) {

			System.out.println("Server socket error: " + e1.getMessage());
			e1.printStackTrace();
		}
	}

	/*****************/
	/* IO OPERATIONS */
	/*****************/

	private void writeOutputStream(Object message) {

		OutputStream outputStream;

		for (Socket socket : socketList) {

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

	private void readInputStream(Socket socket) {

		byte[] buffer = new byte[1024];
		InputStream inputStream;
		String connectedDeviceName = null;

		try {

			inputStream = socket.getInputStream();

			while (inputStream.read(buffer) != -1) {

				ByteBuffer bufferedMessage = ByteBuffer.wrap(buffer);
				char messageType = bufferedMessage.getChar();

				System.out.println("Message from : " + socket.getRemoteSocketAddress());

				if (messageType == '$') {

					receivedMessageQueue.enqueue(Serialization.deserializeMessage(buffer));

				} else if (messageType == '#') {

					connectedDeviceName = Serialization.deserializeStringMessage(buffer);
					addNamedSocket(connectedDeviceName, socket);
					UserLog log = new UserLog(connectedDeviceName, UserType.Device, ConnectionStatus.ONLINE);
					setDeviceStatus(log);

				} else if (messageType == '?') {

					int msgCode;
					List<Object> queryResult;
					List<Object> deserializedQuery;

					msgCode = bufferedMessage.getInt();

					deserializedQuery = Serialization.deserializeDatabaseRequest(buffer);
					queryResult = DatabaseCommunicator.processDatabaseOperation(msgCode, deserializedQuery);
					UserCommunication.sendQueryMessage(socket, queryResult);
				}
			}

			System.out.println("Socket is closed.");

			socket.close();
			removeNamedSocket(connectedDeviceName);
			UserLog log = new UserLog(connectedDeviceName, UserType.Device, ConnectionStatus.OFFLINE);
			setDeviceStatus(log);

		} catch (IOException e) {

			throw new RuntimeException();

		} finally {

			removeSocket(socket);
		}
	}

	// SOCKET OPERATIONS

	private void addSocket(Socket socket) {

		if (socketList.contains(socket) == false) {

			System.out.println("new device connected.");
			socketList.add(socket);
		}
	}

	private void removeSocket(Socket socket) {

		if (socketList.contains(socket) == true) {

			System.out.println("Device removed");
			socketList.remove(socket);
		}
	}

	private void initializeDeviceList() {

		List<DeviceUser> allDevices = serverDatabaseCommunicationHandler.getDevices();

		for (DeviceUser device : allDevices) {

			String id = device.getUserID();
			UserLog emptyLog = new UserLog(id, UserType.Device, ConnectionStatus.OFFLINE, "");
			deviceList.put(id, emptyLog);
			notifyClientServerForLog(emptyLog);
			System.out.println(device.getUserID());
		}
	}

	private void notifyClientServerForLog(UserLog log) {

		for (IClientCommunicationServer listener : deviceServerListeners) {

			listener.onSubmitDevice(log);
		}
	}

	private void setDeviceStatus(UserLog log) {

		if (deviceList.containsKey(log.getUserID()) == true) {

			deviceList.put(log.getUserID(), log);

		} else {

			deviceList.replace(log.getUserID(), log);

		}

		notifyClientServerForLog(log);
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

	private void banDevice(String username) {

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
