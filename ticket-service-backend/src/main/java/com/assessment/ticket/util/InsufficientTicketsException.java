package com.assessment.ticket.util;

public class InsufficientTicketsException extends RuntimeException{
	
	public InsufficientTicketsException(String message) {
		super(message);
	}

}
