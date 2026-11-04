package com.bankz.batch;

import java.util.List;

/** Output of a statement job: JCL step return code, SYSOUT messages and the SYSPRINT report lines. */
public record StatementResult(int returnCode, List<String> messages, List<String> report, int statements) {
}
