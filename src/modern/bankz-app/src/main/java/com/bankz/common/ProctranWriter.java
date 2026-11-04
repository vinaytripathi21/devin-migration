package com.bankz.common;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;

/** The WRITE-PROCTRAN-DB2 section shared by CRECUST, DELCUS, CREACC, DELACC, DBCRFUN and XFRFUN. */
@Service
public class ProctranWriter {

    private final ProctranRepository repository;
    private final JdbcTemplate jdbc;
    private final Clock clock;

    public ProctranWriter(ProctranRepository repository, JdbcTemplate jdbc, Clock clock) {
        this.repository = repository;
        this.jdbc = jdbc;
        this.clock = clock;
    }

    public Proctran write(String sortCode, String accountNumber, String type, String description, BigDecimal amount) {
        LocalDateTime now = LocalDateTime.now(clock);
        Long ref = jdbc.queryForObject("select nextval('proctran_ref_seq')", Long.class);
        Proctran row = new Proctran(sortCode, accountNumber, now.toLocalDate(), LegacyFormat.hhmmss(now.toLocalTime()),
                LegacyFormat.zeroPad(ref == null ? 0 : ref, 12), type, LegacyFormat.pad(description, 40), amount);
        return repository.save(row);
    }
}
