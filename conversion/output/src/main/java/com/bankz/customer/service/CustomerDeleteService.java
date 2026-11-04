package com.bankz.customer.service;

import com.bankz.account.domain.Account;
import com.bankz.account.service.AccountService;
import com.bankz.common.LegacyFormat;
import com.bankz.common.ProctranWriter;
import com.bankz.customer.domain.Customer;
import com.bankz.customer.domain.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/** Conversion of DELCUS: delete every account (DELACC), then the customer, and log PROCTRAN type ODC. */
@Service
public class CustomerDeleteService {

    private final CustomerInquiryService inquiry;
    private final AccountService accounts;
    private final CustomerRepository customers;
    private final ProctranWriter proctran;

    public CustomerDeleteService(CustomerInquiryService inquiry, AccountService accounts,
                                 CustomerRepository customers, ProctranWriter proctran) {
        this.inquiry = inquiry;
        this.accounts = accounts;
        this.customers = customers;
        this.proctran = proctran;
    }

    @Transactional
    public Customer delete(long customerNumber) {
        Customer customer = inquiry.inquire(customerNumber);
        long number = Long.parseLong(customer.getKey().number().trim());
        for (Account account : accounts.listForCustomer(number)) {
            accounts.delete(Long.parseLong(account.getKey().number().trim()));
        }
        customers.delete(customer);
        String sort = customer.getKey().sortCode();
        proctran.write(sort, "00000000", "ODC", CustomerCreateService.description(sort, customer), BigDecimal.ZERO);
        return customer;
    }
}
