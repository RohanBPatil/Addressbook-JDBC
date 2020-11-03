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

}
