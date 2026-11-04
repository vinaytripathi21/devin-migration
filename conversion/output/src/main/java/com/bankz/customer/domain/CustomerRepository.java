package com.bankz.customer.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, CustomerKey> {

    /** GET-LAST-CUSTOMER-DB2: ORDER BY CUSTOMER_NUMBER DESC FETCH FIRST 1 ROW ONLY. */
    Optional<Customer> findFirstByKeySortCodeOrderByKeyNumberDesc(String sortCode);
}
