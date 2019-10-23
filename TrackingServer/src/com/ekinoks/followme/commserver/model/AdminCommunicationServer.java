package com.ekinoks.followme.commserver.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import com.ekinoks.followme.commserver.serverutils.DatabaseCommunicator;
import com.ekinoks.followme.commserver.serverutils.UserCommunication;
import com.ekinoks.followme.trackingutils.communication.Serialization;
import com.ekinoks.followme.trackingutils.databaseops.QueryExitStatus;
import com.ekinoks.followme.trackingutils.users.ClientUser;
import com.ekinoks.followme.trackingutils.users.DeviceUser;
import com.ekinoks.followme.trackingutils.users.User;
import com.ekinoks.followme.trackingutils.users.UserType;

public class AdminCommunicationServer {

	private final AtomicBoolean running = new AtomicBoolean(false);

	private int portNumber = 0;
	private ExecutorService executorService = Executors.newFixedThreadPool(10);

	private Set<IClientDatabaseListener> clientDatabaseListeners = new HashSet<IClientDatabaseListener>();
	private Set<IDeviceDatabaseListener> deviceDatabaseListeners = new HashSet<IDeviceDatabaseListener>();

	private ServerDatabaseCommunicationHandler serverDatabaseCommunicationHandler = new ServerDatabaseCommunicationHandler();
	private List<DeviceUser> allDevices = new ArrayList<DeviceUser>();
	private List<ClientUser> allClients = new ArrayList<ClientUser>();
	private HashMap<User, ArrayList<User>> deviceToClient = new HashMap<>();
	private HashMap<User, ArrayList<User>> clientToDevice = new HashMap<>();

	private HashMap<String, ClientUser> namedClients = new HashMap<String, ClientUser>();
	private HashMap<String, DeviceUser> namedDevices = new HashMap<String, DeviceUser>();

	public AdminCommunicationServer() {

		this.portNumber = 8087;
	}

	public void run() {

		if (running.compareAndSet(false, true) == true) {

			try {

				initializeDevices();
				initializePairs();

				System.out.println(namedDevices.size());
				System.out.println(namedClients.size());
				System.out.println(deviceToClient);
				System.out.println(clientToDevice);

				Thread communicationThread = new Thread(this::startCommuniCation);
				communicationThread.start();

			} catch (RuntimeException e) {

				throw new RuntimeException();
			}
		}
	}

	public void addDatabaseUpdateListener(IClientDatabaseListener listener) {

		if (clientDatabaseListeners.contains(listener) == false) {

			clientDatabaseListeners.add(listener);
		}
	}

	public void removeDatabaseUpdateListener(IClientDatabaseListener listener) {

		if (clientDatabaseListeners.contains(listener) == true) {

			clientDatabaseListeners.remove(listener);
		}
	}

	public void addDeviceUpdateListener(IDeviceDatabaseListener listener) {

		if (deviceDatabaseListeners.contains(listener) == false) {

			deviceDatabaseListeners.add(listener);
		}
	}

	public void removeDeviceUpdateListener(IDeviceDatabaseListener listener) {

		if (deviceDatabaseListeners.contains(listener) == true) {

			deviceDatabaseListeners.remove(listener);
		}
	}

	private void startCommuniCation() {

		try (ServerSocket serverSocket = new ServerSocket(portNumber)) {

			while (true) {

				Socket socket = serverSocket.accept();

				executorService.submit(() -> {

					readInputStream(socket);
				});
			}

		} catch (IOException e1) {

			System.out.println("Server socket error: " + e1.getMessage());
			e1.printStackTrace();
		}
	}

	private void readInputStream(Socket socket) {

		byte[] buffer = new byte[1024];
		InputStream inputStream;

		try {

			inputStream = socket.getInputStream();
			char messageType;

			while (inputStream.read(buffer) != -1) {

				ByteBuffer bufferedMessage = ByteBuffer.wrap(buffer);
				messageType = bufferedMessage.getChar();

				if (messageType == '?') {

					int msgCode = bufferedMessage.getInt();

					List<Object> queryResult;
					List<Object> deserializedQuery;

					deserializedQuery = Serialization.deserializeDatabaseRequest(buffer);

					queryResult = DatabaseCommunicator.processDatabaseOperation(msgCode, deserializedQuery);

					if (QueryExitStatus.PAIR_ADDED.equals(queryResult.get(0))
							|| QueryExitStatus.PAIR_REMOVED.equals(queryResult.get(0))
							|| QueryExitStatus.USERS_PAIRS_REMOVED.equals(queryResult.get(0))) {

						if (QueryExitStatus.PAIR_ADDED.equals(queryResult.get(0))) {

							addPairToLists((String) queryResult.get(1), (String) queryResult.get(2));

						} else if (QueryExitStatus.PAIR_REMOVED.equals(queryResult.get(0))) {

							removePairFromLists((String) queryResult.get(1), (String) queryResult.get(2));

						} else if (QueryExitStatus.USERS_PAIRS_REMOVED.equals(queryResult.get(0))) {

						}
						invokePairUpdateListeners();

					} else if (QueryExitStatus.CLIENT_ADDED.equals(queryResult.get(0))) {

						String username = ((User) queryResult.get(1)).getUserID();
						allClients.add((ClientUser) queryResult.get(1));
						namedClients.put(username, (ClientUser) queryResult.get(1));
						invokeClientUpdateListeners(username, 101);

					} else if (QueryExitStatus.CLIENT_REMOVED.equals(queryResult.get(0))) {

						String username = (String) queryResult.get(1);
						removeUserFromList(username, UserType.Client);
						clientToDevice.remove((namedClients).get(username));
						namedClients.remove(username);
						invokeClientUpdateListeners(username, 100);
						serverDatabaseCommunicationHandler.deleteClientsAllPair(username);

					} else if (QueryExitStatus.DEVICE_ADDED.equals(queryResult.get(0))) {

						String username = ((User) queryResult.get(1)).getUserID();
						allDevices.add((DeviceUser) queryResult.get(1));
						namedDevices.put(username, (DeviceUser) queryResult.get(1));
						invokeDeviceUpdateListeners(username, 101);

					} else if (QueryExitStatus.DEVICE_REMOVED.equals(queryResult.get(0))) {

						String username = (String) queryResult.get(1);
						removeUserFromList(username, UserType.Device);
						deviceToClient.remove((namedDevices.get(username)));
						namedDevices.remove(username);
						invokeDeviceUpdateListeners(username, 100);
						serverDatabaseCommunicationHandler.deleteDevicesAllPair(username);
					}

					UserCommunication.sendQueryMessage(socket, queryResult);

				} else if (messageType == '#') {

					sendUserCount(socket);
					dispatchUsers(socket, 0, UserType.Client);
					dispatchUsers(socket, 0, UserType.Device);

				} else if (messageType == '=' || messageType == '<') {

					int currentPackage = bufferedMessage.getInt();

					System.out.println("Admin requests for : " + currentPackage);

					if (messageType == '<') {

						dispatchUsers(socket, currentPackage, UserType.Client);

						System.out.println("clients dispatched");

					} else {

						dispatchUsers(socket, currentPackage, UserType.Device);

						System.out.println("devices dispatched");
					}
				} else if (messageType == '!') {

					List<Object> pairRequest;

					pairRequest = Serialization.deserializePairRequest(buffer);

					System.out.println(pairRequest.get(2) + " : " + pairRequest.get(1) + " : " + pairRequest.get(0));
					dispatchPairs(socket, (Integer) pairRequest.get(2), (String) pairRequest.get(1),
							(UserType) pairRequest.get(0));
				}
			}

			System.out.println("Socket is closed.");

			socket.close();

		} catch (

		IOException e) {

			throw new RuntimeException();

		}
	}

	private void invokePairUpdateListeners() {

		for (IClientDatabaseListener listener : clientDatabaseListeners) {

			listener.onGetPairUpdates();
		}
	}

	private void invokeDeviceUpdateListeners(String username, int state) {

		for (IDeviceDatabaseListener listener : deviceDatabaseListeners) {

			listener.onGetDeviceUpdates(username, state);
		}
	}

	private void invokeClientUpdateListeners(String username, int state) {

		for (IClientDatabaseListener listener : clientDatabaseListeners) {

			listener.onGetClientUpdates(username, state);
		}
	}

	private void dispatchPairs(Socket socket, int pageNumber, String userID, UserType userType) {

		int offset = pageNumber * 10;
		OutputStream outputStream = null;

		if (UserType.Client.equals(userType)) {

			if (clientToDevice.containsKey(namedClients.get(userID))) {
				for (int i = 0; i < 10 && i < clientToDevice.get(namedClients.get(userID)).size(); i++) {

					try {

						outputStream = socket.getOutputStream();

						if (outputStream != null) {

							outputStream.write(Serialization.serializePair(namedClients.get(userID),
									clientToDevice.get(namedClients.get(userID)).get(i + offset),
									clientToDevice.get(namedClients.get(userID)).size()));
						}

					} catch (IOException e) {

						e.printStackTrace();
					}

				}
			}

		} else if (UserType.Device.equals(userType)) {

			if (deviceToClient.containsKey(namedDevices.get(userID))) {

				for (int i = 0; i < 10 && i < deviceToClient.get(namedDevices.get(userID)).size(); i++) {

					try {

						outputStream = socket.getOutputStream();

						if (outputStream != null) {

							outputStream.write(Serialization.serializePair(namedDevices.get(userID),
									deviceToClient.get(namedDevices.get(userID)).get(i + offset),
									deviceToClient.get(namedDevices.get(userID)).size()));
						}

					} catch (IOException e) {

						e.printStackTrace();
					}

				}

			}
		}

	}

	private void sendUserCount(Socket socket) {

		ByteBuffer buffer = ByteBuffer.allocate(1024);
		buffer.putChar('#');
		buffer.putInt(allClients.size());
		buffer.putChar(',');
		buffer.putInt(allDevices.size());

		OutputStream outputStream = null;

		try {

			outputStream = socket.getOutputStream();

		} catch (IOException e) {

			e.printStackTrace();
		}

		if (outputStream != null) {

			try {

				outputStream.write(buffer.array());

			} catch (IOException e) {

				e.printStackTrace();
			}
		}
	}

	private void dispatchUsers(Socket socket, int pageNumber, UserType requestedUserType) {

		OutputStream outputStream = null;

		try {

			outputStream = socket.getOutputStream();

		} catch (IOException e) {

			e.printStackTrace();
		}

		if (UserType.Device.equals(requestedUserType)) {

			if (allDevices.isEmpty() == false) {

				for (int count = 0; count < 25 && pageNumber + count < allDevices.size(); count++) {

					DeviceUser device = allDevices.get(count + pageNumber);

					if (outputStream != null) {

						try {

							outputStream.write(Serialization.serializeUser(device));

						} catch (IOException e) {

							e.printStackTrace();
						}
					}
				}
			}

		}

		else {

			if (allClients.isEmpty() == false) {

				for (int count = 0; count < 25 && pageNumber + count < allClients.size(); count++) {

					ClientUser client = allClients.get(pageNumber + count);

					try {

						outputStream.write(Serialization.serializeUser(client));

					} catch (IOException e) {

						e.printStackTrace();
					}
				}
			}
		}
	}

	private void removeUserFromList(String deletedUser, UserType type) {

		if (UserType.Device.equals(type)) {

			for (User user : allDevices) {

				if (user.getUserID().equals(deletedUser)) {

					allDevices.remove(user);
					System.out.println(user.getUserID() + " is deleted from list");
					break;
				}
			}

		} else if (UserType.Client.equals(type)) {

			for (User user : allClients) {

				if (user.getUserID().equals(deletedUser)) {

					allClients.remove(user);
					System.out.println(user.getUserID() + " is deleted from list");
					break;
				}
			}

		}
	}

	private void initializeDevices() {

		new Thread(() -> {

			this.allDevices.addAll(serverDatabaseCommunicationHandler.getDevices());

			for (DeviceUser user : allDevices) {

				namedDevices.put(user.getUserID(), user);
			}

		}).run();

		new Thread(() -> {

			this.allClients.addAll(serverDatabaseCommunicationHandler.getClients());

			for (ClientUser user : allClients) {

				namedClients.put(user.getUserID(), user);
			}

		}).run();
	}

	private void initializePairs() {

		ArrayList<String[]> pairs = serverDatabaseCommunicationHandler.getPairedUsers();

		new Thread(() -> {

			for (String[] pair : pairs) {

				if (deviceToClient.containsKey(namedDevices.get(pair[0])) == true) {

					deviceToClient.get(namedDevices.get(pair[0])).add(namedClients.get(pair[1]));

				} else {

					deviceToClient.put(namedDevices.get(pair[0]), new ArrayList<User>());
					deviceToClient.get(namedDevices.get(pair[0])).add(namedClients.get(pair[1]));
				}
			}

		}).run();

		new Thread(() -> {

			for (String[] pair : pairs) {

				if (clientToDevice.containsKey(namedClients.get(pair[1])) == true) {

					clientToDevice.get(namedClients.get(pair[1])).add(namedDevices.get(pair[0]));

				} else {

					clientToDevice.put(namedClients.get(pair[1]), new ArrayList<User>());
					clientToDevice.get(namedClients.get(pair[1])).add(namedDevices.get(pair[0]));
				}
			}

		}).run();
	}

	private void addPairToLists(String client, String device) {

		if (clientToDevice.containsKey(namedClients.get(client)) == true) {

			clientToDevice.get(namedClients.get(client)).add(namedDevices.get(device));

		} else {

			clientToDevice.put(namedClients.get(client), new ArrayList<User>());
			clientToDevice.get(namedClients.get(client)).add(namedDevices.get(device));
		}

		if (deviceToClient.containsKey(namedDevices.get(device)) == true) {

			deviceToClient.get(namedDevices.get(device)).add(namedClients.get(client));

		} else {

			deviceToClient.put(namedDevices.get(device), new ArrayList<User>());
			deviceToClient.get(namedDevices.get(device)).add(namedClients.get(client));
		}

	}

	private void removePairFromLists(String client, String device) {

		if (clientToDevice.containsKey(namedClients.get(client)) == true) {

			clientToDevice.get(namedClients.get(client)).remove(namedDevices.get(device));

		}

		if (deviceToClient.containsKey(namedDevices.get(device)) == true) {

			deviceToClient.get(namedDevices.get(device)).remove(namedClients.get(client));
		}
	}

}
