package com.ekinoks.followme.commserver.model;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.ekinoks.followme.trackingutils.databaseops.QueryExitStatus;
import com.ekinoks.followme.trackingutils.databaseops.SQLStatements;
import com.ekinoks.followme.trackingutils.pbkdf2.ISecurePassword;
import com.ekinoks.followme.trackingutils.users.ClientUser;
import com.ekinoks.followme.trackingutils.users.DeviceUser;
import com.ekinoks.followme.trackingutils.users.User;

public class ServerDatabaseCommunicationHandler implements IServerDatabaseCommunicationHandler {

	public ServerDatabaseCommunicationHandler() {

	}

	private Connection connectToDatabase() {

		String url = "jdbc:postgresql://127.0.0.1:5432/followme";
		String username = "postgres";
		String password = "followme12345";

		try {

			return DriverManager.getConnection(url, username, password);

		} catch (SQLException e) {

			e.printStackTrace();
			return null;
		}
	}

	public ArrayList<String[]> getPairedUsers() {

		ArrayList<String[]> allPairs = new ArrayList<String[]>();

		try (Connection connection = connectToDatabase();
				PreparedStatement preparedStatement = connection.prepareStatement(SQLStatements.getAllPairs,
						Statement.RETURN_GENERATED_KEYS)) {

			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next() == true) {

				String[] pair = new String[2];
				pair[0] = resultSet.getString("device");
				pair[1] = resultSet.getString("client");
				allPairs.add(pair);
			}

			return allPairs;

		} catch (SQLException e) {

			e.printStackTrace();

			return null;
		}
	}

	private QueryExitStatus checkPairExists(String client, String device) {

		try (Connection connection = connectToDatabase();
				PreparedStatement preparedStatement = connection.prepareStatement(SQLStatements.checkPairExists,
						Statement.RETURN_GENERATED_KEYS)) {

			preparedStatement.setString(1, client);
			preparedStatement.setString(2, device);
			ResultSet resultSet = preparedStatement.executeQuery();

			if (resultSet.next() == true) {

				return QueryExitStatus.PAIR_EXISTS;

			} else {

				return QueryExitStatus.PAIR_DOES_NOT_EXIST;
			}
		} catch (SQLException e) {

			return QueryExitStatus.DATABASE_FAIL;
		}
	}

	public List<Object> addPair(String device, String client) {

		ArrayList<Object> ret = new ArrayList<Object>();

		QueryExitStatus checkPair = checkPairExists(client, device);

		if (checkPair.equals(QueryExitStatus.PAIR_EXISTS)) {

			ret.add(QueryExitStatus.PAIR_EXISTS);

		} else if (checkPair.equals(QueryExitStatus.PAIR_DOES_NOT_EXIST)) {

			try (Connection connection = connectToDatabase();
					PreparedStatement preparedStatement = connection.prepareStatement(SQLStatements.addPair,
							Statement.RETURN_GENERATED_KEYS)) {

				preparedStatement.setString(1, client);
				preparedStatement.setString(2, device);
				preparedStatement.execute();

				ret.add(QueryExitStatus.PAIR_ADDED);
				ret.add(client);
				ret.add(device);

			} catch (SQLException e) {
				e.printStackTrace();
				ret.add(QueryExitStatus.DATABASE_FAIL);
			}

		} else {

			ret.add(QueryExitStatus.FAIL);
		}

		return ret;
	}

	public List<Object> deleteClientsAllPair(String client) {

		ArrayList<Object> ret = new ArrayList<Object>();

		try (Connection connection = connectToDatabase();
				PreparedStatement preparedStatement = connection.prepareStatement(SQLStatements.deleteClientsPair,
						Statement.RETURN_GENERATED_KEYS)) {

			preparedStatement.setString(1, client);
			int result = preparedStatement.executeUpdate();

			if (result != 0) {

				ret.add(QueryExitStatus.USERS_PAIRS_REMOVED);
				ret.add(client);

			} else {

				ret.add(QueryExitStatus.PAIR_DOES_NOT_EXIST);
			}
		} catch (SQLException e) {

			e.printStackTrace();
			ret.add(QueryExitStatus.DATABASE_FAIL);
		}

		return ret;
	}

	public List<Object> deleteDevicesAllPair(String device) {

		ArrayList<Object> ret = new ArrayList<Object>();

		try (Connection connection = connectToDatabase();
				PreparedStatement preparedStatement = connection.prepareStatement(SQLStatements.deleteDevicesPair,
						Statement.RETURN_GENERATED_KEYS)) {

			preparedStatement.setString(1, device);
			int result = preparedStatement.executeUpdate();

			if (result != 0) {

				ret.add(QueryExitStatus.USERS_PAIRS_REMOVED);
				ret.add(device);

			} else {

				ret.add(QueryExitStatus.FAIL);
			}
		} catch (SQLException e) {

			e.printStackTrace();
			ret.add(QueryExitStatus.DATABASE_FAIL);
		}

		return ret;

	}

	public List<Object> deletePair(String device, String client) {

		ArrayList<Object> ret = new ArrayList<Object>();

		QueryExitStatus checkPair = checkPairExists(client, device);

		if (checkPair.equals(QueryExitStatus.PAIR_DOES_NOT_EXIST)) {

			ret.add(QueryExitStatus.PAIR_DOES_NOT_EXIST);

		} else if (checkPair.equals(QueryExitStatus.PAIR_EXISTS)) {

			try (Connection connection = connectToDatabase();
					PreparedStatement preparedStatement = connection.prepareStatement(SQLStatements.deletePair,
							Statement.RETURN_GENERATED_KEYS)) {

				preparedStatement.setString(1, client);
				preparedStatement.setString(2, device);
				preparedStatement.executeUpdate();

				ret.add(QueryExitStatus.PAIR_REMOVED);
				ret.add(client);
				ret.add(device);

			} catch (SQLException e) {

				e.printStackTrace();
				ret.add(QueryExitStatus.DATABASE_FAIL);
			}
		}
		return ret;
	}

	public List<Object> addUser(User user) {

		ArrayList<Object> ret = new ArrayList<Object>();

		if (checkUserExists(user).equals(QueryExitStatus.USER_NOT_FOUND)) {

			LocalDateTime now = LocalDateTime.now();

			if (user instanceof DeviceUser) {

				try (Connection connection = connectToDatabase();

						PreparedStatement preparedStatement = connection.prepareStatement(SQLStatements.addDevice,
								Statement.RETURN_GENERATED_KEYS);) {

					preparedStatement.setString(1, user.getUserID());
					preparedStatement.setString(2, user.getPassword());
					preparedStatement.setString(3, ((DeviceUser) user).getManufacturer());
					preparedStatement.setObject(4, now);
					preparedStatement.execute();

					ret.add(QueryExitStatus.DEVICE_ADDED);
					user.setDateAdded(now);
					ret.add(user);

				} catch (SQLException e) {

					e.printStackTrace();
					ret.add(QueryExitStatus.DATABASE_FAIL);
				}

			}

			else if (user instanceof ClientUser) {

				try (Connection connection = connectToDatabase();

						PreparedStatement preparedStatement = connection.prepareStatement(SQLStatements.addClient,
								Statement.RETURN_GENERATED_KEYS);) {

					preparedStatement.setString(1, user.getUserID());
					preparedStatement.setString(2, user.getPassword());
					preparedStatement.setString(3, ((ClientUser) user).getUserName());
					preparedStatement.setObject(4, now);
					preparedStatement.execute();

					ret.add(QueryExitStatus.CLIENT_ADDED);
					user.setDateAdded(now);
					ret.add(user);

				} catch (SQLException e) {

					e.printStackTrace();
					ret.add(QueryExitStatus.DATABASE_FAIL);
				}
			}

			else {

				ret.add(QueryExitStatus.FAIL);
			}

		}

		else {

			ret.add(QueryExitStatus.USER_ID_IN_USE);
		}

		return ret;
	}

	public int getDeviceCount() {

		int count = 0;

		try (Connection connection = connectToDatabase();

				Statement statement = connection.createStatement();

				ResultSet resultSet = statement.executeQuery(SQLStatements.getDeviceCount)) {

			resultSet.next();
			count = resultSet.getInt(1);

		} catch (SQLException e) {

			e.printStackTrace();

		}

		return count;
	}

	public int getClientCount() {

		int count = 0;

		try (Connection connection = connectToDatabase();

				Statement statement = connection.createStatement();

				ResultSet resultSet = statement.executeQuery(SQLStatements.getClientCount)) {

			resultSet.next();
			count = resultSet.getInt(1);

		} catch (SQLException e) {

			e.printStackTrace();

		}

		return count;
	}

	private QueryExitStatus checkUserExists(User user) {

		if (user instanceof ClientUser) {

			try (Connection connection = connectToDatabase();
					PreparedStatement preparedStatement = connection
							.prepareStatement(SQLStatements.checkClientExists)) {

				preparedStatement.setString(1, user.getUserID());
				ResultSet resultSet = preparedStatement.executeQuery();

				if (resultSet.next() == true) {

					return QueryExitStatus.USER_ID_IN_USE;
				}

				else {

					return QueryExitStatus.USER_NOT_FOUND;
				}

			} catch (SQLException e) {

				e.printStackTrace();
			}

		} else if (user instanceof DeviceUser) {

			try (Connection connection = connectToDatabase();
					PreparedStatement preparedStatement = connection
							.prepareStatement(SQLStatements.checkDeviceExists)) {

				preparedStatement.setString(1, user.getUserID());
				ResultSet resultSet = preparedStatement.executeQuery();

				if (resultSet.next() == true) {

					return QueryExitStatus.USER_ID_IN_USE;

				} else {

					return QueryExitStatus.USER_NOT_FOUND;
				}

			} catch (SQLException e) {

				e.printStackTrace();
			}

			return QueryExitStatus.FAIL;

		}

		return QueryExitStatus.FAIL;

	}

	public List<Object> deleteUser(User user) {

		ArrayList<Object> ret = new ArrayList<Object>();

		if (user instanceof DeviceUser) {

			if (checkUserExists(user) == QueryExitStatus.USER_ID_IN_USE) {

				try (Connection connection = connectToDatabase();
						PreparedStatement preparedStatement = connection.prepareStatement(SQLStatements.deleteDevice)) {

					preparedStatement.setString(1, user.getUserID());
					preparedStatement.executeUpdate();
					ret.add(QueryExitStatus.DEVICE_REMOVED);
					ret.add(user.getUserID());

				} catch (SQLException e) {

					e.printStackTrace();
					ret.add(QueryExitStatus.FAIL);
				}

			} else {

				ret.add(QueryExitStatus.USER_NOT_FOUND);
			}

		} else if (user instanceof ClientUser) {

			if (checkUserExists(user).equals(QueryExitStatus.USER_ID_IN_USE)) {

				try (Connection connection = connectToDatabase();
						PreparedStatement preparedStatement = connection.prepareStatement(SQLStatements.deleteClient)) {

					preparedStatement.setString(1, user.getUserID());
					preparedStatement.executeUpdate();
					ret.add(QueryExitStatus.CLIENT_REMOVED);
					ret.add(user.getUserID());

				} catch (SQLException e) {

					e.printStackTrace();
					ret.add(QueryExitStatus.FAIL);
				}

			} else {

				ret.add(QueryExitStatus.USER_NOT_FOUND);
			}
		} else {

			ret.add(QueryExitStatus.FAIL);
		}

		return ret;
	}

	public List<DeviceUser> getDevices() {

		List<DeviceUser> allDevices = new ArrayList<DeviceUser>();

		try (Connection connection = connectToDatabase();
				PreparedStatement preparedStatement = connection.prepareStatement(SQLStatements.getAllDevices,
						Statement.RETURN_GENERATED_KEYS)) {

			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next() == true) {

				String device = resultSet.getString("device_id");
				String manufacturer = resultSet.getString("device_manufacturer");
				LocalDateTime dateAdded = resultSet.getTimestamp("date_added").toLocalDateTime();
				allDevices.add(new DeviceUser(manufacturer, "", device, dateAdded));
			}

			return allDevices;

		} catch (SQLException e) {

			return null;
		}
	}

	public List<ClientUser> getClients() {

		List<ClientUser> allDevices = new ArrayList<ClientUser>();

		try (Connection connection = connectToDatabase();
				PreparedStatement preparedStatement = connection.prepareStatement(SQLStatements.getAllClients,
						Statement.RETURN_GENERATED_KEYS)) {

			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next() == true) {

				String client = resultSet.getString("client_id");
				String name = resultSet.getString("client_name");
				LocalDateTime dateAdded = resultSet.getTimestamp("date_added").toLocalDateTime();
				allDevices.add(new ClientUser(name, "", client, dateAdded));
			}

			return allDevices;

		} catch (SQLException e) {

			return null;
		}
	}

	public List<Object> checkPasswordMatchDevice(String enteredUsername, String enteredPassword) {

		ArrayList<Object> ret = new ArrayList<Object>();

		QueryExitStatus userExistenceStatus = checkUserExistsDevice(enteredUsername);

		if (userExistenceStatus.equals(QueryExitStatus.USER_ID_IN_USE)) {

			try (Connection connection = connectToDatabase();
					PreparedStatement preparedStatement = connection.prepareStatement(SQLStatements.fetchDevicePassword,
							Statement.RETURN_GENERATED_KEYS)) {

				preparedStatement.setString(1, enteredUsername);
				ResultSet resultSet = preparedStatement.executeQuery();

				resultSet.next();

				String storedPassword = resultSet.getString(1);
				try {

					if (ISecurePassword.validateSecurePassword(enteredPassword, storedPassword)) {

						ret.add(QueryExitStatus.VALID_PASSWORD);

					} else {

						ret.add(QueryExitStatus.INVALID_PASSWORD);
					}

				} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {

					e.printStackTrace();
					ret.add(QueryExitStatus.FAIL);
				}
			} catch (SQLException e) {

				e.printStackTrace();
				ret.add(QueryExitStatus.DATABASE_FAIL);
			}

		} else if (userExistenceStatus.equals(QueryExitStatus.USER_NOT_FOUND)) {

			ret.add(QueryExitStatus.USER_NOT_FOUND);

		} else if (userExistenceStatus.equals(QueryExitStatus.DATABASE_FAIL)) {

			ret.add(QueryExitStatus.DATABASE_FAIL);

		} else {

			ret.add(QueryExitStatus.FAIL);
		}

		return ret;
	}

	public List<Object> checkPasswordMatchClient(String enteredUsername, String enteredPassword) {

		ArrayList<Object> ret = new ArrayList<Object>();

		QueryExitStatus userExistenceStatus = checkUserExistsClient(enteredUsername);

		if (userExistenceStatus.equals(QueryExitStatus.USER_ID_IN_USE)) {

			try (Connection connection = connectToDatabase();
					PreparedStatement preparedStatement = connection.prepareStatement(SQLStatements.fetchClientPassword,
							Statement.RETURN_GENERATED_KEYS)) {

				preparedStatement.setString(1, enteredUsername);
				ResultSet resultSet = preparedStatement.executeQuery();

				resultSet.next();

				String storedPassword = resultSet.getString(1);
				try {

					if (ISecurePassword.validateSecurePassword(enteredPassword, storedPassword)) {

						ret.add(QueryExitStatus.VALID_PASSWORD);

					} else {

						ret.add(QueryExitStatus.INVALID_PASSWORD);
					}

				} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {

					e.printStackTrace();
					ret.add(QueryExitStatus.FAIL);
				}
			} catch (SQLException e) {

				e.printStackTrace();
				ret.add(QueryExitStatus.DATABASE_FAIL);
			}

		} else if (userExistenceStatus.equals(QueryExitStatus.USER_NOT_FOUND)) {

			ret.add(QueryExitStatus.USER_NOT_FOUND);

		} else if (userExistenceStatus.equals(QueryExitStatus.DATABASE_FAIL)) {

			ret.add(QueryExitStatus.DATABASE_FAIL);

		} else {

			ret.add(QueryExitStatus.FAIL);
		}

		return ret;
	}

	private QueryExitStatus checkUserExistsDevice(String username) {

		try (Connection connection = connectToDatabase();
				PreparedStatement preparedStatement = connection.prepareStatement(SQLStatements.checkDeviceExists,
						Statement.RETURN_GENERATED_KEYS)) {

			preparedStatement.setString(1, username);
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next() == true) {

				return QueryExitStatus.USER_ID_IN_USE;

			} else {

				return QueryExitStatus.USER_NOT_FOUND;
			}

		} catch (SQLException e) {

			e.printStackTrace();
			return QueryExitStatus.DATABASE_FAIL;
		}
	}

	public QueryExitStatus checkUserExistsClient(String username) {

		try (Connection connection = connectToDatabase();
				PreparedStatement preparedStatement = connection.prepareStatement(SQLStatements.checkClientExists,
						Statement.RETURN_GENERATED_KEYS)) {

			preparedStatement.setString(1, username);
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next() == true) {

				return QueryExitStatus.USER_ID_IN_USE;

			} else {

				return QueryExitStatus.USER_NOT_FOUND;
			}

		} catch (SQLException e) {

			e.printStackTrace();
			return QueryExitStatus.DATABASE_FAIL;
		}
	}
}
