package com.ekinoks.followme.trackingutils.databaseops;

public class SQLStatements {

	public static String addDevice = "INSERT INTO devicedata(device_id, device_password, device_manufacturer, date_added) VALUES(?,?,?,?)";

	public static String addClient = "INSERT INTO clientdata(client_id, client_password, client_name, date_added) VALUES(?,?,?,?)";

	public static String deleteDevice = "DELETE FROM devicedata WHERE device_id = ?";

	public static String deleteClient = "DELETE FROM clientdata WHERE client_id = ?";

	public static String getDeviceCount = "SELECT count(*) FROM devicedata";

	public static String getClientCount = "SELECT count(*) FROM clientdata";

	public static String checkDeviceExists = "SELECT device_id FROM devicedata WHERE device_id = ?";

	public static String fetchDevicePassword = "SELECT device_password FROM devicedata WHERE device_id = ?";

	public static String fetchClientPassword = "SELECT client_password FROM clientdata WHERE client_id = ?";

	public static String checkClientExists = "SELECT client_id FROM clientdata WHERE client_id = ?";

	public static String getAllDevices = "Select * FROM devicedata";

	public static String getAllClients = "Select * FROM clientdata";

	public static String addPair = "INSERT INTO pairs VALUES(?,?)";

	public static String deleteClientsPair = "DELETE FROM pairs WHERE client = ?";

	public static String deleteDevicesPair = "DELETE FROM pairs WHERE device = ?";

	public static String deletePair = "DELETE FROM pairs WHERE client = ? and device = ?";

	public static String getAllPairs = "SELECT * FROM pairs";

	public static String checkPairExists = "SELECT (client, device) FROM pairs WHERE client = ? and device = ?";

	private SQLStatements() {

	}
}
