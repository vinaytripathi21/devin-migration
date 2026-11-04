package com.bankz.customer.service;

import com.bankz.common.BusinessFailureException;
import org.springframework.http.HttpStatus;

/** INQCUST-INQ-SUCCESS = 'N'. failCode is INQCUST-INQ-FAIL-CD: '1' = not found, '9' = no customers for the sort code. */
public class CustomerNotFoundException extends BusinessFailureException {

    public CustomerNotFoundException(String message, String failCode) {
        super(failCode, message, HttpStatus.NOT_FOUND);
    }
}
