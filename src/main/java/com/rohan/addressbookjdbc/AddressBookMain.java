package com.rohan.addressbookjdbc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class AddressBookMain extends AddressBook {

	public enum IOServices {
		CONSOLE_IO, FILE_IO, DB_IO, REST_IO
	};

	public static HashMap<String, AddressBook> stateAddressBookMap = new HashMap<>();

	/**
	 * @param scanner
	 */
	public void addPersonDetails(Scanner scanner) {
		String checkToAdd = "y";

		while (checkToAdd.equalsIgnoreCase("y")) {
			// variables
			String firstName;
			String lastName;
			String address;
			String city;
			String state;
			int zip;
			long phoneNum;
			String email;

			System.out.println("Enter following details : ");
			System.out.println("First Name : ");
			firstName = scanner.nextLine();
			System.out.println("Last Name : ");
			lastName = scanner.nextLine();
			System.out.println("Address : ");
			address = scanner.nextLine();
			System.out.println("City : ");
			city = scanner.nextLine();
			System.out.println("State : ");
			state = scanner.nextLine();
			System.out.println("ZIP : ");
			zip = scanner.nextInt();
			System.out.println("Phone number : ");
			phoneNum = scanner.nextLong();
			scanner.nextLine();
			System.out.println("Email ID : ");
			email = scanner.nextLine();

			Person newPerson = new Person(firstName, lastName, address, city, state, zip, phoneNum, email);

			boolean duplicate;
			try {
				duplicate = stateAddressBookMap.values().stream().map(obj -> obj.getPersonList())
						.anyMatch(personList -> personList.contains(newPerson));
			} catch (NullPointerException e) {
				System.out.println("This contact will not be added");
				System.out.println(
						"Entered state name's address book is not present. First add new address book and then add this contact");
				return;
			}
			if (!duplicate) {
				stateAddressBookMap.get(state).personList.add(newPerson);
			} else {
				System.out.println("Same contact is already present Hence WILL NOT BE ADDED to address book");
			}

			System.out.println("Enter 'y' to ADD NEW PERSON'S DETAILS.\nEnter any other key to STOP ADDING.");
			checkToAdd = scanner.nextLine();
		}

	}

	/**
	 * @param name
	 * @param city
	 */
	public void searchPersonByCity(String name, String city) {
		List<Person> list = new ArrayList<Person>();
		for (Map.Entry<String, AddressBook> entry : stateAddressBookMap.entrySet()) {
			list = entry.getValue().getPersonList().stream().filter(c -> c.getCity().equals(city))
					.filter(c -> (c.getFirstName() + " " + c.getLastName()).equals(name)).collect(Collectors.toList());
		}
		for (Person c : list) {
			System.out.println(c);
		}
	}

	/**
	 * @param name
	 * @param state
	 */
	public void searchPersonByState(String name, String state) {
		List<Person> list = new ArrayList<Person>();
		for (Map.Entry<String, AddressBook> entry : stateAddressBookMap.entrySet()) {
			list = entry.getValue().getPersonList().stream().filter(c -> c.getState().equals(state))
					.filter(c -> (c.getFirstName() + " " + c.getLastName()).equals(name)).collect(Collectors.toList());
		}
		for (Person c : list) {
			System.out.println(c);
		}
	}

	/**
	 * @param city
	 */
	public void viewPersonsByCity(String city) {
		List<Person> list = new ArrayList<Person>();
		for (Map.Entry<String, AddressBook> entry : stateAddressBookMap.entrySet()) {
			list = entry.getValue().getPersonList().stream().filter(c -> c.getCity().equals(city))
					.collect(Collectors.toList());
		}
		for (Person c : list) {
			System.out.println(c);
		}
	}

	/**
	 * @param state
	 */
	public void viewPersonsByState(String state) {
		List<Person> list = new ArrayList<Person>();
		for (Map.Entry<String, AddressBook> entry : stateAddressBookMap.entrySet()) {
			list = entry.getValue().getPersonList().stream().filter(s -> s.getState().equals(state))
					.collect(Collectors.toList());
		}
		for (Person c : list) {
			System.out.println(c);
		}
	}

	/**
	 * @param city
	 */
	public void countByCity(String city) {
		long count = 0;
		for (Map.Entry<String, AddressBook> entry : stateAddressBookMap.entrySet()) {
			count += entry.getValue().getPersonList().stream().filter(c -> c.getCity().equals(city)).count();
		}
		System.out.println("Number of contacts in '" + city + "' : " + count);
	}

	/**
	 * @param state
	 */
	public void countByState(String state) {
		long count = 0;
		for (Map.Entry<String, AddressBook> entry : stateAddressBookMap.entrySet()) {
			count += entry.getValue().getPersonList().stream().filter(c -> c.getCity().equals(state)).count();
		}
		System.out.println("Number of contacts in '" + state + "' : " + count);
	}

	/**
	 * Sorting by person's name
	 */
	public void sortByName() {
		List<Person> personList = new ArrayList<>();
		for (Map.Entry<String, AddressBook> entry : stateAddressBookMap.entrySet()) {
			personList = entry.getValue().getPersonList().stream()
					.sorted((p1, p2) -> p1.getName().compareTo(p2.getName())).collect(Collectors.toList());
		}

		System.out.println("Sorted list of names: ");
		for (Person list : personList) {
			System.out.println(list.getName());
		}
	}

	/**
	 * sorting by zip
	 */
	public void sortByZip() {
		List<Person> personList = new ArrayList<>();
		for (Map.Entry<String, AddressBook> entry : stateAddressBookMap.entrySet()) {
			personList = entry.getValue().getPersonList().stream()
					.sorted((p1, p2) -> Integer.compare(p1.getZip(), p2.getZip())).collect(Collectors.toList());
		}

		System.out.println("Sorted list of ZIPs : ");
		for (Person list : personList) {
			System.out.println(list.getZip());
		}
	}

	/**
	 * reads data from file and prints on console
	 * 
	 * @param ioService
	 */
	public void readData(IOServices ioService) {
		if (ioService.equals(IOServices.FILE_IO))
			new AddressBookFileService().readData();
	}

	/**
	 * writes data taken from user by console to file
	 * 
	 * @param ioService
	 */
	public void writeData(IOServices ioService) {
		if (ioService.equals(IOServices.FILE_IO)) {
			new AddressBookFileService().writeData(stateAddressBookMap);
		}
	}

	/**
	 * reads data from addressbook.csv file
	 * 
	 * @param ioService
	 */
	public void readDataCSV(IOServices ioService) {
		if (ioService.equals(IOServices.FILE_IO)) {
			new AddressBookFileService().readDataCSV();
		}
	}

	/**
	 * writes data to addressbook.csv file
	 * 
	 * @param ioService
	 */
	public void writeDataCSV(IOServices ioService) {
		if (ioService.equals(IOServices.FILE_IO)) {
			new AddressBookFileService().writeDataCSV(stateAddressBookMap);
		}

	}

	/**
	 * reads data from addressbook.json file
	 * 
	 * @param ioService
	 */
	public void readDataGSON(IOServices ioService) {
		if (ioService.equals(IOServices.FILE_IO)) {
			new AddressBookFileService().readDataGSON();
		}
	}

	/**
	 * writes data to addressbook.json file
	 * 
	 * @param ioService
	 */
	public void writeDataGSON(IOServices ioService) {
		if (ioService.equals(IOServices.FILE_IO)) {
			new AddressBookFileService().writeDataGSON(stateAddressBookMap);
		}

	}

	public static void main(String[] args) {

		System.out.println("WELCOME TO ADDRESS BOOK");

		Scanner scanner = new Scanner(System.in);

		System.out.println("Enter first Address Book name : ");
		String addressBookName = scanner.nextLine();
		AddressBookMain addressBookMain = new AddressBookMain();
		AddressBook NewAddressBook = new AddressBook(addressBookName);
		stateAddressBookMap.put(addressBookName, NewAddressBook);
		addressBookMain.addPersonDetails(scanner);

		String yes = "y";

		do {
			System.out.println(
					"1. Add contact\n2. Edit contact\n3. Delete contact\n4. Add new Address Book\n5. Search person by city\n6. Search person by state"
							+ "\n7. View persons by city \n8. View by state\n9. Count by city\n10. Count by state\n11. Sort by name\n12. Sort by ZIP\n"
							+ "13. Write to file\n14. Read from console\n15. Write to CSV file\n16. Read from CSV file\n17. Write to json file\n18. Read from json file");
			int option = scanner.nextInt();
			scanner.nextLine();
			switch (option) {
			case 1:
				addressBookMain.addPersonDetails(scanner);
				break;
			case 2:
				System.out.println("Enter name of address book");
				String name = scanner.nextLine();
				stateAddressBookMap.get(name).editPersonDetails(scanner);
				break;
			case 3:
				System.out.println("Enter name of address book");
				name = scanner.nextLine();
				stateAddressBookMap.get(name).deletePersonDetails(scanner);
				break;
			case 4:
				System.out.println("Enter Address Book name : ");
				name = scanner.nextLine();
				NewAddressBook = new AddressBook(name);
				stateAddressBookMap.put(name, NewAddressBook);
				break;
			case 5:
				System.out.println("Enter the name to search : ");
				String personName = scanner.nextLine();
				System.out.println("Enter the city : ");
				String city = scanner.nextLine();
				addressBookMain.searchPersonByCity(personName, city);
				break;
			case 6:
				System.out.println("Enter the name to search : ");
				personName = scanner.nextLine();
				System.out.println("Enter the state : ");
				String state = scanner.nextLine();
				addressBookMain.searchPersonByCity(personName, state);
				break;
			case 7:
				System.out.println("Enter the city : ");
				city = scanner.nextLine();
				addressBookMain.viewPersonsByCity(city);
				break;
			case 8:
				System.out.println("Enter the state : ");
				state = scanner.nextLine();
				addressBookMain.viewPersonsByState(state);
				break;
			case 9:
				System.out.println("Enter the city : ");
				city = scanner.nextLine();
				addressBookMain.countByCity(city);
				break;
			case 10:
				System.out.println("Enter the state : ");
				state = scanner.nextLine();
				addressBookMain.countByState(state);
				break;
			case 11:
				addressBookMain.sortByName();
				break;
			case 12:
				addressBookMain.sortByZip();
				break;
			case 13:
				addressBookMain.writeData(IOServices.FILE_IO);
				break;
			case 14:
				addressBookMain.readData(IOServices.FILE_IO);
				break;
			case 15:
				addressBookMain.writeDataCSV(IOServices.FILE_IO);
				break;
			case 16:
				addressBookMain.readDataCSV(IOServices.FILE_IO);
				break;
			case 17:
				addressBookMain.writeDataGSON(IOServices.FILE_IO);
				break;
			case 18:
				addressBookMain.readDataGSON(IOServices.FILE_IO);
				break;
			default:
				System.out.println("Select correct choice");
				break;
			}
			System.out.println("Enter 'y' if you want to PERFORM NEW ACTION \nEnter any other key to EXIT");
			yes = scanner.nextLine();
		} while (yes.equalsIgnoreCase("y"));

		scanner.close();
	}

	
}
