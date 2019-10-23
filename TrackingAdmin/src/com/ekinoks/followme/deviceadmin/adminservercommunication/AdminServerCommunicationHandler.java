package com.ekinoks.followme.deviceadmin.adminservercommunication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.ekinoks.followme.deviceadmin.adminview.IClientPanel;
import com.ekinoks.followme.deviceadmin.adminview.IDevicePanel;
import com.ekinoks.followme.deviceadmin.adminview.IPanelEventListener;
import com.ekinoks.followme.trackingutils.communication.Serialization;
import com.ekinoks.followme.trackingutils.users.ClientUser;
import com.ekinoks.followme.trackingutils.users.DeviceUser;
import com.ekinoks.followme.trackingutils.users.IUser;
import com.ekinoks.followme.trackingutils.users.User;
import com.ekinoks.followme.trackingutils.users.UserType;
import com.ekinoks.followme.trackingutils.users.UserWrapper;

public class AdminServerCommunicationHandler {

	private int deviceCount = 0;
	private int clientCount = 0;

	private boolean isConnected = false;
	private Socket socket = null;
	private OutputStream outputStream = null;
	private InputStream inputStream = null;
	private Set<IClientPanel> clientListeners = new HashSet<IClientPanel>();
	private Set<IDevicePanel> deviceListeners = new HashSet<IDevicePanel>();
	private Set<IPanelEventListener> mainPanelListeners = new HashSet<IPanelEventListener>();

	public AdminServerCommunicationHandler() {

		startConnection();

	}

	private void readInputStream() {

		byte[] buffer = new byte[1024];

		try {

			while (inputStream.read(buffer) != -1) {

				ByteBuffer bufferedMessage = ByteBuffer.wrap(buffer);
				char msgType = bufferedMessage.getChar();
				int msgSize;
				byte[] partialMessage;

				if (msgType == '*') {

					int queryResult = bufferedMessage.getInt();

					System.out.println("Query message handled by admin: " + queryResult);
					if (queryResult == 2) {

						for (IPanelEventListener listener : mainPanelListeners) {

							listener.onUserExists();
						}

					} else if (queryResult == 7) {

						System.out.println("Pair does not exist");

					} else if (queryResult == 8) {

						System.out.println("Pair exists");

					} else if (queryResult == 9) {

						User user = deserializeUser(buffer);
						for (IClientPanel client : clientListeners) {

							client.onAddUser(user);

						}

						clientCount++;

					} else if (queryResult == 10) {

						String username;

						bufferedMessage.getChar();
						msgSize = bufferedMessage.getInt();
						partialMessage = new byte[msgSize];
						bufferedMessage.getChar();
						bufferedMessage.get(partialMessage, bufferedMessage.arrayOffset(), msgSize);
						username = new String(partialMessage);

						for (IClientPanel client : clientListeners) {

							client.onDeleteUserFromList(username);
						}

						clientCount--;

					} else if (queryResult == 11) {

						User user = deserializeUser(buffer);

						for (IDevicePanel device : deviceListeners) {

							device.onAddUser(user);
						}

						deviceCount++;

					} else if (queryResult == 12) {

						String username;
						bufferedMessage.getChar();
						msgSize = bufferedMessage.getInt();
						partialMessage = new byte[msgSize];
						bufferedMessage.getChar();
						bufferedMessage.get(partialMessage, bufferedMessage.arrayOffset(), msgSize);
						username = new String(partialMessage);

						for (IDevicePanel device : deviceListeners) {

							device.onDeleteUserFromList(username);
						}

						deviceCount--;

					} else if (queryResult == 13) {

						System.out.println("pair added");

					} else if (queryResult == 14) {

						System.out.println("pair removed");
					}

				} else if (msgType == '>') {

					User user = Serialization.deserializeUser(buffer);

					if (UserType.Device.equals(user.getType())) {

						for (IDevicePanel deviceListener : deviceListeners) {

							deviceListener.onAddUser(user);
						}

					} else {

						for (IClientPanel clientListener : clientListeners) {

							clientListener.onAddUser(user);
						}

					}

				} else if (msgType == '#') {

					clientCount = bufferedMessage.getInt();
					bufferedMessage.getChar();
					deviceCount = bufferedMessage.getInt();

					System.out.println(clientCount + " clients and " + deviceCount + " devices");

				} else if (msgType == '!') {

					ArrayList<IUser> pair = (ArrayList<IUser>) Serialization.deserializePair(buffer);

					int pairCount = bufferedMessage.getInt();

					if (UserType.Client.equals(pair.get(0).getUserType()) == true) {

						for (IClientPanel listener : clientListeners) {

							listener.onAddPair(pair.get(0), pair.get(1), pairCount);
						}

					} else {

						for (IDevicePanel listener : deviceListeners) {

							listener.onAddPair(pair.get(0), pair.get(1), pairCount);
						}
					}
				}
			}

		} catch (IOException e) {

			for (IPanelEventListener listener : mainPanelListeners) {

				listener.onLostConnection();
			}
		}

		isConnected = false;

	}

	public void sendRegisterMessage() {

		ByteBuffer buff = ByteBuffer.allocate(1024);
		buff.putChar('#');

		try {

			outputStream.write(buff.array());

		} catch (IOException e) {

			for (IPanelEventListener listener : mainPanelListeners) {

				listener.onLostConnection();
			}
		}
	}

	public void requestPair(IUser user, int page) {

		ByteBuffer buff = ByteBuffer.allocate(1024);
		buff.putChar('!');
		buff.putInt(user.getUserType().ordinal());
		buff.putChar(',');
		buff.putInt(user.getUserID().getBytes().length);
		buff.putChar(',');
		buff.put(user.getUserID().getBytes());
		buff.putChar(',');
		buff.putInt(page);

		try {

			outputStream.write(buff.array());

		} catch (IOException e) {

			for (IPanelEventListener listener : mainPanelListeners) {

				listener.onLostConnection();
			}
		}
	}

	public void addClientListener(IClientPanel client) {

		if (clientListeners.contains(client) == false) {

			clientListeners.add(client);
		}
	}

	public void addDeviceListener(IDevicePanel device) {

		if (deviceListeners.contains(device) == false) {

			deviceListeners.add(device);
		}
	}

	public void finishConnection() {

		try {

			socket.close();
			isConnected = false;
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	public void startConnection() {

		try {

			socket = new Socket("localhost", 8087);
			outputStream = socket.getOutputStream();
			inputStream = socket.getInputStream();
			isConnected = true;
			Thread readInputStreamThread = new Thread(this::readInputStream);
			readInputStreamThread.start();

		} catch (UnknownHostException e) {

		} catch (IOException e) {

			e.printStackTrace();

		}
	}

	public void addPair(String usernameDevice, String usernameClient) {

		if (outputStream != null) {

			ByteBuffer buff = ByteBuffer.allocate(1024);
			buff.putChar('?');
			buff.putInt(4);
			buff.putChar(',');
			buff.putInt(usernameDevice.getBytes().length);
			buff.putChar(',');
			buff.put(usernameDevice.getBytes());
			buff.putChar(',');
			buff.putInt(usernameClient.getBytes().length);
			buff.putChar(',');
			buff.put(usernameClient.getBytes());

			try {

				outputStream.write(buff.array());

			} catch (IOException e) {

				for (IPanelEventListener listener : mainPanelListeners) {

					listener.onLostConnection();
				}
			}
		}
	}

	public void removePair(String usernameDevice, String usernameClient) {

		if (outputStream != null) {

			ByteBuffer buff = ByteBuffer.allocate(1024);
			buff.putChar('?');
			buff.putInt(5);
			buff.putChar(',');
			buff.putInt(usernameDevice.getBytes().length);
			buff.putChar(',');
			buff.put(usernameDevice.getBytes());
			buff.putChar(',');
			buff.putInt(usernameClient.getBytes().length);
			buff.putChar(',');
			buff.put(usernameClient.getBytes());

			try {

				outputStream.write(buff.array());

			} catch (IOException e) {

				for (IPanelEventListener listener : mainPanelListeners) {

					listener.onLostConnection();
				}
			}
		}
	}

	public void deleteDevicesAllPairs(String username) {

		if (outputStream != null) {

			ByteBuffer buff = ByteBuffer.allocate(1024);
			buff.putChar('?');
			buff.putInt(6);
			buff.putChar(',');
			buff.putInt(username.getBytes().length);
			buff.putChar(',');
			buff.put(username.getBytes());

			try {

				outputStream.write(buff.array());

			} catch (IOException e) {

				for (IPanelEventListener listener : mainPanelListeners) {

					listener.onLostConnection();
				}
			}
		}
	}

	public void deleteClientsAllPairs(String username) {

		if (outputStream != null) {

			ByteBuffer buff = ByteBuffer.allocate(1024);
			buff.putChar('?');
			buff.putInt(7);
			buff.putChar(',');
			buff.putInt(username.getBytes().length);
			buff.putChar(',');
			buff.put(username.getBytes());

			try {

				outputStream.write(buff.array());

			} catch (IOException e) {

				for (IPanelEventListener listener : mainPanelListeners) {

					listener.onLostConnection();
				}
			}
		}
	}

	public void addUser(User u) {

		if (outputStream != null) {

			UserWrapper wrappedUser = new UserWrapper(u);
			int userType = wrappedUser.getUserType().ordinal();
			String userID = wrappedUser.getUserID();
			String password = wrappedUser.getPassword();
			String userSpecific = wrappedUser.getUserSpecificFields();

			ByteBuffer buff = ByteBuffer.allocate(1024);
			buff.putChar('?');
			buff.putInt(0);
			buff.putChar(',');
			buff.putInt(userType);
			buff.putChar(',');
			buff.putInt(userID.getBytes().length);
			buff.putChar(',');
			buff.put(userID.getBytes());
			buff.putChar(',');
			buff.putInt(password.getBytes().length);
			buff.putChar(',');
			buff.put(password.getBytes());
			buff.putChar(',');
			buff.putInt(userSpecific.getBytes().length);
			buff.putChar(',');
			buff.put(userSpecific.getBytes());

			try {

				outputStream.write(buff.array());

			} catch (IOException e) {

				for (IPanelEventListener listener : mainPanelListeners) {

					listener.onLostConnection();
				}
			}
		}
	}

	public void fetchUsers(UserType type, int pageNumber) {

		if (outputStream != null) {

			ByteBuffer buff = ByteBuffer.allocate(1024);

			if (UserType.Client.equals(type)) {

				buff.putChar('<');
				buff.putInt((pageNumber) * 25);

			} else {

				buff.putChar('=');
				buff.putInt((pageNumber) * 25);
			}

			try {

				outputStream.write(buff.array());

			} catch (IOException e) {

				for (IPanelEventListener listener : mainPanelListeners) {

					listener.onLostConnection();
				}
			}
		}
	}

	public void fetchPreviousUsers(UserType type, int currentPage) {

		if (outputStream != null) {

			ByteBuffer buff = ByteBuffer.allocate(1024);

			if (UserType.Client.equals(type)) {

				buff.putChar('<');
				buff.putInt((currentPage - 1) * 10 - 10);

			} else {

				buff.putChar('=');
				buff.putInt((currentPage - 1) * 10 - 10);
			}

			try {

				outputStream.write(buff.array());

			} catch (IOException e) {

				for (IPanelEventListener listener : mainPanelListeners) {

					listener.onLostConnection();
				}
			}
		}
	}

	public void removeUser(User u) {

		if (outputStream != null) {

			UserWrapper wrappedUser = new UserWrapper(u);
			int userType = wrappedUser.getUserType().ordinal();
			String userID = wrappedUser.getUserID();
			ByteBuffer buff = ByteBuffer.allocate(1024);
			buff.putChar('?');
			buff.putInt(1);
			buff.putChar(',');
			buff.putInt(userType);
			buff.putChar(',');
			buff.putInt(userID.getBytes().length);
			buff.putChar(',');
			buff.put(userID.getBytes());

			try {

				outputStream.write(buff.array());

			} catch (IOException e) {

				for (IPanelEventListener listener : mainPanelListeners) {

					listener.onLostConnection();
				}
			}
		}
	}

	public boolean getConnectionStatus() {

		return this.isConnected;
	}

	private User deserializeUser(byte[] buffer) {

		ByteBuffer bufferedMessage = ByteBuffer.wrap(buffer);
		bufferedMessage.getChar();
		int msgSize;
		byte[] partialMessage;
		int queryResult = bufferedMessage.getInt();

		String userID;
		String dateAdded;
		String userSpecificField;

		bufferedMessage.getChar();
		msgSize = bufferedMessage.getInt();
		bufferedMessage.getChar();

		partialMessage = new byte[msgSize];
		bufferedMessage.get(partialMessage, bufferedMessage.arrayOffset(), msgSize);
		userID = new String(partialMessage);

		bufferedMessage.getChar();
		msgSize = bufferedMessage.getInt();
		bufferedMessage.getChar();

		partialMessage = new byte[msgSize];
		bufferedMessage.get(partialMessage, bufferedMessage.arrayOffset(), msgSize);
		dateAdded = new String(partialMessage);

		bufferedMessage.getChar();
		msgSize = bufferedMessage.getInt();
		bufferedMessage.getChar();

		partialMessage = new byte[msgSize];
		bufferedMessage.get(partialMessage, bufferedMessage.arrayOffset(), msgSize);
		userSpecificField = new String(partialMessage);

		LocalDateTime time = LocalDateTime.parse(dateAdded);

		if (queryResult == 9 || queryResult == 10) {

			return new ClientUser(userSpecificField, "-", userID, time);

		} else if (queryResult == 11 || queryResult == 12) {

			return new DeviceUser(userSpecificField, "-", userID, time);
		}

		return null;
	}

	public int getDeviceCount() {

		return deviceCount;
	}

	public void setDeviceCount(int deviceCount) {

		this.deviceCount = deviceCount;
	}

	public int getClientCount() {

		return clientCount;
	}

	public void setClientCount(int clientCount) {

		this.clientCount = clientCount;
	}

	public void addPanelListener(IPanelEventListener listener) {

		if (mainPanelListeners.contains(listener) == false) {

			mainPanelListeners.add(listener);
		}
	}

}
