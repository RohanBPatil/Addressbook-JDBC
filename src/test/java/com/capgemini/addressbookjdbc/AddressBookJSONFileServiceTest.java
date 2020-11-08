package com.capgemini.addressbookjdbc;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.capgemini.addressbookjdbc.AddressBookFileService.IOService;
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

	/**
	 * updating phone number of person in JSON server as well as application memory
	 * 
	 * @param name
	 * @param phone
	 * @return
	 * @throws DatabaseException
	 */
	private Response updatePhone(String name, long phone) throws DatabaseException {
		Person[] arrayOfContact = getContactList();
		AddressBookFileService addressbookService = new AddressBookFileService(Arrays.asList(arrayOfContact));
		addressbookService.updatePersonsPhone(name, phone, IOService.REST_IO);
		Person contactToUpdate = addressbookService.getContact(name);
		String contactJson = new Gson().toJson(contactToUpdate);
		RequestSpecification request = RestAssured.given();
		request.header("Content-Type", "application/json");
		request.body(contactJson);
		return request.put("/contacts/" + contactToUpdate.getId());

	}

	@Test
	public void givenMultipleEmployees_WhenUpdatedSalary_ShouldSyncWithDB() throws DatabaseException {
		int statusCode = updatePhone("Joe Bidden", 8888855555L).getStatusCode();
		assertEquals(200, statusCode);
	}

	/**
	 * deleting contact from JSON server and application memory
	 * 
	 * @param name
	 * @return
	 */
	private Response deleteContact(String name) {
		Person[] arrayOfContact = getContactList();
		AddressBookFileService addressbookService = new AddressBookFileService(Arrays.asList(arrayOfContact));
		Person contact = addressbookService.getContact(name);
		addressbookService.deleteFromApplicationMemory(contact);
		RequestSpecification request = RestAssured.given();
		request.header("Content-Type", "application/json");
		return request.delete("/contacts/" + contact.getId());
	}

	@Test
	public void givenContactToDelete_WhenDeleted_ShouldMatch200ResponseAndCount() {
		int statusCode = deleteContact("Kamala Harris").getStatusCode();
		assertEquals(200, statusCode);
		Person[] arrayOfContact = getContactList();
		int count = arrayOfContact.length;
		assertEquals(17, count);
	}
}