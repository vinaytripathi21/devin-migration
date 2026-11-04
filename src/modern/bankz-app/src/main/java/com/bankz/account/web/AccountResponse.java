package com.bankz.account.web;

import com.bankz.account.domain.Account;
import com.bankz.common.LegacyFormat;

import java.math.BigDecimal;

/** JSON of the z/OS Connect Account schema (operation GET /accounts/{accountId}, response_200.yaml). */
public record AccountResponse(String accountId, String accountType, String currency, String accountNumber,
                              String sortCode, String status, String openingDate, String customerId,
                              BigDecimal interestRate, Integer overdraftLimit, BigDecimal availableBalance,
                              BigDecimal actualBalance, String lastStatementDate, String nextStatementDate) {

    public static AccountResponse from(Account a) {
        String number = LegacyFormat.trim(a.getKey().number());
        return new AccountResponse(number, LegacyFormat.trim(a.getType()), "GBP", number, a.getKey().sortCode(),
                "ACTIVE", String.valueOf(a.getOpened()), LegacyFormat.trim(a.getCustomerNumber()),
                a.getInterestRate(), a.getOverdraftLimit(), a.getAvailableBalance(), a.getActualBalance(),
                String.valueOf(a.getLastStatement()), String.valueOf(a.getNextStatement()));
    }
}
