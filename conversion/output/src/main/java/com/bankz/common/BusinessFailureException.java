package com.bankz.common;

import org.springframework.http.HttpStatus;

/** A COBOL commarea returned with SUCCESS = 'N'; failCode is the program's FAIL-CODE value. */
public class BusinessFailureException extends RuntimeException {

    private final String failCode;
    private final HttpStatus status;

    public BusinessFailureException(String failCode, String message, HttpStatus status) {
        super(message);
        this.failCode = failCode;
        this.status = status;
    }

    public String getFailCode() { return failCode; }
    public HttpStatus getStatus() { return status; }
}
