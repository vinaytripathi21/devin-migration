package com.bankz.common;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/** Record of the ABNDFILE VSAM file written by ABNDPROC. */
@Entity
@Table(name = "abend_record")
public class AbendRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "abend_id")
    private Long id;
    private String program;
    @Column(name = "abend_code")
    private String abendCode;
    @Column(name = "occurred_at")
    private LocalDateTime occurredAt;
    private String message;

    protected AbendRecord() {
    }

    public AbendRecord(String program, String abendCode, LocalDateTime occurredAt, String message) {
        this.program = program;
        this.abendCode = abendCode;
        this.occurredAt = occurredAt;
        this.message = message == null ? null : message.substring(0, Math.min(600, message.length()));
    }

    public Long getId() { return id; }
    public String getProgram() { return program; }
    public String getAbendCode() { return abendCode; }
    public LocalDateTime getOccurredAt() { return occurredAt; }
    public String getMessage() { return message; }
}
