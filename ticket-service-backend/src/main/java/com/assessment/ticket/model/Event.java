package com.assessment.ticket.model;

import jakarta.persistence.*;

@Entity
@Table(name = "events")
public class Event {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String name;
	
	private int remainingTickets;
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getRemainingTickets() {
		return remainingTickets;
	}
	public void setRemainingTickets(int remainingTickets) {
		this.remainingTickets = remainingTickets;
	}
	
	
}
