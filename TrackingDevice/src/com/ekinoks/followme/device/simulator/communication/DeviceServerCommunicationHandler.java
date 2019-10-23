package com.ekinoks.followme.device.simulator.communication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import com.ekinoks.followme.device.simulator.view.IPanelEventListener;
import com.ekinoks.followme.trackingutils.events.EngineEvent;
import com.ekinoks.followme.trackingutils.events.LocationEvent;
import com.ekinoks.followme.trackingutils.events.SeatBeltEvent;
import com.google.gson.Gson;

class DeviceServerCommunicationHandler implements IDeviceServerCommunicationHandler {

	private static final Gson gson = new Gson();
	private boolean isConnected = false;
	private String deviceName = "";
	private Socket socket = null;
	private OutputStream st;
	private InputStream inputStream;

	private final AtomicBoolean running = new AtomicBoolean(false);

	private Set<IPanelEventListener> connectionListeners = new HashSet<IPanelEventListener>();

	public DeviceServerCommunicationHandler() {

		startConnection();
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

	@Override
	public void sendLocationEvent(LocationEvent event) {

		ByteBuffer buff = ByteBuffer.allocate(1024);
		buff.putChar('$');
		buff.putInt(100);
		buff.putChar(',');
		buff.putInt(gson.toJson(event).getBytes().length);
		buff.putChar(',');
		buff.put(gson.toJson(event).getBytes());
		System.out.println(new String(gson.toJson(event).getBytes()));
		System.out.println("A location event has been sent with size: " + gson.toJson(event).getBytes().length);

		try {

			st.write(buff.array());
			System.out.println("Location event generated and sent");

		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	@Override
	public void sendEngineEvent(EngineEvent event) {

		ByteBuffer buff = ByteBuffer.allocate(1024);
		buff.putChar('$');
		buff.putInt(101);
		buff.putChar(',');
		buff.putInt(gson.toJson(event).getBytes().length);
		buff.putChar(',');
		buff.put(gson.toJson(event).getBytes());

		try {

			st.write(buff.array());
			System.out.println("Engine event generated and sent");

		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void sendSeatBeltEvent(SeatBeltEvent event) {

		ByteBuffer buff = ByteBuffer.allocate(1024);
		buff.putChar('$');
		buff.putInt(102);
		buff.putChar(',');
		buff.putInt(gson.toJson(event).getBytes().length);
		buff.putChar(',');
		buff.put(gson.toJson(event).getBytes());

		try {

			st.write(buff.array());
			System.out.println("SeatBelt event generated and sent");

		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void finishConnection() {

		if (running.compareAndSet(true, false) == true) {

			try {
				if (isConnected == true && socket.isClosed() == false) {

					isConnected = false;
					socket.close();
				}
			} catch (IOException e) {

				e.printStackTrace();
			}

		}
	}

	public void startConnection() {

		if (running.compareAndSet(false, true) == true) {

			try {

				socket = new Socket("localhost", 8086);
				st = socket.getOutputStream();
				inputStream = socket.getInputStream();
				isConnected = true;

				Thread readInputStreamThread = new Thread(this::readInputStream);
				readInputStreamThread.start();

			} catch (UnknownHostException e) {

			} catch (IOException e) {

			}

		}

	}

	public boolean getConnectionStatus() {

		return this.isConnected;
	}

	public String getDeviceName() {

		return deviceName;
	}

	public void setDeviceName(String deviceName) {

		this.deviceName = deviceName;
	}

	private void invokeLogoutListeners() {

		for (IPanelEventListener listener : connectionListeners) {

			listener.onLogout();
		}
	}

	private void invokeLoginListeners() {

		for (IPanelEventListener listener : connectionListeners) {

			listener.onLogin();

		}
	}

	private void readInputStream() {

		byte[] buffer = new byte[1024];

		try {

			while ((running.get() == true && inputStream.read(buffer) != -1)) {

				if (ByteBuffer.wrap(buffer).getChar() == '-') {

					finishConnection();
					invokeLogoutListeners();

				} else if (ByteBuffer.wrap(buffer).getChar() == '*') {

					int queryResult;

					ByteBuffer bufferedMessage = ByteBuffer.wrap(buffer);
					bufferedMessage.getChar();
					queryResult = bufferedMessage.getInt();

					if (queryResult == 6) {

						sendRegisterMessage();
						invokeLoginListeners();

					} else if (queryResult == 3) {

						for (IPanelEventListener listener : connectionListeners) {

							listener.invalidUsernamePopup();
						}
					} else if (queryResult == 4) {

						for (IPanelEventListener listener : connectionListeners) {

							listener.databaseFailPopup();
						}
					} else if (queryResult == 5) {

						for (IPanelEventListener listener : connectionListeners) {

							listener.invalidPasswordPopup();
						}
					}

				}
			}

		} catch (IOException e) {

			// e.printStackTrace();
		}

		isConnected = false;
	}

	private void sendRegisterMessage() {

		ByteBuffer buff = ByteBuffer.allocate(1024);
		buff.putChar('#');
		buff.putInt(((String) this.deviceName).getBytes().length);
		buff.putChar(',');
		buff.put(((String) this.deviceName).getBytes());

		try {

			st.write(buff.array());
			System.out.println("Device sent register request. Name: " + this.deviceName);

		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	public void sendLoginRequest(String username, String password) {

		deviceName = username;

		ByteBuffer buff = ByteBuffer.allocate(1024);
		buff.putChar('?');
		buff.putInt(11);
		buff.putChar(',');
		buff.putInt(username.getBytes().length);
		buff.putChar(',');
		buff.put(username.getBytes());
		buff.putChar(',');
		buff.putInt(password.getBytes().length);
		buff.putChar(',');
		buff.put(password.getBytes());

		try {

			st.write(buff.array());
			System.out.println("Device sent login request. Name: " + this.deviceName);

		} catch (IOException e) {

			e.printStackTrace();
		}
	}

}
