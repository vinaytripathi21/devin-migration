package com.bankz.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;

/** ABNDPROC: persists abend details in its own transaction so they survive the caller's rollback. */
@Service
public class AbendService {

    private static final Logger log = LoggerFactory.getLogger(AbendService.class);

    private final AbendRecordRepository repository;
    private final Clock clock;

    public AbendService(AbendRecordRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AbendRecord record(String program, String abendCode, String message) {
        log.error("Abend {} in {}: {}", abendCode, program, message);
        return repository.save(new AbendRecord(program, abendCode, LocalDateTime.now(clock), message));
    }
}
