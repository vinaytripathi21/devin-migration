package com.bankz.customer.web;

import com.bankz.customer.service.CustomerNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(CustomerNotFoundException.class)
    ResponseEntity<Map<String, String>> notFound(CustomerNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", e.getMessage(), "failCode", e.getFailCode()));
    }

    @ExceptionHandler(InvalidCustomerIdException.class)
    ResponseEntity<Map<String, String>> badRequest(InvalidCustomerIdException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }

    /** Replaces the COBOL abend CVR1 raised for any SQLCODE other than 0 or 100. */
    @ExceptionHandler(DataAccessException.class)
    ResponseEntity<Map<String, String>> databaseError(DataAccessException e) {
        log.error("Customer database read failed (legacy abend CVR1)", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Customer database read failed", "abendCode", "CVR1"));
    }
}
