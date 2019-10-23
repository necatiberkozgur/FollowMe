package com.ekinoks.followme.commserver.serverutils;

import java.util.List;

import com.ekinoks.followme.commserver.model.ServerDatabaseCommunicationHandler;
import com.ekinoks.followme.trackingutils.users.User;

public class DatabaseCommunicator {

	public static List<Object> processDatabaseOperation(int opCode, List<Object> args) {

		ServerDatabaseCommunicationHandler serverDatabaseCommunicationHandler = new ServerDatabaseCommunicationHandler();

		if (opCode == 0) {

			return serverDatabaseCommunicationHandler.addUser((User) args.get(0));

		} else if (opCode == 1) {

			return serverDatabaseCommunicationHandler.deleteUser((User) args.get(0));

		} else if (opCode == 2) {

		} else if (opCode == 3) {

		} else if (opCode == 4) {

			return serverDatabaseCommunicationHandler.addPair((String) args.get(0), (String) args.get(1));

		} else if (opCode == 5) {

			return serverDatabaseCommunicationHandler.deletePair((String) args.get(0), (String) args.get(1));

		} else if (opCode == 6) {

			return serverDatabaseCommunicationHandler.deleteDevicesAllPair((String) args.get(0));

		} else if (opCode == 7) {

			return serverDatabaseCommunicationHandler.deleteClientsAllPair((String) args.get(0));

		} else if (opCode == 8) {

		} else if (opCode == 9) {

		} else if (opCode == 10) {

		} else if (opCode == 11) {

			return serverDatabaseCommunicationHandler.checkPasswordMatchDevice((String) args.get(0),
					(String) args.get(1));

		} else if (opCode == 12) {

			return serverDatabaseCommunicationHandler.checkPasswordMatchClient((String) args.get(0),
					(String) args.get(1));
		} else {

			System.out.println("else ");
		}
		return null;
	}

}
