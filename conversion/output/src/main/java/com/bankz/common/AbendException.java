package com.bankz.common;

/** Replaces EXEC CICS ABEND ABCODE(...): the request fails, the transaction rolls back and the abend is recorded. */
public class AbendException extends RuntimeException {

    private final String program;
    private final String abendCode;

    public AbendException(String program, String abendCode, String message) {
        super(message);
        this.program = program;
        this.abendCode = abendCode;
    }

    public AbendException(String program, String abendCode, String message, Throwable cause) {
        super(message, cause);
        this.program = program;
        this.abendCode = abendCode;
    }

    public String getProgram() { return program; }
    public String getAbendCode() { return abendCode; }
}
