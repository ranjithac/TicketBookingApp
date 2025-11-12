package com.assessment.ticket.service;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.assessment.ticket.dao.EventRepository;
import com.assessment.ticket.model.Event;
import com.assessment.ticket.util.ResourceNotFoundException;

@Service
public class TicketService {
	
	private final static Logger log = Logger.getLogger(TicketService.class.getName());
	
	private final EventRepository eventRepository;
	
	private TicketService(EventRepository eventRepository) {
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
}
