package com.assessment.ticket.enums;

public enum ResponseConstants {

	STATUS("status"),
	DATA("data"),
	MESSAGE("message");
	
	private String property;
	
	private ResponseConstants(String propertyName) {
		this.property = propertyName;
	}
	public String getName() {
		return this.property;
		
	}
}
