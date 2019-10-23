package com.ekinoks.followme.commserver.datagenerator;

import java.time.LocalDateTime;

import com.ekinoks.followme.commserver.model.ServerDatabaseCommunicationHandler;
import com.ekinoks.followme.trackingutils.pbkdf2.ISecurePassword;
import com.ekinoks.followme.trackingutils.users.ClientUser;
import com.ekinoks.followme.trackingutils.users.DeviceUser;

public class Main {

	public static void main(String[] args) {

		ServerDatabaseCommunicationHandler serverDatabaseCommunicationHandler = new ServerDatabaseCommunicationHandler();

		char[] client = new char[5];
		client[0] = 'C';
		client[1] = 'L';

		char[] device = new char[5];
		device[0] = 'E';
		device[1] = 'K';
		String password = ISecurePassword.createSecurePassword("1");

		for (int i = 0; i < 10; i++) {

			for (int j = 0; j < 10; j++) {

				for (int k = 0; k < 10; k++) {

					client[2] = (char) (i + 48);
					device[2] = (char) (i + 48);
					client[3] = (char) (j + 48);
					device[3] = (char) (j + 48);
					client[4] = (char) (k + 48);
					device[4] = (char) (k + 48);

					System.out.println(new String(client) + " : " + new String(device));
					serverDatabaseCommunicationHandler
							.addUser(new ClientUser("Necati", password, new String(client), LocalDateTime.now()));

					serverDatabaseCommunicationHandler
							.addUser(new DeviceUser("Ekinoks", password, new String(device), LocalDateTime.now()));

				}
			}
		}
	}
}
