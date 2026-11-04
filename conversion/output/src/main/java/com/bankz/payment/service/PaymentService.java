package com.bankz.payment.service;

import com.bankz.account.domain.Account;
import com.bankz.account.domain.AccountKey;
import com.bankz.account.domain.AccountRepository;
import com.bankz.common.AbendException;
import com.bankz.common.BusinessFailureException;
import com.bankz.common.LegacyFormat;
import com.bankz.common.Proctran;
import com.bankz.common.ProctranWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/** Conversion of DBCRFUN (debit/credit) and XFRFUN (transfer between accounts). */
@Service
public class PaymentService {

    /** COMM-FACILTYPE 496: payment from the payment interface rather than a teller counter. */
    public static final int PAYMENT_FACILITY = 496;

    private final AccountRepository accounts;
    private final ProctranWriter proctran;
    private final long sortCode;

    public PaymentService(AccountRepository accounts, ProctranWriter proctran,
                          @Value("${bankz.sort-code:987654}") long sortCode) {
        this.accounts = accounts;
        this.proctran = proctran;
        this.sortCode = sortCode;
    }

    public record PaymentResult(Account account, Proctran transaction) {
    }

    public record TransferResult(Account from, Account to, Proctran transaction) {
    }

    /** DBCRFUN: a negative amount is a debit. */
    @Transactional
    public PaymentResult debitCredit(long accountNumber, BigDecimal amount, int facilityType, String origin) {
        Account account = accounts.lockById(AccountKey.of(sortCode, accountNumber))
                .orElseThrow(() -> new BusinessFailureException("1", "Account " + accountNumber + " not found",
                        HttpStatus.NOT_FOUND));
        boolean payment = facilityType == PAYMENT_FACILITY;
        String type = LegacyFormat.trim(account.getType());
        if (payment && (type.equals("MORTGAGE") || type.equals("LOAN"))) {
            throw new BusinessFailureException("4", "Payments are not allowed on " + type + " accounts",
                    HttpStatus.BAD_REQUEST);
        }
        boolean debit = amount.signum() < 0;
        if (debit && payment && account.getAvailableBalance().add(amount).signum() < 0) {
            throw new BusinessFailureException("3", "Insufficient funds", HttpStatus.BAD_REQUEST);
        }
        account.applyAmount(amount);

        String description = debit ? "COUNTER WTHDRW" : "COUNTER RECVED";
        String tranType = debit ? "DEB" : "CRE";
        if (payment) {
            tranType = debit ? "PDR" : "PCR";
            description = origin == null ? "" : origin.substring(0, Math.min(origin.length(), 40));
        }
        Proctran row = proctran.write(account.getKey().sortCode(), account.getKey().number(), tranType,
                description, amount);
        return new PaymentResult(account, row);
    }

    /** XFRFUN: rows are locked in account-number order, as XFRFUN updated them, to avoid deadlocks. */
    @Transactional
    public TransferResult transfer(long fromAccount, long toAccount, BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new BusinessFailureException("4", "Transfer amount must be greater than zero",
                    HttpStatus.BAD_REQUEST);
        }
        if (fromAccount == toAccount) {
            throw new AbendException("XFRFUN", "SAME", "Cannot transfer to the same account");
        }
        Account from;
        Account to;
        if (fromAccount < toAccount) {
            from = lock(fromAccount, "1");
            to = lock(toAccount, "2");
        } else {
            to = lock(toAccount, "2");
            from = lock(fromAccount, "1");
        }
        from.applyAmount(amount.negate());
        to.applyAmount(amount);
        Proctran row = proctran.write(from.getKey().sortCode(), from.getKey().number(), "TFR",
                LegacyFormat.pad("TRANSFER", 26) + to.getKey().sortCode() + to.getKey().number(), amount.negate());
        return new TransferResult(from, to, row);
    }

    private Account lock(long accountNumber, String failCode) {
        return accounts.lockById(AccountKey.of(sortCode, accountNumber))
                .orElseThrow(() -> new BusinessFailureException(failCode, "Account " + accountNumber + " not found",
                        HttpStatus.NOT_FOUND));
    }
}
