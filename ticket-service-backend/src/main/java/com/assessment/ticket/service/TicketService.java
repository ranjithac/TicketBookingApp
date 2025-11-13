package com.assessment.ticket.service;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.assessment.ticket.dao.EventRepository;
import com.assessment.ticket.model.Event;
import com.assessment.ticket.util.InsufficientTicketsException;
import com.assessment.ticket.util.ResourceNotFoundException;

@Service
public class TicketService {
	
	private final static Logger log = Logger.getLogger(TicketService.class.getName());
	
	private final EventRepository eventRepository;
	
	private static final int MAX_RETRY_ATTEMPTS = 10;
	
	public TicketService(EventRepository eventRepository) {
		this.eventRepository = eventRepository;
	}
	
	public Event getEvent(Long id) {
        return eventRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Failed to load event details"));
    }
	
	public Event saveEvent(Event obj) {
		obj = eventRepository.save(obj);
		log.info("event id in saveEvent method : service "+obj.getId());
		return obj;
	}
	
	public List<Event> fetchAllEvents(){
		return eventRepository.findAll();
	}
	
	/**
	 * Books tickets for an event with optimistic locking and retry mechanism
	 * @param eventId the Event ID
	 * @param numberOfTickets number of tickets to book 
	 * handles the ObjectOptimisticLockingFailureException with retry
	 **/
	
	public Event bookTickets(Long eventId, int numberOfTickets) {
		int attempt = 0;
		
		while(attempt < MAX_RETRY_ATTEMPTS) {
			try {
				return tryBookTickets(eventId, numberOfTickets);
			} catch(ObjectOptimisticLockingFailureException optimisticExp) {
				attempt++;
				log.info(" Optimistic lock failure on attempt "+attempt+ " for event "+eventId);
				 if (attempt >= MAX_RETRY_ATTEMPTS) {
	                    throw new RuntimeException("Booking failed due to high concurrency. Please try again.");
	             }
				 
				 //small fixed delay before retry
				 try { 
					 Thread.sleep(50);
				 } catch (InterruptedException ie) {
					 Thread.currentThread().interrupt();
					 throw new RuntimeException("Booking interrupted", ie);
				 }
			}
		}
		throw new RuntimeException("Booking failed after maximum retry attempts");
	}
	
	/**
	 * Books tickets for an event with optimistic locking and retry mechanism
	 * @param eventId the Event ID
	 * @param numberOfTickets number of tickets to book
	 * @return updated Event object with new remaining tickets count
	 * @throws ResourceNotFoundException if event is not exist
	 * @thorws <p> ObjectOptimisticLockingFailureException if concurrent 
	 * modification exception after max retries </p>
	 */
	@Transactional
	protected Event tryBookTickets(Long eventId, int numberOfTickets) {
		Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
		
		if(event.getRemainingTickets() < numberOfTickets) {
			log.info("Insufficient tickets for the event. Requested : "+numberOfTickets + " available tickets "+event.getRemainingTickets());
			throw new InsufficientTicketsException("Insufficient tickets available. Requested: "+numberOfTickets +" Available : "+event.getRemainingTickets());
		}
		
		//decrement the tickets to book and update 
		event.setRemainingTickets(event.getRemainingTickets() - numberOfTickets);
		Event updatedEvent = eventRepository.save(event);
		
		log.info("Successfully booked "+numberOfTickets + "Remaining tickets for event "+updatedEvent.getRemainingTickets());
		return updatedEvent;
	}
}
