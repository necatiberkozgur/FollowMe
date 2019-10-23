package com.ekinoks.followme.commclient.communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import com.ekinoks.followme.commclient.view.IPanelEventListener;
import com.ekinoks.followme.commclient.view.IUserLogController;
import com.ekinoks.followme.trackingutils.communication.ConnectionStatus;
import com.ekinoks.followme.trackingutils.communication.UserLog;
import com.ekinoks.followme.trackingutils.events.EngineEvent;
import com.ekinoks.followme.trackingutils.events.LocationEvent;
import com.ekinoks.followme.trackingutils.events.SeatBeltEvent;
import com.ekinoks.followme.trackingutils.users.UserType;
import com.google.gson.Gson;

public class ClientServerCommunicationHandler {

	private Socket socket;
	private InputStream inputStream;
	private boolean isConnectedToServer = false;

	private final AtomicBoolean running = new AtomicBoolean(false);

	private final LinkedBlockingQueue<Object> sendQueue = new LinkedBlockingQueue<>();
	private final LinkedBlockingQueue<Object> receiveQueue = new LinkedBlockingQueue<>();
	private final LinkedBlockingQueue<Object> errorMessageQueue = new LinkedBlockingQueue<>();
	private final LinkedBlockingQueue<Object> sentMessageListeners = new LinkedBlockingQueue<>();

	private Set<Consumer<Object>> messageListeners = Collections.synchronizedSet(new HashSet<>());
	private Set<Consumer<Object>> errorListeners = Collections.synchronizedSet(new HashSet<>());

	private Map<String, UserLog> deviceStateList = Collections.synchronizedMap(new HashMap<String, UserLog>());

	private Set<IPanelEventListener> connectionListeners = new HashSet<IPanelEventListener>();

	private Set<IUserLogController> onlineUserListeners = new HashSet<IUserLogController>();

	private String userID = "";

	public ClientServerCommunicationHandler() {

		Thread objectDispatcherThread = new Thread(this::dispatchReceivedObjects);
		objectDispatcherThread.start();

		Thread raiseErrorThread = new Thread(this::dispatchRaisedErrors);
		raiseErrorThread.start();

		addSentMessageListener(this::writeOutputStream);

		start();

	}

	public void addConnectionListener(IPanelEventListener listener) {

		if (connectionListeners.contains(listener) == false) {

			connectionListeners.add(listener);
		}
	}

	public void removeConnectionListener(IPanelEventListener listener) {

		if (connectionListeners.contains(listener) == true) {

			connectionListeners.remove(listener);
		}
	}

	public void addOnlineUserListener(IUserLogController listener) {

		if (onlineUserListeners.contains(listener) == false) {

			onlineUserListeners.add(listener);
		}
	}

	public void removeOnlineUserListener(IUserLogController listener) {

		if (onlineUserListeners.contains(listener) == true) {

			onlineUserListeners.remove(listener);
		}
	}

	public void start() {

		if (running.compareAndSet(false, true) == true) {

			try {

				socket = new Socket("localhost", 8085);
				inputStream = socket.getInputStream();

				isConnectedToServer = true;

				Thread streamReaderThread = new Thread(this::readInputStream);
				streamReaderThread.start();

				errorMessageQueue.put("Connected...");

			} catch (Exception e) {

				try {

					errorMessageQueue.put("Server is closed");

				} catch (InterruptedException e1) {

					e1.printStackTrace();
				}
			}
		}
	}

	public void stop() {

		if (running.compareAndSet(true, false) == true) {

			try {

				if (isConnectedToServer == true && socket.isClosed() == false) {

					isConnectedToServer = false;
					socket.close();
				}

			} catch (Exception e) {

				throw new RuntimeException(e);
			}

		}
	}

	public int getPacketSize() {

		return 1024;
	}

	public void sendMessage(Object message) {

		sendQueue.add(message);
	}

	public boolean getConnectionStatus() {

		return isConnectedToServer;
	}

	public String getUserID() {

		return userID;
	}

	public void setUserID(String userID) {

		this.userID = userID;
	}

	public List<UserLog> getAssignedUsers() {

		ArrayList<UserLog> ret = new ArrayList<UserLog>();

		for (UserLog log : deviceStateList.values()) {

			ret.add(log);
		}

		return ret;
	}

	public void addReceivedMessageListener(Consumer<Object> listener) {

		if (this.messageListeners.contains(listener) == false) {

			this.messageListeners.add(listener);
		}
	}

	public void removeReceivedMessageListener(Consumer<Object> listener) {

		if (this.messageListeners.contains(listener) == true) {

			this.messageListeners.remove(listener);
		}
	}

	public void addErrorListener(Consumer<Object> listener) {

		if (this.errorListeners.contains(listener) == false) {

			this.errorListeners.add(listener);
		}
	}

	public void removeErrorListener(Consumer<Object> listener) {

		if (this.errorListeners.contains(listener) == true) {

			this.errorListeners.remove(listener);
		}
	}

	public void addSentMessageListener(Consumer<Object> listener) {

		if (this.sentMessageListeners.contains(listener) == false) {

			this.sentMessageListeners.add(listener);
		}
	}

	public void removeSentMessageListener(Consumer<Object> listener) {

		if (this.sentMessageListeners.contains(listener) == true) {

			this.sentMessageListeners.remove(listener);
		}
	}

	private void invokeConnectionListeners() {

		for (IPanelEventListener listener : connectionListeners) {

			listener.onLogout();
		}
	}

	private void invokeOnlineUserListeners(UserLog log) {

		for (IUserLogController listener : onlineUserListeners) {

			listener.onGetUpdates(log);
		}
	}

	private void dispatchReceivedObjects() {

		while (true) {

			try {

				Object message = receiveQueue.take();

				for (Consumer<Object> listener : messageListeners) {

					System.out.println("MESSAGE DISPATCHED (CLIENT)");

					listener.accept(message);
				}

			} catch (InterruptedException e) {

				// TODO NECO log the exception
				System.out.println("Error: write request while reading!");
			}
		}
	}

	private void dispatchRaisedErrors() {

		while (true) {

			try {

				Object message = errorMessageQueue.take();

				for (Consumer<Object> listener : errorListeners) {

					listener.accept(message);
				}

			} catch (InterruptedException e) {

				// TODO NECO log the exception
				System.out.println("Error: write request while reading!");
			}
		}
	}

	@SuppressWarnings("unused")
	private void dispatchSentMessages() {

		while (true) {

			try {

				Object message = sentMessageListeners.take();

				for (Consumer<Object> listener : errorListeners) {

					listener.accept(message);
				}

			} catch (InterruptedException e) {

				// TODO NECO log the exception
				System.out.println("Error: write request while reading!");
			}
		}
	}

	private void writeOutputStream(Object message) {

		try {

			OutputStream outputStream = socket.getOutputStream();

			if (message != null) {

				outputStream.write(serializeMessage(message));

			} else {

				System.out.println("No messages to show");
			}

		} catch (Exception e) {

			// TODO NECO log the exception
			e.printStackTrace();
		}
	}

	private void readInputStream() {

		byte[] buffer = new byte[1024];

		try {

			while (running.get() == true && inputStream.read(buffer) != -1) {

				try {

					ByteBuffer buff = ByteBuffer.wrap(buffer);
					char msgCode = buff.getChar();

					if (msgCode == '$') {

						receiveQueue.put(deserializeMessage(buffer));

					} else if (msgCode == '}') {

						submitDeviceStatus((UserLog) deserializeMessage(buffer));

					} else if (msgCode == '{') {

						submitDeviceStatus((UserLog) deserializeMessage(buffer));

					} else if (msgCode == '#') {

					} else if (msgCode == '-') {

						stop();
						invokeConnectionListeners();

					} else if (msgCode == '*') {

						int queryResult;

						queryResult = buff.getInt();

						if (queryResult == 6) {

							writeOutputStream(this.getUserID());
							invokeLoginListeners();

						} else if (queryResult == 3) {

							for (IPanelEventListener panel : connectionListeners) {

								panel.invalidUsernamePopup();
							}

						} else if (queryResult == 5) {

							for (IPanelEventListener panel : connectionListeners) {

								panel.invalidPasswordPopup();
							}

						}
					}

				} catch (InterruptedException e) {

					e.printStackTrace();
				}
			}

			// UNEXPECTED CONNECTION LOSE (i.e. inputStream = -1)

			isConnectedToServer = false;
			errorMessageQueue.put("Connection error!");

			for (IPanelEventListener panel : connectionListeners) {

				panel.serverFailPopup();
			}

		} catch (IOException e) {

			// SIGN OUT CASE(i.e. user clicked sign out button)

			try {

				errorMessageQueue.put("Signed out.");

			} catch (InterruptedException e1) {

				e1.printStackTrace();
			}

		} catch (InterruptedException e) {

			e.printStackTrace();
		}
	}

	private byte[] serializeMessage(Object message) {

		Gson gson = new Gson();

		if (message instanceof EngineEvent) {

			message = (EngineEvent) message;

			ByteBuffer buff = ByteBuffer.allocate(1024);
			buff.putChar('$');
			buff.putInt(101);
			buff.putChar(',');
			buff.putInt(gson.toJson(message).getBytes().length);
			buff.putChar(',');
			buff.put(gson.toJson(message).getBytes());

			return buff.array();

		} else if (message instanceof SeatBeltEvent) {

			message = (SeatBeltEvent) message;

			ByteBuffer buff = ByteBuffer.allocate(1024);
			buff.putChar('$');
			buff.putInt(102);
			buff.putChar(',');
			buff.putInt(gson.toJson(message).getBytes().length);
			buff.putChar(',');
			buff.put(gson.toJson(message).getBytes());

			return buff.array();

		} else if (message instanceof LocationEvent) {

			message = (LocationEvent) message;

			ByteBuffer buff = ByteBuffer.allocate(1024);
			buff.putChar('$');
			buff.putInt(100);
			buff.putChar(',');
			buff.putInt(gson.toJson(message).getBytes().length);
			buff.putChar(',');
			buff.put(gson.toJson(message).getBytes());

			return buff.array();

		} else if (message instanceof String) {

			ByteBuffer buff = ByteBuffer.allocate(1024);
			buff.putChar('#');
			buff.putInt(((String) message).getBytes().length);
			buff.putChar(',');
			buff.put(((String) message).getBytes());

			return buff.array();

		} else if (message instanceof ByteBuffer) {

			return ((ByteBuffer) message).array();
		}

		return null;
	}

	private Object deserializeMessage(byte[] message) {

		char messageType;
		byte[] msgPart;

		ByteBuffer buff = ByteBuffer.wrap(message);
		messageType = buff.getChar();
		int msgSize = 0;

		if (messageType == '$') {

			Gson gson = new Gson();
			int msgID;
			msgID = buff.getInt();
			buff.getChar();
			msgSize = buff.getInt();
			msgPart = new byte[msgSize];
			buff.getChar();
			buff.get(msgPart, buff.arrayOffset(), msgSize);

			System.out.println("Message ID = " + msgID + " Message size: " + msgSize);

			if (msgID == 100) {

				System.out.println("Loc evt deserialized by client");
				return gson.fromJson(new BufferedReader(new StringReader(new String(msgPart))), LocationEvent.class);

			} else if (msgID == 101) {

				System.out.println("Eng evt deserialized by client");
				return gson.fromJson(new BufferedReader(new StringReader(new String(msgPart))), EngineEvent.class);

			} else if (msgID == 102) {

				System.out.println("SB evt deserialized by client");
				return gson.fromJson(new BufferedReader(new StringReader(new String(msgPart))), SeatBeltEvent.class);

			}

		} else if (messageType == '}') {

			String name;
			String timeStamp;

			msgSize = buff.getInt();
			msgPart = new byte[msgSize];

			buff.getChar();
			buff.get(msgPart, buff.arrayOffset(), msgSize);
			name = new String(msgPart);

			buff.getChar();
			msgSize = buff.getInt();
			msgPart = new byte[msgSize];
			buff.getChar();
			buff.get(msgPart, buff.arrayOffset(), msgSize);
			timeStamp = new String(msgPart);

			return new UserLog(name, UserType.Device, ConnectionStatus.ONLINE, timeStamp);

		} else if (messageType == '{') {

			String name;
			String timeStamp;
			msgSize = buff.getInt();
			msgPart = new byte[msgSize];
			buff.getChar();
			buff.get(msgPart, buff.arrayOffset(), msgSize);
			name = new String(msgPart);
			buff.getChar();
			msgSize = buff.getInt();
			msgPart = new byte[msgSize];
			buff.getChar();
			buff.get(msgPart, buff.arrayOffset(), msgSize);
			timeStamp = new String(msgPart);

			return new UserLog(name, UserType.Device, ConnectionStatus.OFFLINE, timeStamp);

		}

		return null;

	}

	private void invokeLoginListeners() {

		for (IPanelEventListener listener : connectionListeners) {

			listener.onLogin();
		}
	}

	private void submitDeviceStatus(UserLog log) {

		if (deviceStateList.containsKey(log.getUserID()) == true) {

			deviceStateList.replace(log.getUserID(), log);

		} else {

			deviceStateList.put(log.getUserID(), log);
		}

		invokeOnlineUserListeners(log);
	}

	public void sendLoginRequest(String username, String password) {

		userID = username;

		ByteBuffer buff = ByteBuffer.allocate(1024);
		buff.putChar('?');
		buff.putInt(12);
		buff.putChar(',');
		buff.putInt(username.getBytes().length);
		buff.putChar(',');
		buff.put(username.getBytes());
		buff.putChar(',');
		buff.putInt(password.getBytes().length);
		buff.putChar(',');
		buff.put(password.getBytes());

		writeOutputStream(buff);

	}

}
