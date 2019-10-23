package com.ekinoks.followme.commserver.model;

import java.util.List;

public interface IServerDatabaseCommunicationHandler {

	List<Object> checkPasswordMatchDevice(String enteredUsername, String enteredPassword);
}
