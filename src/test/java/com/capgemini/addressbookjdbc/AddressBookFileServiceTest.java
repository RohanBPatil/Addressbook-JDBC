package com.capgemini.addressbookjdbc;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.capgemini.addressbookjdbc.AddressBookFileService.IOService;

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
		assertEquals(15, contactData.size());
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
		assertEquals(8, contactByDateList.size());
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
	 * adding new contact and checking if it is in sync
	 * 
	 * @throws DatabaseException
	 * @throws SQLException
	 */
	@Test
	public void givenNewContact_WhenAdded_ShouldSincWithDB() throws DatabaseException {
		addressBookFileService.addNewContact("Ratan", "Tata", "Shirdi", "Nashik", "Nashik", 758458, 1134567800,
				"ratantata@gmail.com", Arrays.asList("family", "profession"));
		contactData = addressBookFileService.readContactData(IOService.DB_IO);
		boolean result = addressBookFileService.checkContactDataSync("Ratan Tata");
		assertTrue(result);
	}

	/**
	 * adding multiple new contact and checking if it is in sync
	 * 
	 * @throws DatabaseException
	 * @throws SQLException
	 */
	@Test
	public void givenMultipleNewContact_WhenAddedUsingThreads_ShouldSincWithDB() throws DatabaseException {
		List<Person> newContactsList = Arrays.asList(
				new Person(0, "Jeff", "Bezos", "Shirdi", "Nashik", "Nashik", 758458, 7777777777L, "jeffbezz@gmail.com",
						"", "profession"),
				new Person(0, "Bill", "Gates", "Shirdi", "Nashik", "Nashik", 758458, 9865986532L, "billgates@gmail.com",
						"", "family"),
				new Person(0, "Mark", "Zuks", "Shirdi", "Nashik", "Nashik", 758458, 1111111111L, "markzuk@gmail.com",
						"", "friend"));
		addressBookFileService.addMultipleContacts(newContactsList);
		contactData = addressBookFileService.readContactData(IOService.DB_IO);
		boolean result = addressBookFileService
				.checkMultipleContactDataSync(Arrays.asList("Jeff Bezos", "Bill Gates", "Mark Zuks"));
		assertTrue(result);
	}
}
