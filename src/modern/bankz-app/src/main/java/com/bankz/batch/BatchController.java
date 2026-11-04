package com.bankz.batch;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** On-demand runs of the batch programs (the JCL equivalents are in StatementJobRunner). */
@RestController
@RequestMapping("/api/batch")
public class BatchController {

    private final StatementService statements;
    private final BankDataGenerator bankData;

    public BatchController(StatementService statements, BankDataGenerator bankData) {
        this.statements = statements;
        this.bankData = bankData;
    }

    /** BNKSTMTC, e.g. ?customerId=C000000001&period=2026-10. */
    @GetMapping("/statements")
    public StatementResult customerStatement(@RequestParam String customerId,
                                             @RequestParam(required = false) String period) {
        return statements.customerStatement(period == null ? customerId : customerId + " " + period);
    }

    /** BNKSTMTP, e.g. ?sortCode=987654&reportingMonth=202610. */
    @GetMapping("/statements/sortcode")
    public StatementResult sortCodeStatements(@RequestParam(required = false) String sortCode,
                                              @RequestParam(required = false) String reportingMonth) {
        return statements.sortCodeStatements(sortCode, reportingMonth);
    }

    /** BANKDATA. */
    @PostMapping("/bankdata")
    public BankDataGenerator.Generated bankData(@RequestParam long start, @RequestParam long end,
                                                @RequestParam(defaultValue = "1") long step,
                                                @RequestParam(defaultValue = "1") long seed) {
        return bankData.generate(start, end, step, seed);
    }
}
