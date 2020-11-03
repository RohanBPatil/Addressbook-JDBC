package com.rohan.addressbookjdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mysql.jdbc.Statement;

public class AddressbookDBService {
	private static AddressbookDBService addressBookDB;

	public AddressbookDBService() {
	}

	public static AddressbookDBService getInstance() {
		if (addressBookDB == null) {
			addressBookDB = new AddressbookDBService();
		}
		return addressBookDB;
	}

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

	public List<Person> readData() throws DatabaseException {
		String sql = "select contacts_table.contactId, addressbook_table.addressbookName, addressbookTypes.type, contacts_table.firstName, contacts_table.lastName, \r\n"
				+ "contacts_table.address, zipCityState.city, zipCityState.state, contacts_table.zip, contacts_table.phone, contacts_table.email\r\n"
				+ "from contacts_table\r\n"
				+ "inner join zipCityState on contacts_table.zip = zipCityState.zip\r\n"
				+ "inner join addressbook_table on contacts_table.contactId = addressbook_table.contactId\r\n"
				+ "inner join addressbookTypes on addressbookTypes.addressbookName = addressbook_table.addressbookName;";
		return this.getContactData(sql);
	}

	private List<Person> getContactData(String sql) throws DatabaseException {
		List<Person> contactList = new ArrayList<>();
		try (Connection connection = this.getConnection()) {
			Statement statement = (Statement) connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
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
				contactList.add(new Person(contactId, fname, lname, address, city, state, zip, phoneNumber, email,
						addbookName, type));
			}
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage());
		}
		return contactList;
	}

}
