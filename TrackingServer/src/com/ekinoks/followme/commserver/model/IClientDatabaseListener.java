package com.ekinoks.followme.commserver.model;

public interface IClientDatabaseListener {

	public void onGetPairUpdates();

	public void onGetClientUpdates(String username, int transactionCode);
}
