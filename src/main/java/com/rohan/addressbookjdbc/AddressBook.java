package com.rohan.addressbookjdbc;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AddressBook {
	private String AddressBookName;
	public List<Person> personList;

	public List<Person> getPersonList() {
		return personList;
	}

	public AddressBook(String AddressBookName) {
		personList = new ArrayList<Person>();
		this.AddressBookName = AddressBookName;
	}

	public String getAddressBookName() {
		return AddressBookName;
	}

	public void setAddressBookName(String addressBookName) {
		AddressBookName = addressBookName;
	}

	public void setPersonList(List<Person> personList) {
		this.personList = personList;
	}

	public AddressBook() {
		personList = new ArrayList<Person>();
	}

	public void editPersonDetails(Scanner scanner) {
		String FirstName;
		String LastName;
		String address;
		String city;
		String state;
		int zip;
		long phoneNum;
		String email;

		System.out.println("Enter name of a person to edit contact details");
		System.out.println("First Name : ");
		FirstName = scanner.nextLine();
		System.out.println("Last Name : ");
		LastName = scanner.nextLine();

		for (Person thatPerson : personList) {
			if (FirstName.equalsIgnoreCase(thatPerson.getFirstName())
					&& LastName.equalsIgnoreCase(thatPerson.getLastName())) {
				System.out.println("New Address : ");
				address = scanner.nextLine();
				thatPerson.setAddress(address);
				System.out.println("New City : ");
				city = scanner.nextLine();
				thatPerson.setCity(city);
				System.out.println("New State : ");
				state = scanner.nextLine();
				thatPerson.setState(state);
				System.out.println("New ZIP : ");
				zip = scanner.nextInt();
				thatPerson.setZip(zip);
				System.out.println("New Phone number : ");
				phoneNum = scanner.nextLong();
				thatPerson.setPhoneNumber(phoneNum);
				scanner.nextLine();
				System.out.println("New Email ID : ");
				email = scanner.nextLine();
				thatPerson.setEmail(email);
			}
		}
	}

	public void deletePersonDetails(Scanner scanner) {
		String FirstName;
		String LastName;

		System.out.println("Enter name of a person to DELETE contact details");
		System.out.println("First Name : ");
		FirstName = scanner.nextLine();
		System.out.println("Last Name : ");
		LastName = scanner.nextLine();
		for (Person thatPerson : personList) {
			if (FirstName.equalsIgnoreCase(thatPerson.getFirstName())
					&& LastName.equalsIgnoreCase(thatPerson.getLastName())) {
				personList.remove(thatPerson);
			}
		}
	}

}
