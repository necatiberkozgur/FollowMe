package com.ekinoks.followme.deviceadmin.databaseops;

public interface IDatabaseOpsHandler {

	static DatabaseOpsHandler createInstance() {

		return new DatabaseOpsHandler();
	}

	String getUsernameAdmin();

	String getPasswordAdmin();

}
