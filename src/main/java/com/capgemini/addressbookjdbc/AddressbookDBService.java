package com.capgemini.addressbookjdbc;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
		String sql = "select c.contact_id, a.addressbook_name, t.type, c.first_name, c.last_name, \r\n"
				+ "c.address, z.city, z.state, c.zip, c.phone, c.email, c.date_added\r\n" + "from contacts_table c\r\n"
				+ "inner join zip_city_state z using(zip)\r\n" + "inner join addressbook_table a using(contact_id)\r\n"
				+ "inner join addressbook_types t using(addressbook_name);";
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
			int contactId = resultSet.getInt("contact_id");
			String fname = resultSet.getString("first_name");
			String lname = resultSet.getString("last_name");
			String address = resultSet.getString("address");
			int zip = resultSet.getInt("zip");
			String city = resultSet.getString("city");
			String state = resultSet.getString("state");
			long phoneNumber = resultSet.getLong("phone");
			String email = resultSet.getString("email");
			String addbookName = resultSet.getString("addressbook_name");
			String type = resultSet.getString("type");
			LocalDate dateAdded = resultSet.getDate("date_added").toLocalDate();
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
		String sql = "update contacts_table set phone = ? where first_name = ?";
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
		String sql = String.format("select c.contact_id, a.addressbook_name, t.type, c.first_name, c.last_name, \r\n"
				+ "c.address, z.city, z.state, c.zip, c.phone, c.email, c.date_added\r\n" + "from contacts_table c\r\n"
				+ "inner join zip_city_state z using(zip)\r\n" + "inner join addressbook_table a using(contact_id)\r\n"
				+ "inner join addressbook_types t using(addressbook_name) WHERE first_name = '%s' and last_name = '%s'",
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
		String sql = String.format("select c.contact_id, a.addressbook_name, t.type, c.first_name, c.last_name, \r\n"
				+ "c.address, z.city, z.state, c.zip, c.phone, c.email, c.date_added\r\n" + "from contacts_table c\r\n"
				+ "inner join zip_city_state z using(zip)\r\n" + "inner join addressbook_table a using(contact_id)\r\n"
				+ "inner join addressbook_types t using(addressbook_name) WHERE city = '%s'", city);
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
		String sql = String.format("select c.contact_id, a.addressbook_name, t.type, c.first_name, c.last_name, \r\n"
				+ "c.address, z.city, z.state, c.zip, c.phone, c.email, c.date_added\r\n" + "from contacts_table c\r\n"
				+ "inner join zip_city_state z using(zip)\r\n" + "inner join addressbook_table a using(contact_id)\r\n"
				+ "inner join addressbook_types t using(addressbook_name) WHERE state = '%s'", state);
		return getContactData(sql).stream().distinct().collect(Collectors.toList());
	}

	/**
	 * Adding contact to the address book database and returning added records
	 * 
	 * @param firstName
	 * @param lastName
	 * @param address
	 * @param city
	 * @param state
	 * @param zip
	 * @param phone
	 * @param email
	 * @param addbookName
	 * @param dateAdded
	 * @return
	 * @throws DatabaseException
	 * @throws SQLException
	 */
	@SuppressWarnings("static-access")
	public List<Person> addContactToDatabase(String firstName, String lastName, String address, String city,
			String state, int zip, long phone, String email, List<String> types, LocalDate dateAdded)
			throws DatabaseException, SQLException {
		int contactId = -1;
		Connection connection = null;
		List<Person> addedContacts = new ArrayList<>();
		connection = this.getConnection();
		try {
			connection.setAutoCommit(false);
		} catch (SQLException exception) {
			throw new DatabaseException(exception.getMessage());
		}

		try (Statement statement = (Statement) connection.createStatement()) { // adding to contacts_table
			String sql = String.format(
					"insert into contacts_table (first_name, last_name, address, zip, phone, email, date_added) values ('%s', '%s', '%s', '%s', '%s', '%s', '%s')",
					firstName, lastName, address, zip, phone, email, Date.valueOf(dateAdded));
			int rowAffected = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
			if (rowAffected == 1) {
				ResultSet resultSet = statement.getGeneratedKeys();
				if (resultSet.next())
					contactId = resultSet.getInt(1);
			}
		} catch (SQLException exception) {
			try {
				connection.rollback();
			} catch (SQLException e) {
				throw new DatabaseException(e.getMessage());
			}
			throw new DatabaseException("Unable to add to contacts_table");
		}

		try (Statement statement = (Statement) connection.createStatement()) { // adding to zip_city_state
			String sqlGetZip = String.format("select zip from zip_city_state where zip = %s", zip);
			ResultSet resultSet = statement.executeQuery(sqlGetZip);
			int existingZip = 0;
			while (resultSet.next()) {
				existingZip = resultSet.getInt("zip");
			}
			if (existingZip == 0) { // if zip is not present in zip_city_state then add or skip
				String sql = String.format("insert into zip_city_state (zip, city, state) values ('%s', '%s', '%s')",
						zip, city, state);
				statement.executeUpdate(sql);
			}

		} catch (SQLException exception) {
			try {
				connection.rollback();
			} catch (SQLException e) {
				throw new DatabaseException(e.getMessage());
			}
			throw new DatabaseException("Unable to add to zip_city_state table");
		}

		Map<String, String> typeAddbookNameMap = new HashMap<>();
		try (Statement tempStatement = (Statement) connection.createStatement()) { // getting addressbook types
			String sqlGetType = String.format("select * from addressbook_types");
			ResultSet resultSet = tempStatement.executeQuery(sqlGetType);
			while (resultSet.next()) {
				typeAddbookNameMap.put(resultSet.getString("type"), resultSet.getString("addressbook_name"));
			}
		} catch (Exception e) {
			throw new DatabaseException(e.getMessage());
		}
		final int contId = contactId;
		try (Statement statement = (Statement) connection.createStatement()) { // adding to addressbook_table
			types.forEach(type -> {
				String sql = String.format(
						"insert into addressbook_table (contact_id, addressbook_name) values ('%s', '%s')", contId,
						typeAddbookNameMap.get(type));
				try {
					statement.executeUpdate(sql);
				} catch (SQLException e) {
				}
			});
			types.forEach(type -> addedContacts.add(new Person(contId, firstName, lastName, address, city, state,
					zip, phone, email, typeAddbookNameMap.get(type), type, dateAdded)));

		} catch (SQLException exception) {
			try {
				connection.rollback();
			} catch (SQLException e) {
				throw new DatabaseException(e.getMessage());
			}
			throw new DatabaseException("Unable to add to addressbook_table");
		}

		try {
			connection.commit();
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage());
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
		return addedContacts;
	}
}
