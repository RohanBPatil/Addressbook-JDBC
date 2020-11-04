package com.rohan.addressbookjdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;

public class AddressbookDBService {
	private static AddressbookDBService addressBookDB;
	private static PreparedStatement contactPrepareStatement;

	private AddressbookDBService() {
	}

	public static AddressbookDBService getInstance() {
		if (addressBookDB == null) {
			addressBookDB = new AddressbookDBService();
		}
		return addressBookDB;
	}

	/**
	 * getting connection established with database
	 * 
	 * @return
	 * @throws DatabaseException
	 */
	private Connection getConnection() throws DatabaseException {
		String jdbcURL = "jdbc:mysql://localhost:3306/addressBookService?useSSL=false";
		String userName = "root";
		String password = "rpatil";
		Connection connection = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(jdbcURL, userName, password);
		} catch (Exception e) {
			throw new DatabaseException("Connection was unsuccessful");
		}
		return connection;

	}

	/**
	 * reads data from database
	 * 
	 * @return
	 * @throws DatabaseException
	 */
	public List<Person> readData() throws DatabaseException {
		String sql = "select contacts_table.contactId, addressbook_table.addressbookName, addressbookTypes.type, contacts_table.firstName, contacts_table.lastName, \r\n"
				+ "contacts_table.address, zipCityState.city, zipCityState.state, contacts_table.zip, contacts_table.phone, contacts_table.email, contacts_table.dateAdded\r\n"
				+ "from contacts_table\r\n" + "inner join zipCityState on contacts_table.zip = zipCityState.zip\r\n"
				+ "inner join addressbook_table on contacts_table.contactId = addressbook_table.contactId\r\n"
				+ "inner join addressbookTypes on addressbookTypes.addressbookName = addressbook_table.addressbookName;";
		return this.getContactData(sql);
	}

	/**
	 * when sql query passed, returns list of persons
	 * 
	 * @param sql
	 * @return
	 * @throws DatabaseException
	 */
	private List<Person> getContactData(String sql) throws DatabaseException {
		List<Person> contactList = new ArrayList<>();
		try (Connection connection = this.getConnection()) {
			Statement statement = (Statement) connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			contactList = this.getData(resultSet);
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage());
		}
		return contactList;
	}

	/**
	 * when result set passed returns list of persons
	 * 
	 * @param resultSet
	 * @return
	 * @throws SQLException
	 */
	private List<Person> getData(ResultSet resultSet) throws SQLException {
		List<Person> contactList = new ArrayList<>();
		while (resultSet.next()) {
			int contactId = resultSet.getInt("contactId");
			String fname = resultSet.getString("firstName");
			String lname = resultSet.getString("lastName");
			String address = resultSet.getString("address");
			int zip = resultSet.getInt("zip");
			String city = resultSet.getString("city");
			String state = resultSet.getString("state");
			long phoneNumber = resultSet.getLong("phone");
			String email = resultSet.getString("email");
			String addbookName = resultSet.getString("addressbookName");
			String type = resultSet.getString("type");
			LocalDate dateAdded = resultSet.getDate("dateAdded").toLocalDate();
			contactList.add(new Person(contactId, fname, lname, address, city, state, zip, phoneNumber, email,
					addbookName, type, dateAdded));
		}
		return contactList;
	}

	/**
	 * updates person's data
	 * 
	 * @param name
	 * @param phone
	 * @return
	 * @throws DatabaseException
	 */
	@SuppressWarnings("static-access")
	public int updatePersonsData(String name, long phone) throws DatabaseException {
		String sql = "update contacts_table set phone = ? where firstName = ?";
		int result = 0;
		try {
			if (this.contactPrepareStatement == null) {
				Connection connection = this.getConnection();
				contactPrepareStatement = (PreparedStatement) connection.prepareStatement(sql);
			}
			contactPrepareStatement.setLong(1, phone);
			contactPrepareStatement.setString(2, name);
			result = contactPrepareStatement.executeUpdate();
		} catch (Exception e) {
			throw new DatabaseException(e.getMessage());
		}
		return result;
	}

	/**
	 * returns list of persons whose full name matches with input name
	 * 
	 * @param name
	 * @return
	 * @throws DatabaseException
	 */
	public List<Person> getContactFromDatabase(String name) throws DatabaseException {
		String[] fullName = name.split("[ ]");
		String sql = String.format(
				"select contacts_table.contactId, addressbook_table.addressbookName, addressbookTypes.type, contacts_table.firstName, contacts_table.lastName, \r\n"
						+ "contacts_table.address, zipCityState.city, zipCityState.state, contacts_table.zip, contacts_table.phone, contacts_table.email, contacts_table.dateAdded\r\n"
						+ "from contacts_table\r\n"
						+ "inner join zipCityState on contacts_table.zip = zipCityState.zip\r\n"
						+ "inner join addressbook_table on contacts_table.contactId = addressbook_table.contactId\r\n"
						+ "inner join addressbookTypes on addressbookTypes.addressbookName = addressbook_table.addressbookName WHERE firstName = '%s' and lastName = '%s'",
				fullName[0], fullName[1]);
		List<Person> contactList = new ArrayList<>();
		try (Connection connection = this.getConnection()) {
			Statement statement = (Statement) connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			contactList = this.getData(resultSet);
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage());
		}
		return contactList;
	}

	/**
	 * returns list of contacts added between given dates
	 * 
	 * @param start
	 * @param end
	 * @return
	 * @throws DatabaseException
	 */
	public List<Person> readDataForGivenDateRange(LocalDate start, LocalDate end) throws DatabaseException {
		List<Person> contactAddedBetweenDates = this.readData().stream().filter(
				contact -> (contact.getDateAdded().compareTo(start) >= 0 && contact.getDateAdded().compareTo(end) <= 0))
				.distinct().collect(Collectors.toList());
		return contactAddedBetweenDates;
	}

	/**
	 * returns list of contacts belonging to given city
	 * 
	 * @param city
	 * @return
	 * @throws DatabaseException
	 */
	public List<Person> getContactsByCity(String city) throws DatabaseException {
		String sql = String.format(
				"select contacts_table.contactId, addressbook_table.addressbookName, addressbookTypes.type, contacts_table.firstName, contacts_table.lastName, \r\n"
						+ "contacts_table.address, zipCityState.city, zipCityState.state, contacts_table.zip, contacts_table.phone, contacts_table.email, contacts_table.dateAdded\r\n"
						+ "from contacts_table\r\n"
						+ "inner join zipCityState on contacts_table.zip = zipCityState.zip\r\n"
						+ "inner join addressbook_table on contacts_table.contactId = addressbook_table.contactId\r\n"
						+ "inner join addressbookTypes on addressbookTypes.addressbookName = addressbook_table.addressbookName WHERE city = '%s'",
				city);
		return getContactData(sql).stream().distinct().collect(Collectors.toList());
	}

	/**
	 * returns list of contacts belonging to given state
	 * 
	 * @param state
	 * @return
	 * @throws DatabaseException
	 */
	public List<Person> getContactsByState(String state) throws DatabaseException {
		String sql = String.format(
				"select contacts_table.contactId, addressbook_table.addressbookName, addressbookTypes.type, contacts_table.firstName, contacts_table.lastName, \r\n"
						+ "contacts_table.address, zipCityState.city, zipCityState.state, contacts_table.zip, contacts_table.phone, contacts_table.email, contacts_table.dateAdded\r\n"
						+ "from contacts_table\r\n"
						+ "inner join zipCityState on contacts_table.zip = zipCityState.zip\r\n"
						+ "inner join addressbook_table on contacts_table.contactId = addressbook_table.contactId\r\n"
						+ "inner join addressbookTypes on addressbookTypes.addressbookName = addressbook_table.addressbookName WHERE state = '%s'",
				state);
		return getContactData(sql).stream().distinct().collect(Collectors.toList());
	}
}
