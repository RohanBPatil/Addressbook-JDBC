package com.rohan.addressbookjdbc;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
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
		assertEquals(8, contactData.size());
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
		assertEquals(3, contactByDateList.size());
	}
}
