package com.assessment.ticket.controller;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.assessment.ticket.model.Event;
import com.assessment.ticket.service.TicketService;
import com.assessment.ticket.util.ResponseUtil;

@RestController
@RequestMapping("/tickets")
public class TicketController {

	private final static Logger log = Logger.getLogger(TicketController.class.getName());
	
	private final TicketService ticketService;
	
	public TicketController(TicketService ticketServ) {
		this.ticketService = ticketServ;
	}
	
	@GetMapping
	public List<Event> listAllEvents(){
		return ticketService.fetchAllEvents();
	}
	
	/**
	 * <p> This api used to create events with predefined event meta data for event list</p>
	 * @param event input object with name and number of available tickets
	 * @return saved event object with event Id
	 */
	@PostMapping
	public ResponseEntity<Event> createEvents(@RequestBody Event event){
		log.info(" Inside createEvents");
		Event dbEvent = ticketService.saveEvent(event);
		return new ResponseEntity<Event>(dbEvent, HttpStatus.OK);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Object> getEventById(@PathVariable Long id){
		Event event = ticketService.getEvent(id);
		return new ResponseEntity<Object>(new ResponseUtil().buildAndReturnResponse("Event details fetched successfully", event, "success"), HttpStatus.OK);
	}
	
	@PostMapping("/{id}/book")
	public ResponseEntity<Event> bookTickets(@PathVariable Long id, @RequestParam int count){
		log.info(" Booking Request: event is "+id + ", tickets to book : "+count);
		
		Event updatedEvent = ticketService.bookTickets(id, count);
		
		return new ResponseEntity<>(updatedEvent, HttpStatus.OK);
	}
}
