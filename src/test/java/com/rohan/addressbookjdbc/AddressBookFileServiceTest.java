package com.rohan.addressbookjdbc;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.rohan.addressbookjdbc.AddressBookFileService.IOService;

class AddressBookFileServiceTest {

	/**
	 * checking if Retrieved data from the database
	 * 
	 * @throws DatabaseException
	 */
	@Test
	public void givenContactDataInDB_WhenRetrieved_ShouldMatchContactCount() throws DatabaseException {
		AddressBookFileService addressBookService = new AddressBookFileService();
		List<Person> contactData = addressBookService.readContactData(IOService.DB_IO);
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
		AddressBookFileService addressBookService = new AddressBookFileService();
		List<Person> contactData = addressBookService.readContactData(IOService.DB_IO);
		addressBookService.updatePersonsPhone("abc bca", 0000000000);
		addressBookService.readContactData(IOService.DB_IO);
		boolean result = addressBookService.checkContactDataSync("abc bca");
		assertEquals(true, result);
	}
}
