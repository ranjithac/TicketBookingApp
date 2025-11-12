package com.assessment.ticket.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.assessment.ticket.model.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

}
