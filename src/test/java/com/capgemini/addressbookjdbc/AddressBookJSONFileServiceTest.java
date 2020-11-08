package com.capgemini.addressbookjdbc;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

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
		assertEquals(18, entries);
	}

	/**
	 * adds new contacts to JSON server and returns response
	 * 
	 * @param newContacts
	 * @return
	 */
	private Response addContactToJsonServer(Person contact) {
		String json = new Gson().toJson(contact);
		RequestSpecification request = RestAssured.given();
		request.header("Content-Type", "application/json");
		request.body(json);
		return request.post("/contacts");
	}

	@Test
	public void givenMultipleNewContacts_WhenAdded_ShouldMatch201ResponseAndCount() {
		List<Person> newContacts = Arrays.asList(
				new Person("Joe", "Bidden", "Karad", "Satara", "Maharashtra", 525252, 0000000000, "joeb@gmail.com",
						LocalDate.now()),
				new Person("saurabh", "raut", "Panchgani", "Satara", "Maharashtra", 525253, 0000000000,
						"saurabhr@gmail.com", LocalDate.now()),
				new Person("Kamala", "Harris", "Vita", "Sangli", "Maharashtra", 858585, 0000000000, "kamalah@gmail.com",
						LocalDate.now()));
		AddressBookFileService addressBookFileService = new AddressBookFileService(Arrays.asList(getContactList()));
		newContacts.forEach(contact -> {
			Runnable task = () -> {
				Response response = addContactToJsonServer(contact);
				int statusCode = response.getStatusCode();
				assertEquals(201, statusCode);
				addressBookFileService.addToApplicationMemory(contact);
			};
			Thread thread = new Thread(task, contact.getFirstName());
			thread.start();
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
		long entries = addressBookFileService.countEntries();
		assertEquals(18, entries);
	}

}
