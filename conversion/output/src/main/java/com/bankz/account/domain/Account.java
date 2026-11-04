package com.bankz.account.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Row of the DB2 table BANKZ.ACCOUNT (copybooks ACCOUNT / ACCDB2), now in PostgreSQL. */
@Entity
@Table(name = "account")
public class Account {

    @EmbeddedId
    private AccountKey key;

    @Column(name = "account_eyecatcher")
    private String eyecatcher;
    @Column(name = "account_customer_number")
    private String customerNumber;
    @Column(name = "account_type")
    private String type;
    @Column(name = "account_interest_rate")
    private BigDecimal interestRate;
    @Column(name = "account_opened")
    private LocalDate opened;
    @Column(name = "account_overdraft_limit")
    private Integer overdraftLimit;
    @Column(name = "account_last_statement")
    private LocalDate lastStatement;
    @Column(name = "account_next_statement")
    private LocalDate nextStatement;
    @Column(name = "account_available_balance")
    private BigDecimal availableBalance;
    @Column(name = "account_actual_balance")
    private BigDecimal actualBalance;

    protected Account() {
    }

    public Account(AccountKey key, String customerNumber, String type, BigDecimal interestRate, LocalDate opened,
                   Integer overdraftLimit, LocalDate lastStatement, LocalDate nextStatement,
                   BigDecimal availableBalance, BigDecimal actualBalance) {
        this.key = key;
        this.eyecatcher = "ACCT";
        this.customerNumber = customerNumber;
        this.type = type;
        this.interestRate = interestRate;
        this.opened = opened;
        this.overdraftLimit = overdraftLimit;
        this.lastStatement = lastStatement;
        this.nextStatement = nextStatement;
        this.availableBalance = availableBalance;
        this.actualBalance = actualBalance;
    }

    /** UPDACC changes only these three columns. */
    public void update(String type, BigDecimal interestRate, Integer overdraftLimit) {
        this.type = type;
        this.interestRate = interestRate;
        this.overdraftLimit = overdraftLimit;
    }

    /** DBCRFUN / XFRFUN: both balances move by the same amount. */
    public void applyAmount(BigDecimal amount) {
        this.availableBalance = this.availableBalance.add(amount);
        this.actualBalance = this.actualBalance.add(amount);
    }

    /** BNKSTMTC: statement dates roll forward after a statement is produced. */
    public void recordStatement(LocalDate last, LocalDate next) {
        this.lastStatement = last;
        this.nextStatement = next;
    }

    public AccountKey getKey() { return key; }
    public String getEyecatcher() { return eyecatcher; }
    public String getCustomerNumber() { return customerNumber; }
    public String getType() { return type; }
    public BigDecimal getInterestRate() { return interestRate; }
    public LocalDate getOpened() { return opened; }
    public Integer getOverdraftLimit() { return overdraftLimit; }
    public LocalDate getLastStatement() { return lastStatement; }
    public LocalDate getNextStatement() { return nextStatement; }
    public BigDecimal getAvailableBalance() { return availableBalance; }
    public BigDecimal getActualBalance() { return actualBalance; }
}
