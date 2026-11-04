package com.bankz.customer.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

/** CUSTOMER-KEY: CUSTOMER_SORTCODE CHAR(6) + CUSTOMER_NUMBER CHAR(10), zero padded. */
@Embeddable
public record CustomerKey(
        @Column(name = "customer_sortcode", length = 6) String sortCode,
        @Column(name = "customer_number", length = 10) String number) implements Serializable {

    public static CustomerKey of(long sortCode, long number) {
        return new CustomerKey(String.format("%06d", sortCode), String.format("%010d", number));
    }
}
