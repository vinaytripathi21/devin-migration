package com.bankz.customer.web;

import com.bankz.customer.domain.Customer;
import com.bankz.customer.domain.CustomerKey;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerResponseTest {

    @Test
    void convertsYyyymmddIntegerLikeCobol() {
        assertThat(CustomerResponse.date(19850615)).isEqualTo("1985-06-15");
        assertThat(CustomerResponse.date(20000101)).isEqualTo("2000-01-01");
    }

    @Test
    void nullDateBecomesZerosLikeInitializedCommarea() {
        assertThat(CustomerResponse.date(null)).isEqualTo("0000-00-00");
    }

    @Test
    void stripsDb2CharPaddingAndMapsAllFields() {
        Customer c = new Customer(new CustomerKey("987654", "0000000042"), "Dr        ", "Alex   ", "Morgan  ",
                19781130, "+1 212 555 0199     ", "7 Park Avenue  ", null, "New York  ", "10016     ",
                "United States ", "SUSPENDED ", 20190302, (short) 480, 20251201);

        CustomerResponse r = CustomerResponse.from(c);

        assertThat(r.customerId()).isEqualTo("0000000042");
        assertThat(r.title()).isEqualTo("Dr");
        assertThat(r.firstName()).isEqualTo("Alex");
        assertThat(r.lastName()).isEqualTo("Morgan");
        assertThat(r.dateOfBirth()).isEqualTo("1978-11-30");
        assertThat(r.phoneNumber()).isEqualTo("+1 212 555 0199");
        assertThat(r.address()).isEqualTo(
                new CustomerResponse.Address("7 Park Avenue", "", "New York", "10016", "United States"));
        assertThat(r.customerStatus()).isEqualTo("SUSPENDED");
        assertThat(r.createdDate()).isEqualTo("2019-03-02T00:00:00Z");
    }
}
