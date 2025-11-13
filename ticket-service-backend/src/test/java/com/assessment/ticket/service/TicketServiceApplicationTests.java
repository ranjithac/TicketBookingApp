package com.assessment.ticket.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.assessment.ticket.dao.EventRepository;
import com.assessment.ticket.model.Event;
import com.assessment.ticket.util.InsufficientTicketsException;

/**
 * Tests for concurrent ticket booking scenarios
 */
@SpringBootTest
public class TicketServiceApplicationTests {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private EventRepository eventRepository;

    private Event testEvent;

    @BeforeEach
    public void setup() {
        testEvent = new Event();
        testEvent.setName("Test Concert");
        testEvent.setRemainingTickets(100);
        testEvent = eventRepository.save(testEvent);
    }

    @Test
    public void testSingleBooking_Success() {
        Long eventId = testEvent.getId();
        int ticketsToBook = 5;

        Event updatedEvent = ticketService.bookTickets(eventId, ticketsToBook);

        assertEquals(95, updatedEvent.getRemainingTickets());
        
        Event dbEvent = eventRepository.findById(testEvent.getId()).orElseThrow();
        assertEquals(95, dbEvent.getRemainingTickets());
    }

    @Test
    public void testBooking_InsufficientTickets() {
       
        Long eventId = testEvent.getId();
        int ticketsToBook = 150;

        assertThrows(InsufficientTicketsException.class, () -> {
            ticketService.bookTickets(eventId, ticketsToBook);
        });

        //no changes in the event data
        Event unchangedEvent = eventRepository.findById(testEvent.getId()).orElseThrow();
        assertEquals(100, unchangedEvent.getRemainingTickets());
    }

    @Test
    public void testConcurrentBookings_NoOverbooking() throws InterruptedException {
        //10 users each trying to book 10 tickets simultaneously
        int numberOfUsers = 4;
        int ticketsPerUser = 25;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfUsers);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch completionLatch = new CountDownLatch(numberOfUsers);
        
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        // submitting all booking requests
        for (int i = 0; i < numberOfUsers; i++) {
            executorService.submit(() -> {
                try {
                    startLatch.await(); // Wait for all threads to be ready
                    ticketService.bookTickets(testEvent.getId(), ticketsPerUser);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                } finally {
                    completionLatch.countDown();
                }
            });
        }

        // all threads start at once
        startLatch.countDown();
        
        // Waiting for all threads to complete
        completionLatch.await(30, TimeUnit.SECONDS);
        executorService.shutdown();

        assertEquals(4, successCount.get(), "All 4 bookings should succeed");
        assertEquals(0, failureCount.get(), "No bookings should fail");

        Event finalEvent = eventRepository.findById(testEvent.getId()).orElseThrow();
        assertEquals(0, finalEvent.getRemainingTickets(), "All tickets should be booked");
    }

    @Test
    public void testConcurrentBookings_SomeWillFail() throws InterruptedException {
        //15 users each trying to book 10 tickets, but only 100 available
        int numberOfUsers = 15;
        int ticketsPerUser = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfUsers);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch completionLatch = new CountDownLatch(numberOfUsers);
        
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        for (int i = 0; i < numberOfUsers; i++) {
            executorService.submit(() -> {
                try {
                    startLatch.await();
                    ticketService.bookTickets(testEvent.getId(), ticketsPerUser);
                    successCount.incrementAndGet();
                } catch (InsufficientTicketsException e) {
                    failureCount.incrementAndGet();
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                } finally {
                    completionLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        completionLatch.await(30, TimeUnit.SECONDS);
        executorService.shutdown();

        assertEquals(10, successCount.get(), "Exactly 10 bookings should succeed");
        assertEquals(5, failureCount.get(), "5 bookings should fail");

        Event finalEvent = eventRepository.findById(testEvent.getId()).orElseThrow();
        assertEquals(0, finalEvent.getRemainingTickets(), "All tickets should be booked");
    }
    
    @Test
    public void testConcurrentBookings_VariableRequests_NoOverbooking() throws InterruptedException {
        //10 users requesting variable count of tickets
        int[] ticketsPerUser = {30, 20, 10, 25, 15, 5, 10, 20, 5, 10}; // total 150
        int totalAvailableTickets = 100; // initial tickets in testEvent
        testEvent.setRemainingTickets(totalAvailableTickets);
        eventRepository.save(testEvent);

        int numberOfUsers = ticketsPerUser.length;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfUsers);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch completionLatch = new CountDownLatch(numberOfUsers);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        AtomicInteger bookedTickets = new AtomicInteger(0);

        for (int i = 0; i < numberOfUsers; i++) {
            final int ticketsToBook = ticketsPerUser[i];
            executorService.submit(() -> {
                try {
                    startLatch.await();
                    System.out.println(" TicketsToBook count "+ ticketsToBook);
                    ticketService.bookTickets(testEvent.getId(), ticketsToBook);
                    successCount.incrementAndGet();
                    bookedTickets.addAndGet(ticketsToBook);
                } catch (InsufficientTicketsException e) {
                    failureCount.incrementAndGet();
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                } finally {
                    completionLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        completionLatch.await(30, TimeUnit.SECONDS);
        executorService.shutdown();

        Event finalEvent = eventRepository.findById(testEvent.getId()).orElseThrow();
        int remaining = finalEvent.getRemainingTickets();

        System.out.println("Total tickets booked: " + bookedTickets.get() +" Remaining tickets: " + remaining);
        System.out.println("Successful bookings: " + successCount.get() +" Failed bookings: " + failureCount.get());

        // based on the variable count over the iteration it will vary
        assertTrue(finalEvent.getRemainingTickets() >= 0); 

        //total booked + remaining = original tickets
        assertEquals(bookedTickets.get() + remaining,totalAvailableTickets, "All the 100 tickets booked");
    }
}


