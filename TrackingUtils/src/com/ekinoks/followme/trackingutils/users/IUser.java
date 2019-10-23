package com.ekinoks.followme.trackingutils.users;

import java.time.LocalDateTime;

public interface IUser {

	String getUserID();

	LocalDateTime getDateAdded();

	UserType getUserType();
}
