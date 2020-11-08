package com.capgemini.addressbookjdbc;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;

import io.restassured.RestAssured;
import io.restassured.response.Response;

class AddressBookJSONFileServiceTest {
	@BeforeEach
	public void setUp() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = 3000;
	}

	/**
	 * sending get request and retrieving all data from JSON server
	 * 
	 * @return
	 */
	private Person[] getContactList() {
		Response response = RestAssured.get("/contacts");
		System.out.println("Contact entries in JSONServer:\n" + response.asString());
		String responseBody = response.getBody().asString();
		System.out.println("Response Body is =>  " + responseBody);
		Person[] arrayOfContact = new Gson().fromJson(response.asString(), Person[].class);
		return arrayOfContact;
	}

	@Test
	public void givenContactDataInJSONServer_WhenRetrieved_ShouldMatchTheCount() {
		Person[] arrayOfContact = getContactList();
		AddressBookFileService addressBookFileService = new AddressBookFileService(Arrays.asList(arrayOfContact));
		long entries = addressBookFileService.countEntries();
		assertEquals(15, entries);
	}

}
