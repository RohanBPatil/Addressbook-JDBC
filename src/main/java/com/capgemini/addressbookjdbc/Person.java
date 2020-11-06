package com.capgemini.addressbookjdbc;

import java.time.LocalDate;
import java.util.Objects;

public class Person {
	private int contactId;
	private String firstName;
	private String lastName;
	private String address;
	private String city;
	private String state;

	private int zip;
	private long phoneNum;
	private String email;
	private String addbookName;
	private String type;
	private LocalDate dateAdded;

	public int getContactId() {
		return contactId;
	}

	public void setContactId(int contactId) {
		this.contactId = contactId;
	}

	public String getAddbookName() {
		return addbookName;
	}

	public void setAddbookName(String addbookName) {
		this.addbookName = addbookName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Person() {

	}

	public Person(String firstName, String lastName, String address, String city, String state, int zip, long phoneNum,
			String email) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.address = address;
		this.city = city;
		this.state = state;
		this.zip = zip;
		this.phoneNum = phoneNum;
		this.email = email;
	}

	public Person(int contactId, String firstName, String lastName, String address, String city, String state, int zip,
			long phoneNum, String email, String addbookName, String type) {
		this(firstName, lastName, address, city, state, zip, phoneNum, email);
		this.contactId = contactId;
		this.addbookName = addbookName;
		this.type = type;
	}

	public Person(int contactId, String firstName, String lastName, String address, String city, String state, int zip,
			long phoneNum, String email, String addbookName, String type, LocalDate dateAdded) {
		this(contactId, firstName, lastName, address, city, state, zip, phoneNum, email, addbookName, type);
		this.dateAdded = dateAdded;
	}

	public String getName() {
		return firstName + " " + lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public int getZip() {
		return zip;
	}

	public void setZip(int zip) {
		this.zip = zip;
	}

	public long getPhoneNumber() {
		return phoneNum;
	}

	public void setPhoneNumber(long phoneNum) {
		this.phoneNum = phoneNum;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public LocalDate getDateAdded() {
		return dateAdded;
	}

	public void setDateAdded(LocalDate dateAdded) {
		this.dateAdded = dateAdded;
	}

	@Override
	public String toString() {
		String details = contactId + "   " + firstName + "   " + lastName + "   " + address + "   " + city + "   "
				+ state + "   " + zip + "   " + phoneNum + "   " + email + "   " + addbookName + "   " + type + "   "
				+ dateAdded + "   " + "\n";
		return details;
	}

	@Override
	public boolean equals(Object object) {
		boolean result = false;
		if ((object == null) || (getClass() != object.getClass())) {
			result = false;
		} else {
			Person person = (Person) object;
			String name = this.firstName + this.lastName;
			result = (name).equals(person.getFirstName() + person.getLastName())
					&& (this.contactId == person.getContactId());
		}

		return result;
	}

	@Override
	public int hashCode() {
		return Objects.hash(contactId, firstName, lastName, phoneNum);
	}

}
