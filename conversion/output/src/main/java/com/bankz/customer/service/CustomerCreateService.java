package com.bankz.customer.service;

import com.bankz.common.BusinessFailureException;
import com.bankz.common.ControlRecord;
import com.bankz.common.ControlRepository;
import com.bankz.common.LegacyFormat;
import com.bankz.common.ProctranWriter;
import com.bankz.customer.domain.Customer;
import com.bankz.customer.domain.CustomerKey;
import com.bankz.customer.domain.CustomerRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.OptionalInt;
import java.util.random.RandomGenerator;

/** Conversion of CRECUST: validate, credit check, allocate the customer number, insert CUSTOMER and PROCTRAN. */
@Service
public class CustomerCreateService {

    private final CustomerRepository customers;
    private final ControlRepository controls;
    private final ProctranWriter proctran;
    private final CreditAgencyService creditAgencies;
    private final RandomGenerator random;
    private final Clock clock;
    private final long sortCode;

    public CustomerCreateService(CustomerRepository customers, ControlRepository controls, ProctranWriter proctran,
                                 CreditAgencyService creditAgencies, RandomGenerator random, Clock clock,
                                 @Value("${bankz.sort-code:987654}") long sortCode) {
        this.customers = customers;
        this.controls = controls;
        this.proctran = proctran;
        this.creditAgencies = creditAgencies;
        this.random = random;
        this.clock = clock;
        this.sortCode = sortCode;
    }

    @Transactional
    public Customer create(NewCustomer c) {
        if (!Titles.isValid(c.title())) {
            throw failure("T", "Invalid title: " + c.title());
        }
        LocalDate today = LocalDate.now(clock);
        int dob = validateDateOfBirth(c, today);

        OptionalInt score = creditAgencies.averageScore();
        LocalDate reviewDate = score.isPresent() ? today.plusDays(random.nextInt(20) + 1) : today;

        String sort = LegacyFormat.zeroPad(sortCode, 6);
        ControlRecord counter = controls.lockByName("BANKZCUST" + sort)
                .orElseThrow(() -> failure("4", "Customer number counter BANKZCUST" + sort + " is missing"));
        int number = counter.getValueNum() + 1;
        counter.setValueNum(number);

        Customer customer = customers.save(new Customer(CustomerKey.of(sortCode, number), c.title(), c.firstName(),
                c.lastName(), dob, c.phone(), c.addressLine1(), c.addressLine2(), c.city(), c.postcode(),
                c.country(), c.status(), LegacyFormat.yyyymmdd(today), (short) score.orElse(0),
                LegacyFormat.yyyymmdd(reviewDate)));

        proctran.write(sort, "00000000", "OCC", description(sort, customer), BigDecimal.ZERO);
        return customer;
    }

    /** CRECUST DATE-OF-BIRTH-CHECK: 'O' too old or before 1601, 'Z' not a date, 'Y' in the future. */
    private static int validateDateOfBirth(NewCustomer c, LocalDate today) {
        if (c.dobYear() < 1601) {
            throw failure("O", "Date of birth year must be 1601 or later");
        }
        LocalDate dob;
        try {
            dob = LocalDate.of(c.dobYear(), c.dobMonth(), c.dobDay());
        } catch (DateTimeException e) {
            throw failure("Z", "Date of birth is not a valid date");
        }
        if (today.getYear() - dob.getYear() > 150) {
            throw failure("O", "Customer would be older than 150");
        }
        if (dob.isAfter(today)) {
            throw failure("Y", "Date of birth is in the future");
        }
        return LegacyFormat.yyyymmdd(dob);
    }

    /** PROCTRAN-DESC: sort code(6) + customer number(10) + name(14) + date of birth DD/MM/YYYY(10). */
    static String description(String sort, Customer customer) {
        return sort + customer.getKey().number()
                + LegacyFormat.pad(LegacyFormat.fullName(customer.getFirstName(), customer.getLastName()), 14)
                + LegacyFormat.ddSlashMmSlashYyyy(customer.getDateOfBirth());
    }

    private static BusinessFailureException failure(String code, String message) {
        return new BusinessFailureException(code, message, HttpStatus.BAD_REQUEST);
    }
}
