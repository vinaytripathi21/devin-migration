package com.bankz.account.service;

import com.bankz.account.domain.Account;
import com.bankz.account.domain.AccountKey;
import com.bankz.account.domain.AccountRepository;
import com.bankz.common.BusinessFailureException;
import com.bankz.common.ControlRecord;
import com.bankz.common.ControlRepository;
import com.bankz.common.LegacyFormat;
import com.bankz.common.ProctranWriter;
import com.bankz.customer.service.CustomerInquiryService;
import com.bankz.customer.service.CustomerNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

/** Conversion of CREACC, INQACC, INQACCS, INQACCCU, UPDACC and DELACC. */
@Service
public class AccountService {

    public static final long LAST_ACCOUNT = 99_999_999L;
    static final int MAX_ACCOUNTS_PER_CUSTOMER = 9;
    private static final List<String> ACCOUNT_TYPES = List.of("ISA", "MORTGAGE", "SAVING", "CURRENT", "LOAN");

    private final AccountRepository accounts;
    private final ControlRepository controls;
    private final ProctranWriter proctran;
    private final CustomerInquiryService customers;
    private final Clock clock;
    private final long sortCode;

    public AccountService(AccountRepository accounts, ControlRepository controls, ProctranWriter proctran,
                          CustomerInquiryService customers, Clock clock,
                          @Value("${bankz.sort-code:987654}") long sortCode) {
        this.accounts = accounts;
        this.controls = controls;
        this.proctran = proctran;
        this.customers = customers;
        this.clock = clock;
        this.sortCode = sortCode;
    }

    /** CREACC. */
    @Transactional
    public Account create(NewAccount request) {
        try {
            customers.inquire(request.customerNumber());
        } catch (CustomerNotFoundException e) {
            throw new BusinessFailureException("1", "Customer " + request.customerNumber() + " not found",
                    HttpStatus.NOT_FOUND);
        }
        if (listForCustomer(request.customerNumber()).size() > MAX_ACCOUNTS_PER_CUSTOMER) {
            throw new BusinessFailureException("8", "Customer already has the maximum of 10 accounts",
                    HttpStatus.BAD_REQUEST);
        }
        String type = request.type() == null ? "" : request.type();
        if (ACCOUNT_TYPES.stream().noneMatch(type::startsWith)) {
            throw new BusinessFailureException("A", "Invalid account type: " + type, HttpStatus.BAD_REQUEST);
        }

        String sort = LegacyFormat.zeroPad(sortCode, 6);
        ControlRecord last = lockControl(sort + "-ACCOUNT-LAST");
        ControlRecord count = lockControl(sort + "-ACCOUNT-COUNT");
        int number = last.getValueNum() + 1;
        last.setValueNum(number);
        count.setValueNum(count.getValueNum() + 1);

        LocalDate today = LocalDate.now(clock);
        LocalDate next = nextStatementDate(today);
        String customerNumber = LegacyFormat.zeroPad(request.customerNumber(), 10);
        Account account = accounts.save(new Account(AccountKey.of(sortCode, number), customerNumber, type,
                request.interestRate(), today, request.overdraftLimit(), today, next,
                request.availableBalance(), request.actualBalance()));

        proctran.write(sort, account.getKey().number(), "OCA",
                customerNumber + LegacyFormat.pad(type, 8) + LegacyFormat.ddmmyyyy(today)
                        + LegacyFormat.ddmmyyyy(next) + "      ",
                BigDecimal.ZERO);
        return account;
    }

    /** CREACC CALCULATE-DATES: 30 days ahead, or 28/29 days when the account is opened in February. */
    static LocalDate nextStatementDate(LocalDate today) {
        if (today.getMonth() == Month.FEBRUARY) {
            return today.plusDays(today.isLeapYear() ? 29 : 28);
        }
        return today.plusDays(30);
    }

    /** INQACC: 99999999 returns the highest-numbered account. */
    @Transactional(readOnly = true)
    public Account inquire(long accountNumber) {
        String sort = LegacyFormat.zeroPad(sortCode, 6);
        return (accountNumber == LAST_ACCOUNT
                ? accounts.findFirstByKeySortCodeOrderByKeyNumberDesc(sort)
                : accounts.findById(AccountKey.of(sortCode, accountNumber)))
                .orElseThrow(() -> notFound(accountNumber));
    }

    /** INQACCS. */
    @Transactional(readOnly = true)
    public List<Account> listAll() {
        return accounts.findByKeySortCodeOrderByKeyNumberAsc(LegacyFormat.zeroPad(sortCode, 6));
    }

    /** INQACCCU: customer numbers 0 and 9999999999 are rejected, otherwise the customer must exist. */
    @Transactional(readOnly = true)
    public List<Account> listForCustomer(long customerNumber) {
        if (customerNumber == CustomerInquiryService.RANDOM_CUSTOMER
                || customerNumber == CustomerInquiryService.LAST_CUSTOMER) {
            throw new CustomerNotFoundException("Customer " + customerNumber + " not found", "1");
        }
        customers.inquire(customerNumber);
        return accounts.findTop20ByKeySortCodeAndCustomerNumberOrderByKeyNumberAsc(
                LegacyFormat.zeroPad(sortCode, 6), LegacyFormat.zeroPad(customerNumber, 10));
    }

    /** UPDACC: type, interest rate and overdraft limit only; the type must not be blank. */
    @Transactional
    public Account update(long accountNumber, String type, BigDecimal interestRate, Integer overdraftLimit) {
        Account account = accounts.findById(AccountKey.of(sortCode, accountNumber))
                .orElseThrow(() -> notFound(accountNumber));
        if (LegacyFormat.isBlank(type)) {
            throw new BusinessFailureException("2", "Account type must not be blank", HttpStatus.BAD_REQUEST);
        }
        account.update(type,
                interestRate == null ? account.getInterestRate() : interestRate,
                overdraftLimit == null ? account.getOverdraftLimit() : overdraftLimit);
        return account;
    }

    /** DELACC: deletes the account and logs PROCTRAN type ODA with the actual balance. */
    @Transactional
    public Account delete(long accountNumber) {
        Account account = accounts.findById(AccountKey.of(sortCode, accountNumber))
                .orElseThrow(() -> notFound(accountNumber));
        accounts.delete(account);
        proctran.write(account.getKey().sortCode(), account.getKey().number(), "ODA",
                LegacyFormat.pad(account.getCustomerNumber(), 10) + LegacyFormat.pad(account.getType(), 8)
                        + LegacyFormat.ddmmyyyy(account.getLastStatement())
                        + LegacyFormat.ddmmyyyy(account.getNextStatement()) + "DELETE",
                account.getActualBalance());
        return account;
    }

    private ControlRecord lockControl(String name) {
        return controls.lockByName(name).orElseThrow(() ->
                new BusinessFailureException("3", "Account number counter " + name + " is missing",
                        HttpStatus.INTERNAL_SERVER_ERROR));
    }

    private static BusinessFailureException notFound(long accountNumber) {
        return new BusinessFailureException("1", "Account " + accountNumber + " not found", HttpStatus.NOT_FOUND);
    }
}
