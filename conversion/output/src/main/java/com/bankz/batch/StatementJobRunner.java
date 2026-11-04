package com.bankz.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Replaces the BNKSTMTC.jcl / BNKSTMTP.jcl job steps. Example:
 * {@code java -jar bankz-app.jar --spring.main.web-application-type=none --bankz.batch.job=BNKSTMTC
 * --bankz.batch.sysin="C000000001 2026-10"}
 */
@Component
@ConditionalOnProperty("bankz.batch.job")
public class StatementJobRunner implements ApplicationRunner, ExitCodeGenerator {

    private static final Logger log = LoggerFactory.getLogger(StatementJobRunner.class);

    private final StatementService service;
    private final String job;
    private final String sysin;
    private final String sortCode;
    private final String dateCard;
    private int returnCode;

    public StatementJobRunner(StatementService service,
                              @Value("${bankz.batch.job}") String job,
                              @Value("${bankz.batch.sysin:}") String sysin,
                              @Value("${bankz.batch.sortcode:}") String sortCode,
                              @Value("${bankz.batch.datecard:}") String dateCard) {
        this.service = service;
        this.job = job;
        this.sysin = sysin;
        this.sortCode = sortCode;
        this.dateCard = dateCard;
    }

    @Override
    public void run(ApplicationArguments args) {
        StatementResult result = switch (job.toUpperCase()) {
            case "BNKSTMTC" -> service.customerStatement(sysin);
            case "BNKSTMTP" -> service.sortCodeStatements(sortCode, dateCard);
            default -> throw new IllegalArgumentException("Unknown batch job " + job);
        };
        result.messages().forEach(m -> log.info("{}: {}", job, m));
        result.report().forEach(System.out::println);
        returnCode = result.returnCode();
        log.info("{} ended with return code {}", job, returnCode);
    }

    @Override
    public int getExitCode() {
        return returnCode;
    }
}
