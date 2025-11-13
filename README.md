# TicketBookingApp

A Concurrent Ticket Booking Application built using Spring Boot (Java 17) and Angular 20.3.0.This project demonstrates how to safely handle concurrent ticket bookings for events, preventing overbooking even when multiple users attempt to book simultaneously.

**Project Structure**

TicketBookingApp/
│
├── ticket-service-backend/ 
│
└── ticketbooking-frontend/

**Backend (Spring Boot)**

* RESTful APIs for managing event tickets
* Concurrent-safe ticket booking using transactional locking
* In-memory H2 Database
* Spring Data JPA for ORM
* JUnit tests to validate concurrent bookings

**Frontend (Angular)**

* Built with Angular 20.3.0 and Angular Material
* Displays events and available tickets in a table view
* Allows booking through a modal dialog
* Displays success or error notifications
* Uses proxy configuration (/api) to communicate with the backend

**Backend Setup**

**Prerequisites**

Requirement	    Version/Notes
Java	          17
Maven	          3.8
Port	          8080

Steps to Run
----------------
1. cd ticket-service-backend  2. mvn clean install 3. mvn spring-boot:run

**Frontend Setup**
**Prerequisites**

Requirement	    Version/Notes
Node.js	        18+
Angular CLI	    20+
Port            4200

Steps to run
-------------
1.cd ticketbooking-frontend 2.npm install  3.Start with ng serve --proxy-config proxy.conf.json so /api forwards to the Spring Boot backend.

**App will be available at: http://localhost:4200**

  
