package com.capgemini.addressbookjdbc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonStreamParser;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

public class AddressBookFileService {
	private static final Logger LOG = LogManager.getLogger(AddressBookFileService.class);

	public enum IOService {
		CONSOLE_IO, FILE_IO, DB_IO, REST_IO
	};

	public static String FILE_NAME = "AddressBook.txt";
	public static String CSV_FILE_NAME = "C:\\Users\\abc\\eclipse-workspace\\Addressbook JDBC\\src\\main\\java\\com\\rohan\\addressbooksystem/addressbook.csv";
	public static String GSON_FILE_NAME = "C:\\Users\\abc\\eclipse-workspace\\Addressbook JDBC\\src\\main\\java\\com\\rohan\\addressbooksystem/addressbook.json";
	private List<Person> contactList = new ArrayList<>();
	private AddressbookDBService addressbookDBService;

	public AddressBookFileService() {
		addressbookDBService = AddressbookDBService.getInstance();
	}

	public AddressBookFileService(List<Person> list) {
		this();
		this.contactList = list;
	}

	public void writeData(Map<String, AddressBook> stateAddressBookMap) {
		StringBuffer personBuffer = new StringBuffer();
		stateAddressBookMap.values().stream().map(book -> book.getPersonList()).forEach(list -> {
			list.forEach(person -> {
				String empString = person.toString().concat("\n");
				personBuffer.append(empString);
			});
		});
		try {
			Files.write(Paths.get(FILE_NAME), personBuffer.toString().getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void readData() {
		try {
			Files.lines(new File(FILE_NAME).toPath()).forEach(System.out::println);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeDataCSV(Map<String, AddressBook> stateAddressBookMap) {

		File file = new File(CSV_FILE_NAME);

		try {
			FileWriter outputfile = new FileWriter(file);
			CSVWriter writer = new CSVWriter(outputfile);
			List<String[]> data = new ArrayList<String[]>();
			String[] header = { "First Name", "Last Name", "Address", "City", "State", "ZIP", "Phone Number",
					"Email ID" };
			data.add(header);
			stateAddressBookMap.values().stream().map(entry -> entry.getPersonList())
					.forEach(listEntry -> listEntry.forEach(person -> {
						String[] personData = { person.getFirstName(), person.getLastName(), person.getAddress(),
								person.getCity(), person.getState(), Integer.toString(person.getZip()),
								Long.toString(person.getPhoneNumber()), person.getEmail() };
						data.add(personData);
					}));

			writer.writeAll(data);
			writer.close();
			System.out.println("Data entered successfully to addressbook.csv file.");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void readDataCSV() {
		try {
			FileReader filereader = new FileReader(CSV_FILE_NAME);
			CSVReader csvReader = new CSVReader(filereader);
			String[] nextRecord;

			// we are going to read data line by line
			while ((nextRecord = csvReader.readNext()) != null) {
				for (String cell : nextRecord) {
					System.out.print(cell + "\t");
				}
				System.out.println();
			}
			csvReader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void writeDataGSON(Map<String, AddressBook> stateAddressBookMap) {
		try {
			Gson gson = new Gson();
			FileWriter writer = new FileWriter(GSON_FILE_NAME);
			stateAddressBookMap.values().stream().map(entry -> entry.getPersonList())
					.forEach(listEntry -> listEntry.forEach(person -> {
						String json = gson.toJson(person);
						try {
							writer.write(json);
						} catch (IOException exception) {
							exception.printStackTrace();
						}
					}));
			writer.close();
			System.out.println("Data entered successfully to addressbook.json file.");
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	public void readDataGSON() {
		Gson gson = new Gson();
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(GSON_FILE_NAME));
			JsonStreamParser parser = new JsonStreamParser(bufferedReader);
			while (parser.hasNext()) {
				JsonElement json = parser.next();
				if (json.isJsonObject()) {
					Person person = gson.fromJson(json, Person.class);
					System.out.println(person);
				}
			}
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	/**
	 * reads all data from database
	 * 
	 * @param ioService
	 * @return
	 * @throws DatabaseException
	 */
	public List<Person> readContactData(IOService ioService) throws DatabaseException {
		if (ioService.equals(IOService.DB_IO)) {
			this.contactList = addressbookDBService.readData();
		}
		return this.contactList;
	}

	public void updatePersonsPhone(String name, long phone, IOService ioService) throws DatabaseException {
		if (ioService.equals(IOService.DB_IO)) {
			int result = addressbookDBService.updatePersonsData(name, phone);
			if (result == 0)
				return;
		}
		if (ioService.equals(IOService.REST_IO)) {
			Person contact = this.getContact(name);
			if (contact != null)
				contact.setPhoneNumber(phone);
		}
	}

	public Person getContact(String name) {
		Person contact = this.contactList.stream().filter(contactData -> contactData.getName().equalsIgnoreCase(name))
				.findFirst().orElse(null);
		return contact;
	}

	public boolean checkContactDataSync(String name) throws DatabaseException {
		List<Person> employeeList = addressbookDBService.getContactFromDatabase(name);
		return employeeList.get(0).equals(getContact(name));

	}

	/**
	 * returns list of contacts added between given dates
	 * 
	 * @param start
	 * @param end
	 * @return
	 * @throws DatabaseException
	 */
	public List<Person> getContactsByDate(LocalDate start, LocalDate end) throws DatabaseException {
		List<Person> contactByDateList = addressbookDBService.readDataForGivenDateRange(start, end);
		return contactByDateList;
	}

	/**
	 * returns list of contacts belonging to given city
	 * 
	 * @param city
	 * @return
	 * @throws DatabaseException
	 */
	public List<Person> getContactsByCity(String city) throws DatabaseException {
		return addressbookDBService.getContactsByCity(city);
	}

	/**
	 * returns list of contacts belonging to given state
	 * 
	 * @param state
	 * @return
	 * @throws DatabaseException
	 */
	public List<Person> getContactsByState(String state) throws DatabaseException {
		return addressbookDBService.getContactsByState(state);
	}

	/**
	 * Adding new contact to database
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
	 * @throws DatabaseException
	 * @throws SQLException
	 */
	public void addNewContact(String firstName, String lastName, String address, String city, String state, int zip,
			long phone, String email, List<String> types) throws DatabaseException {
		try {
			addressbookDBService.addContactToDatabase(firstName, lastName, address, city, state, zip, phone, email,
					types, LocalDate.now());
		} catch (DatabaseException | SQLException exception) {
			throw new DatabaseException(exception.getMessage());
		}
	}

	/**
	 * adding multiple new contacts in database using threads
	 * 
	 * @param newContactsList
	 * @throws DatabaseException
	 */
	public void addMultipleContacts(List<Person> newContactsList) throws DatabaseException {
		Map<Integer, Boolean> contactAdditionStatus = new HashMap<Integer, Boolean>();
		newContactsList.forEach(person -> {
			Runnable task = () -> {
				contactAdditionStatus.put(person.hashCode(), false);
				LOG.info("Contact Being Added: " + Thread.currentThread().getName());
				try {
					addNewContact(person.getFirstName(), person.getLastName(), person.getAddress(), person.getCity(),
							person.getState(), person.getZip(), person.getPhoneNumber(), person.getEmail(),
							Arrays.asList(person.getType()));
				} catch (DatabaseException exception) {
					exception.printStackTrace();
				}
				contactAdditionStatus.put(person.hashCode(), true);
				LOG.info("Contact Added: " + Thread.currentThread().getName());
			};
			Thread thread = new Thread(task, person.getName());
			thread.start();
		});
		while (contactAdditionStatus.containsValue(false)) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException exception) {
				throw new DatabaseException(exception.getMessage());
			}
		}
	}

	/**
	 * checking if data added is in sync
	 * 
	 * @param namesList
	 * @return
	 */
	public boolean checkMultipleContactDataSync(List<String> namesList) {
		List<Boolean> resultList = new ArrayList<>();
		namesList.forEach(name -> {
			try {
				resultList.add(checkContactDataSync(name));
			} catch (DatabaseException e) {
				e.printStackTrace();
			}
		});
		if (resultList.contains(false)) {
			return false;
		}
		return true;
	}

	/**
	 * returns size of application memory
	 * 
	 * @return
	 */
	public int countEntries() {
		return contactList.size();
	}

	/**
	 * adds new contacts to application memory
	 * 
	 * @param newContacts
	 */
	public void addToApplicationMemory(Person contact) {
		contactList.add(contact);
	}

}
