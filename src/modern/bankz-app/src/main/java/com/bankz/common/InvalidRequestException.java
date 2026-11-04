package com.bankz.common;

/** Input that the 3270 screens or z/OS Connect mapping would have rejected before calling the program. */
public class InvalidRequestException extends RuntimeException {

    public InvalidRequestException(String message) {
        super(message);
    }
}
