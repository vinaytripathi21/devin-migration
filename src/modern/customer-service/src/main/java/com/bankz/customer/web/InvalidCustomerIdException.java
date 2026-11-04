package com.bankz.customer.web;

public class InvalidCustomerIdException extends RuntimeException {

    public InvalidCustomerIdException(String customerId) {
        super("Customer ID must be 1 to 10 digits: " + customerId);
    }
}
