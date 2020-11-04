package com.rohan.addressbookjdbc;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.rohan.addressbookjdbc.AddressBookFileService.IOService;

class AddressBookFileServiceTest {
	private static AddressBookFileService addressBookFileService;
	private static List<Person> contactData;

	@BeforeAll
	static void setUp() throws DatabaseException {
		addressBookFileService = new AddressBookFileService();
		contactData = addressBookFileService.readContactData(IOService.DB_IO);
	}

	/**
	 * checking if Retrieved data from the database
	 * 
	 * @throws DatabaseException
	 */
	@Test
	public void givenContactDataInDB_WhenRetrieved_ShouldMatchContactCount() throws DatabaseException {
		assertEquals(10, contactData.size());
	}

	/**
	 * checking Updated info of a persons is in sync with database
	 * 
	 * @throws DatabaseException
	 * @throws SQLException
	 */
	@Test
	public void givenNewDataForContact_WhenUpdated_ShouldBeInSync() throws DatabaseException {
		addressBookFileService.updatePersonsPhone("abc bca", 0000000000);
		addressBookFileService.readContactData(IOService.DB_IO);
		boolean result = addressBookFileService.checkContactDataSync("abc bca");
		assertEquals(true, result);
	}

	/**
	 * checking if the getContactsByDate() method returns list of persons added
	 * between given dates
	 * 
	 * @throws DatabaseException
	 */
	@Test
	public void givenContactDataInDB_WhenRetrieved_ShouldMatchContactAddedInGivenDateRangeCount()
			throws DatabaseException {
		List<Person> contactByDateList = null;
		LocalDate start = LocalDate.of(2018, 8, 10);
		LocalDate end = LocalDate.now();
		contactByDateList = addressBookFileService.getContactsByDate(start, end);
		assertEquals(4, contactByDateList.size());
	}

	/**
	 * checking if we get the list of contacts belonging to city or state
	 * 
	 * @throws DatabaseException
	 */
	@Test
	public void givenContactDataInDB_WhenRetrievedByCityOrState_ShouldMatchContactInStateOrCityCount()
			throws DatabaseException {
		List<Person> contactByCity = addressBookFileService.getContactsByCity("Pune");
		List<Person> contactByState = addressBookFileService.getContactsByState("Goa");
		assertEquals(1, contactByCity.size());
		assertEquals(1, contactByState.size());
	}

	/**
	 * adding new contact and checking if in sync
	 * 
	 * @throws DatabaseException
	 * @throws SQLException
	 */
	@Test
	public void givenNewContact_WhenAdded_ShouldSincWithDB() throws DatabaseException, SQLException {
		addressBookFileService.addNewContact("Raghav", "Soni", "Nashik", "Nashik", "Nashik", 758452, 1234567891,
				"raghavsoni@gmail.com", Arrays.asList("AddressBook1", "AddressBook3"));
		contactData = addressBookFileService.readContactData(IOService.DB_IO);
		boolean result = addressBookFileService.checkContactDataSync("Raghav Soni");
		assertTrue(result);
	}
}
