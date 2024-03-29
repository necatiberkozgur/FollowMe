package com.ekinoks.followme.trackingutils.databaseops;

public enum QueryExitStatus {

	SUCCESS, FAIL, USER_ID_IN_USE, USER_NOT_FOUND, DATABASE_FAIL, INVALID_PASSWORD, VALID_PASSWORD, PAIR_DOES_NOT_EXIST,
	PAIR_EXISTS, CLIENT_ADDED, CLIENT_REMOVED, DEVICE_ADDED, DEVICE_REMOVED, PAIR_ADDED, PAIR_REMOVED,
	USERS_PAIRS_REMOVED
}
