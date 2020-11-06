package com.capgemini.addressbookjdbc;

@SuppressWarnings("serial")
public class DatabaseException extends Exception{
	public DatabaseException(String message) {
		super(message);
	}
}
