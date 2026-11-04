package com.bankz.customer.web;

import com.bankz.common.InvalidRequestException;

public class InvalidCustomerIdException extends InvalidRequestException {

    public InvalidCustomerIdException(String customerId) {
        super("Customer ID must be 1 to 10 digits: " + customerId);
    }
}
