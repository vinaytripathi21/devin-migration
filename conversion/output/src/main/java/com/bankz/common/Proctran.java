package com.bankz.common;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Row of BANKZ.PROCTRAN (copybook PROCTRAN): the processed-transaction audit trail. */
@Entity
@Table(name = "proctran")
public class Proctran {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "proctran_id")
    private Long id;
    @Column(name = "proctran_eyecatcher")
    private String eyecatcher;
    @Column(name = "proctran_sortcode")
    private String sortCode;
    @Column(name = "proctran_number")
    private String accountNumber;
    @Column(name = "proctran_date")
    private LocalDate date;
    @Column(name = "proctran_time")
    private String time;
    @Column(name = "proctran_ref")
    private String reference;
    @Column(name = "proctran_type")
    private String type;
    @Column(name = "proctran_desc")
    private String description;
    @Column(name = "proctran_amount")
    private BigDecimal amount;

    protected Proctran() {
    }

    public Proctran(String sortCode, String accountNumber, LocalDate date, String time, String reference,
                    String type, String description, BigDecimal amount) {
        this.eyecatcher = "PRTR";
        this.sortCode = sortCode;
        this.accountNumber = accountNumber;
        this.date = date;
        this.time = time;
        this.reference = reference;
        this.type = type;
        this.description = description;
        this.amount = amount;
    }

    public Long getId() { return id; }
    public String getEyecatcher() { return eyecatcher; }
    public String getSortCode() { return sortCode; }
    public String getAccountNumber() { return accountNumber; }
    public LocalDate getDate() { return date; }
    public String getTime() { return time; }
    public String getReference() { return reference; }
    public String getType() { return type; }
    public String getDescription() { return description; }
    public BigDecimal getAmount() { return amount; }
}
