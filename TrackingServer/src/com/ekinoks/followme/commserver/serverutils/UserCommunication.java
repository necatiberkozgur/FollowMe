package com.ekinoks.followme.commserver.serverutils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

import com.ekinoks.followme.trackingutils.communication.Serialization;

public class UserCommunication {

	public static void sendQueryMessage(Socket socket, List<Object> result) {

		if (socket != null && socket.isClosed() == false) {

			OutputStream outputStream = null;

			try {

				outputStream = socket.getOutputStream();

				outputStream.write(Serialization.serializeQueryMessage(result));

				System.out.println("message sent to user");

			} catch (IOException e) {

				e.printStackTrace();
			}
		}
	}
}
