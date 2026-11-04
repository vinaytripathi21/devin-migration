package com.bankz.customer.service;

import com.bankz.customer.domain.Customer;
import com.bankz.customer.domain.CustomerKey;
import com.bankz.customer.domain.CustomerRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.random.RandomGenerator;

/**
 * Conversion of INQCUST. Customer number 0 returns a random customer and 9999999999 returns the
 * customer with the highest number, as in the COBOL program.
 */
@Service
@Transactional(readOnly = true)
public class CustomerInquiryService {

    public static final long RANDOM_CUSTOMER = 0L;
    public static final long LAST_CUSTOMER = 9_999_999_999L;
    static final int MAX_RANDOM_RETRIES = 1000;

    private final CustomerRepository repository;
    private final RandomGenerator random;
    private final long defaultSortCode;

    public CustomerInquiryService(CustomerRepository repository, RandomGenerator random,
                                  @Value("${bankz.sort-code:987654}") long defaultSortCode) {
        this.repository = repository;
        this.random = random;
        this.defaultSortCode = defaultSortCode;
    }

    public Customer inquire(long customerNumber) {
        return inquire(defaultSortCode, customerNumber);
    }

    public Customer inquire(long sortCode, long customerNumber) {
        if (customerNumber == LAST_CUSTOMER) {
            return lastCustomer(sortCode);
        }
        if (customerNumber == RANDOM_CUSTOMER) {
            return randomCustomer(sortCode);
        }
        return repository.findById(CustomerKey.of(sortCode, customerNumber))
                .orElseThrow(() -> new CustomerNotFoundException(
                        "Customer " + customerNumber + " not found", "1"));
    }

    private Customer lastCustomer(long sortCode) {
        long last = highestCustomerNumber(sortCode);
        Optional<Customer> customer = repository.findById(CustomerKey.of(sortCode, last));
        if (customer.isEmpty()) {
            // The last customer was deleted between the two reads: retry once, as INQCUST does.
            customer = repository.findById(CustomerKey.of(sortCode, highestCustomerNumber(sortCode)));
        }
        return customer.orElseThrow(() -> new CustomerNotFoundException("Last customer not found", "1"));
    }

    private Customer randomCustomer(long sortCode) {
        long highest = highestCustomerNumber(sortCode);
        for (int attempt = 0; attempt <= MAX_RANDOM_RETRIES; attempt++) {
            // COMPUTE RANDOM-CUSTOMER = ((NCS-CUST-NO-VALUE - 1) * FUNCTION RANDOM) + 1, truncated.
            long candidate = (long) ((highest - 1) * random.nextDouble()) + 1;
            Optional<Customer> customer = repository.findById(CustomerKey.of(sortCode, candidate));
            if (customer.isPresent()) {
                return customer.get();
            }
        }
        throw new CustomerNotFoundException("No random customer found", "1");
    }

    private long highestCustomerNumber(long sortCode) {
        return repository.findFirstByKeySortCodeOrderByKeyNumberDesc(String.format("%06d", sortCode))
                .map(c -> Long.parseLong(c.getKey().number().trim()))
                .orElseThrow(() -> new CustomerNotFoundException("No customers for sort code " + sortCode, "9"));
    }
}
