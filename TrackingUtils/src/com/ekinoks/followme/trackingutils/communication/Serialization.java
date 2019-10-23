package com.ekinoks.followme.trackingutils.communication;

import java.io.BufferedReader;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.ekinoks.followme.trackingutils.databaseops.QueryExitStatus;
import com.ekinoks.followme.trackingutils.events.EngineEvent;
import com.ekinoks.followme.trackingutils.events.LocationEvent;
import com.ekinoks.followme.trackingutils.events.SeatBeltEvent;
import com.ekinoks.followme.trackingutils.users.ClientUser;
import com.ekinoks.followme.trackingutils.users.DeviceUser;
import com.ekinoks.followme.trackingutils.users.IUser;
import com.ekinoks.followme.trackingutils.users.User;
import com.ekinoks.followme.trackingutils.users.UserType;
import com.ekinoks.followme.trackingutils.users.UserWrapper;
import com.google.gson.Gson;

public class Serialization {

	public static Object deserializeMessage(byte[] message) {

		Gson gson = new Gson();
		int msgID, msgSize = 0;

		byte[] jsonPart;
		ByteBuffer buff = ByteBuffer.wrap(message);

		buff.getChar();
		msgID = buff.getInt();
		buff.getChar();
		msgSize = buff.getInt();
		jsonPart = new byte[msgSize];
		buff.getChar();
		buff.get(jsonPart, buff.arrayOffset(), msgSize);

		if (msgID == 100) {

			return gson.fromJson(new BufferedReader(new StringReader(new String(jsonPart))), LocationEvent.class);
		} else if (msgID == 101) {

			return gson.fromJson(new BufferedReader(new StringReader(new String(jsonPart))), EngineEvent.class);
		} else if (msgID == 102) {

			return gson.fromJson(new BufferedReader(new StringReader(new String(jsonPart))), SeatBeltEvent.class);
		} else {

			return null;
		}
	}

	public static String deserializeStringMessage(byte[] message) {

		int msgSize = 0;
		byte[] stringPart;
		ByteBuffer bufferedMessage = ByteBuffer.wrap(message);
		bufferedMessage.getChar();
		msgSize = bufferedMessage.getInt();
		stringPart = new byte[msgSize];
		bufferedMessage.getChar();
		bufferedMessage.get(stringPart, bufferedMessage.arrayOffset(), msgSize);
		return new String(stringPart);
	}

	public static byte[] serializePair(User user1, User user2, int userCount) {

		ByteBuffer buffer = ByteBuffer.allocate(1024);
		UserWrapper userWrapper1 = new UserWrapper(user1);
		UserWrapper userWrapper2 = new UserWrapper(user2);
		buffer.putChar('!');
		buffer.putInt(userCount);
		buffer.putChar(',');
		buffer.putInt(userWrapper1.getUserType().ordinal());
		buffer.putChar(',');
		buffer.putInt(userWrapper1.getUserID().getBytes().length);
		buffer.putChar(',');
		buffer.put(userWrapper1.getUserID().getBytes());
		buffer.putChar(',');
		buffer.putInt(userWrapper1.getUserSpecificFields().getBytes().length);
		buffer.putChar(',');
		buffer.put(userWrapper1.getUserSpecificFields().getBytes());
		buffer.putChar(',');
		buffer.putInt(userWrapper1.getDateAdded().toString().getBytes().length);
		buffer.putChar(',');
		buffer.put(userWrapper1.getDateAdded().toString().getBytes());
		buffer.putChar(',');
		buffer.putInt(userWrapper2.getUserID().getBytes().length);
		buffer.putChar(',');
		buffer.put(userWrapper2.getUserID().getBytes());
		buffer.putChar(',');
		buffer.putInt(userWrapper2.getUserSpecificFields().getBytes().length);
		buffer.putChar(',');
		buffer.put(userWrapper2.getUserSpecificFields().getBytes());
		buffer.putChar(',');
		buffer.putInt(userWrapper2.getDateAdded().toString().getBytes().length);
		buffer.putChar(',');
		buffer.put(userWrapper2.getDateAdded().toString().getBytes());

		return buffer.array();

	}

	public static List<IUser> deserializePair(byte[] message) {

		ArrayList<IUser> returnedPairs = new ArrayList<IUser>();

		int msgSize;
		int userType;
		byte[] partialMessage;
		String userID;
		String userSpecific;
		LocalDateTime dateAdded;

		ByteBuffer bufferedMessage = ByteBuffer.wrap(message);

		bufferedMessage.getChar();
		userType = bufferedMessage.getInt();
		bufferedMessage.getChar();
		userType = bufferedMessage.getInt();

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
		userSpecific = new String(partialMessage);

		bufferedMessage.getChar();
		msgSize = bufferedMessage.getInt();
		bufferedMessage.getChar();
		partialMessage = new byte[msgSize];
		bufferedMessage.get(partialMessage, bufferedMessage.arrayOffset(), msgSize);
		dateAdded = LocalDateTime.parse(new String(partialMessage));

		if (userType == 0) {

			returnedPairs.add(new UserWrapper(new ClientUser(userSpecific, "", userID, dateAdded)));
		} else {
			returnedPairs.add(new UserWrapper(new DeviceUser(userSpecific, "", userID, dateAdded)));

		}

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
		userSpecific = new String(partialMessage);

		bufferedMessage.getChar();
		msgSize = bufferedMessage.getInt();
		bufferedMessage.getChar();
		partialMessage = new byte[msgSize];
		bufferedMessage.get(partialMessage, bufferedMessage.arrayOffset(), msgSize);
		dateAdded = LocalDateTime.parse(new String(partialMessage));

		if (userType == 0) {

			returnedPairs.add(new UserWrapper(new DeviceUser(userSpecific, "", userID, dateAdded)));

		} else {

			returnedPairs.add(new UserWrapper(new ClientUser(userSpecific, "", userID, dateAdded)));

		}

		return returnedPairs;
	}

	public static List<Object> deserializePairRequest(byte[] message) {

		ArrayList<Object> returnList = new ArrayList<>();
		int msgSize = 0;
		UserType userType;
		int pageNumber = 0;
		byte[] partialMessage;
		String userID;

		ByteBuffer bufferedMessage = ByteBuffer.wrap(message);
		bufferedMessage.getChar();
		userType = UserType.values()[bufferedMessage.getInt()];
		bufferedMessage.getChar();
		msgSize = bufferedMessage.getInt();
		bufferedMessage.getChar();
		partialMessage = new byte[msgSize];
		bufferedMessage.get(partialMessage, bufferedMessage.arrayOffset(), msgSize);
		userID = new String(partialMessage);
		bufferedMessage.getChar();
		pageNumber = bufferedMessage.getInt();

		returnList.add(userType);
		returnList.add(userID);
		returnList.add(new Integer(pageNumber));
		return returnList;
	}

	public static List<Object> deserializeDatabaseRequest(byte[] message) {

		ArrayList<Object> returnList = new ArrayList<>();
		int msgSize = 0;
		int requestCode = 0;
		byte[] partialMessage;
		ByteBuffer bufferedMessage = ByteBuffer.wrap(message);

		bufferedMessage.getChar(); // Fetch "?" sign.
		requestCode = bufferedMessage.getInt(); // Fetch req code.

		if (requestCode == 0) {

			String userID;
			String password;
			String userSpecificField;

			bufferedMessage.getChar();
			UserType userType = UserType.values()[bufferedMessage.getInt()];
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
			password = new String(partialMessage);

			bufferedMessage.getChar();
			msgSize = bufferedMessage.getInt();
			bufferedMessage.getChar();

			partialMessage = new byte[msgSize];
			bufferedMessage.get(partialMessage, bufferedMessage.arrayOffset(), msgSize);
			userSpecificField = new String(partialMessage);

			if (userType.equals(UserType.Device)) {

				returnList.add(new DeviceUser(userSpecificField, password, userID));

			} else {

				returnList.add(new ClientUser(userSpecificField, password, userID));
			}

		} else if (requestCode == 1) {

			String username;
			UserType userType;

			bufferedMessage.getChar();
			userType = UserType.values()[bufferedMessage.getInt()];

			bufferedMessage.getChar();

			msgSize = bufferedMessage.getInt();
			bufferedMessage.getChar();
			partialMessage = new byte[msgSize];
			bufferedMessage.get(partialMessage, bufferedMessage.arrayOffset(), msgSize);

			username = new String(partialMessage);
			System.out.println(username + userType);
			if (UserType.Client.equals(userType)) {

				returnList.add(new ClientUser("-", "-", username, LocalDateTime.now()));

			} else if (UserType.Device.equals(userType)) {

				returnList.add(new DeviceUser("-", "-", username, LocalDateTime.now()));

			}

		} else if (requestCode == 4 || requestCode == 5) {

			// Add & remove pair

			bufferedMessage.getChar();
			msgSize = bufferedMessage.getInt();
			partialMessage = new byte[msgSize];
			bufferedMessage.getChar();
			bufferedMessage.get(partialMessage, bufferedMessage.arrayOffset(), msgSize);
			returnList.add(new String(partialMessage));

			bufferedMessage.getChar();
			msgSize = bufferedMessage.getInt();
			partialMessage = new byte[msgSize];
			bufferedMessage.getChar();
			bufferedMessage.get(partialMessage, bufferedMessage.arrayOffset(), msgSize);
			returnList.add(new String(partialMessage));

		} else if (requestCode == 6 || requestCode == 7) {
			// Delete clients/devices all pairs

			bufferedMessage.getChar();
			msgSize = bufferedMessage.getInt();
			partialMessage = new byte[msgSize];
			bufferedMessage.getChar();
			bufferedMessage.get(partialMessage, bufferedMessage.arrayOffset(), msgSize);
			returnList.add(new String(partialMessage));

		} else if (requestCode == 8) {
			// Get all devices
		} else if (requestCode == 9) {

		} else if (requestCode == 10) {

		} else if (requestCode == 11 || requestCode == 12) {

			bufferedMessage.getChar();
			msgSize = bufferedMessage.getInt();
			partialMessage = new byte[msgSize];
			bufferedMessage.getChar();
			bufferedMessage.get(partialMessage, bufferedMessage.arrayOffset(), msgSize);
			returnList.add(new String(partialMessage));

			bufferedMessage.getChar();
			msgSize = bufferedMessage.getInt();
			partialMessage = new byte[msgSize];
			bufferedMessage.getChar();
			bufferedMessage.get(partialMessage, bufferedMessage.arrayOffset(), msgSize);
			returnList.add(new String(partialMessage));
		}

		return returnList;
	}

	public static byte[] serializeMessage(Object message) {

		Gson gson = new Gson();
		ByteBuffer buff = ByteBuffer.allocate(1024);

		if (message instanceof EngineEvent) {

			System.out.println("An engine event is received and forwarded by server");

			message = (EngineEvent) message;

			buff.putChar('$');
			buff.putInt(101);
			buff.putChar(',');
			buff.putInt(gson.toJson(message).getBytes().length);
			buff.putChar(',');
			buff.put(gson.toJson(message).getBytes());

			return buff.array();

		} else if (message instanceof SeatBeltEvent) {

			System.out.println("A seat belt event is received and forwarded by server");

			message = (SeatBeltEvent) message;

			buff.putChar('$');
			buff.putInt(102);
			buff.putChar(',');
			buff.putInt(gson.toJson(message).getBytes().length);
			buff.putChar(',');
			buff.put(gson.toJson(message).getBytes());

			return buff.array();

		} else if (message instanceof LocationEvent) {

			System.out.println("A location event is received and forwarded by server");

			message = (LocationEvent) message;

			buff.putChar('$');
			buff.putInt(100);
			buff.putChar(',');
			buff.putInt(gson.toJson(message).getBytes().length);
			buff.putChar(',');
			buff.put(gson.toJson(message).getBytes());

			return buff.array();

		} else if (message instanceof String) {

			buff.putChar('#');
			buff.putInt(((String) message).getBytes().length);
			buff.putChar(',');
			buff.put(((String) message).getBytes());

			return buff.array();

		} else if (message instanceof UserLog) {

			String deviceName = ((UserLog) message).getUserID();
			String timeStamp = ((UserLog) message).getTimeStamp();
			ConnectionStatus status = ((UserLog) message).getTransaction();

			if (status.equals(ConnectionStatus.ONLINE)) {

				buff.putChar('}');

			} else {

				buff.putChar('{');
			}

			buff.putInt(deviceName.getBytes().length);
			buff.putChar(',');
			buff.put(deviceName.getBytes());
			buff.putChar(',');
			buff.putInt(timeStamp.getBytes().length);
			buff.putChar(',');
			buff.put(timeStamp.getBytes());

			return buff.array();

		}

		System.out.println("message returned null");

		return null;
	}

	public static byte[] serializeQueryMessage(List<Object> queryResult) {

		byte[] returnArray = new byte[1024];

		ByteBuffer buff = ByteBuffer.wrap(returnArray);

		buff.putChar('*');

		QueryExitStatus queryExitStatus = (QueryExitStatus) queryResult.get(0);
		System.out.println(queryExitStatus);
		buff.putInt(queryExitStatus.ordinal());

		if (queryExitStatus.equals(QueryExitStatus.VALID_PASSWORD)) {

		} else if (queryExitStatus.equals(QueryExitStatus.INVALID_PASSWORD)) {

		} else if (queryExitStatus.equals(QueryExitStatus.USER_NOT_FOUND)) {

		} else if (queryExitStatus.equals(QueryExitStatus.PAIR_ADDED)) {

			String client = (String) queryResult.get(1);
			String device = (String) queryResult.get(2);

			buff.putChar(',');
			buff.putInt(device.getBytes().length);
			buff.putChar(',');
			buff.put(device.getBytes());
			buff.putChar(',');
			buff.putInt(client.getBytes().length);
			buff.putChar(',');
			buff.put(client.getBytes());

		} else if (queryExitStatus.equals(QueryExitStatus.PAIR_REMOVED)) {

			String client = (String) queryResult.get(1);
			String device = (String) queryResult.get(2);

			buff.putChar(',');
			buff.putInt(device.getBytes().length);
			buff.putChar(',');
			buff.put(device.getBytes());
			buff.putChar(',');
			buff.putInt(client.getBytes().length);
			buff.putChar(',');
			buff.put(client.getBytes());

		} else if (queryExitStatus.equals(QueryExitStatus.DEVICE_ADDED)) {

			UserWrapper wrappedUser = new UserWrapper((User) queryResult.get(1));
			String username = wrappedUser.getUserID();
			String dateAdded = wrappedUser.getDateAdded().toString();
			String userSpecific = wrappedUser.getUserSpecificFields();

			buff.putChar(',');
			buff.putInt(username.getBytes().length);
			buff.putChar(',');
			buff.put(username.getBytes());
			buff.putChar(',');
			buff.putInt(dateAdded.getBytes().length);
			buff.putChar(',');
			buff.put(dateAdded.getBytes());
			buff.putChar(',');
			buff.putInt(userSpecific.getBytes().length);
			buff.putChar(',');
			buff.put(userSpecific.getBytes());

		} else if (queryExitStatus.equals(QueryExitStatus.DEVICE_REMOVED)
				|| queryExitStatus.equals(QueryExitStatus.CLIENT_REMOVED)) {

			String username = (String) queryResult.get(1);

			buff.putChar(',');
			buff.putInt(username.getBytes().length);
			buff.putChar(',');
			buff.put(username.getBytes());

		} else if (queryExitStatus.equals(QueryExitStatus.CLIENT_ADDED)) {

			UserWrapper user = new UserWrapper((User) queryResult.get(1));
			String device = user.getUserID();
			String timeStamp = user.getDateAdded().toString();
			String userSpecific = user.getUserSpecificFields();

			buff.putChar(',');
			buff.putInt(device.getBytes().length);
			buff.putChar(',');
			buff.put(device.getBytes());
			buff.putChar(',');
			buff.putInt(timeStamp.getBytes().length);
			buff.putChar(',');
			buff.put(timeStamp.getBytes());
			buff.putChar(',');
			buff.putInt(userSpecific.getBytes().length);
			buff.putChar(',');
			buff.put(userSpecific.getBytes());

		} else if (queryExitStatus.equals((QueryExitStatus.PAIR_DOES_NOT_EXIST))) {

		} else if (queryExitStatus.equals((QueryExitStatus.PAIR_EXISTS))) {

		}

		return buff.array();
	}

	public static byte[] serializeUser(User user) {

		UserWrapper wrappedUser = new UserWrapper(user);
		ByteBuffer buffer = ByteBuffer.allocate(1024);

		String userID = wrappedUser.getUserID();
		String userSpecific = wrappedUser.getUserSpecificFields();
		String dateAdded = wrappedUser.getDateAdded().toString();

		buffer.putChar('>');
		buffer.putInt(user.getType().ordinal());
		buffer.putChar(',');
		buffer.putInt(userID.getBytes().length);
		buffer.putChar(',');
		buffer.put(userID.getBytes());
		buffer.putChar(',');
		buffer.putInt(userSpecific.getBytes().length);
		buffer.putChar(',');
		buffer.put(userSpecific.getBytes());
		buffer.putChar(',');
		buffer.putInt(dateAdded.getBytes().length);
		buffer.putChar(',');
		buffer.put(dateAdded.getBytes());

		return buffer.array();

	}

	public static User deserializeUser(byte[] message) {

		int msgSize;
		byte[] partialMessage;
		String userID;
		String userSpecific;
		LocalDateTime dateAdded;

		ByteBuffer bufferedMessage = ByteBuffer.wrap(message);
		bufferedMessage.getChar();

		UserType userType = UserType.values()[bufferedMessage.getInt()];
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
		userSpecific = new String(partialMessage);
		bufferedMessage.getChar();

		msgSize = bufferedMessage.getInt();
		bufferedMessage.getChar();

		partialMessage = new byte[msgSize];
		bufferedMessage.get(partialMessage, bufferedMessage.arrayOffset(), msgSize);
		dateAdded = LocalDateTime.parse(new String(partialMessage));

		if (userType.equals(UserType.Client)) {

			return new ClientUser(userSpecific, "-", userID, dateAdded);

		} else if (userType.equals(UserType.Device)) {

			return new DeviceUser(userSpecific, "-", userID, dateAdded);
		}

		return null;
	}
}
