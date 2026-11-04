package com.bankz.customer.service;

import com.bankz.common.BusinessFailureException;
import com.bankz.common.LegacyFormat;
import com.bankz.customer.domain.Customer;
import com.bankz.customer.domain.CustomerKey;
import com.bankz.customer.domain.CustomerRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Conversion of UPDCUST: only non-blank groups of fields are changed. */
@Service
public class CustomerUpdateService {

    private final CustomerRepository customers;
    private final long sortCode;

    public CustomerUpdateService(CustomerRepository customers, @Value("${bankz.sort-code:987654}") long sortCode) {
        this.customers = customers;
        this.sortCode = sortCode;
    }

    @Transactional
    public Customer update(long customerNumber, CustomerChanges c) {
        if (!Titles.isValid(c.title())) {
            throw new BusinessFailureException("T", "Invalid title: " + c.title(), HttpStatus.BAD_REQUEST);
        }
        if (LegacyFormat.isBlank(c.firstName()) && LegacyFormat.isBlank(c.lastName())
                && LegacyFormat.isBlank(c.addressLine1())) {
            throw new BusinessFailureException("4", "First name, last name and address are all blank",
                    HttpStatus.BAD_REQUEST);
        }
        Customer customer = customers.findById(CustomerKey.of(sortCode, customerNumber))
                .orElseThrow(() -> new CustomerNotFoundException("Customer " + customerNumber + " not found", "1"));

        if (!LegacyFormat.isBlank(c.firstName())) {
            customer.updateName(c.title(), c.firstName(), c.lastName());
        }
        if (!LegacyFormat.isBlank(c.phone())) {
            customer.updatePhone(c.phone());
        }
        if (!LegacyFormat.isBlank(c.addressLine1())) {
            customer.updateAddress(c.addressLine1(), c.addressLine2(), c.city(), c.postcode(), c.country());
        }
        if (!LegacyFormat.isBlank(c.status())) {
            customer.updateStatus(c.status());
        }
        if (c.dateOfBirth() != null && c.dateOfBirth() / 10000 != 0) {
            customer.updateDateOfBirth(c.dateOfBirth());
        }
        return customer;
    }
}
