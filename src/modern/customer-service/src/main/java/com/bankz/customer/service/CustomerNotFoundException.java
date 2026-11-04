package com.bankz.customer.service;

/** INQCUST-INQ-SUCCESS = 'N'. failCode is INQCUST-INQ-FAIL-CD: '1' = not found, '9' = no customers for the sort code. */
public class CustomerNotFoundException extends RuntimeException {

    private final String failCode;

    public CustomerNotFoundException(String message, String failCode) {
        super(message);
        this.failCode = failCode;
    }

    public String getFailCode() {
        return failCode;
    }
}
