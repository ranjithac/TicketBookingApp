package com.assessment.ticket.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<Map<String, Object>> handleResourceNotFoundException(ResourceNotFoundException ex){
		
		Map<String, Object> body = new HashMap<>();
		body.put("status", 404);
		body.put("error", "Not Found");
		body.put("data", null);
		body.put("message", ex.getMessage() );
		
		return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
	} 
	
	@ExceptionHandler(InsufficientTicketsException.class)
	public ResponseEntity<Map<String, Object>> handleInsufficientTicketsException(InsufficientTicketsException ex){
		
		Map<String, Object> body = new HashMap<>();
		body.put("status", 400);
		body.put("error", "Bad Request");
		body.put("data", null);
		body.put("message", ex.getMessage() );
		
		return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(ObjectOptimisticLockingFailureException.class)
	public ResponseEntity<Map<String, Object>> handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException ex){
		
		Map<String, Object> body = new HashMap<>();
		body.put("status", 409);
		body.put("error", "conflict");
		body.put("data", null);
		body.put("message", "Another user just booked. Please try again." );
		
		return new ResponseEntity<>(body, HttpStatus.CONFLICT);
	}

}
