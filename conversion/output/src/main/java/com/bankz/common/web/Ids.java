package com.bankz.common.web;

import com.bankz.common.InvalidRequestException;

/** Path/body identifiers: PIC 9(n) numbers sent as strings. */
public final class Ids {

    private Ids() {
    }

    public static long parse(String value, int maxDigits, String name) {
        if (value == null || !value.matches("\\d{1," + maxDigits + "}")) {
            throw new InvalidRequestException(name + " must be 1 to " + maxDigits + " digits: " + value);
        }
        return Long.parseLong(value);
    }

    /** Account ids are an 8-digit number, optionally prefixed with "sortcode-" as the frontend sends them. */
    public static long account(String value) {
        String v = value != null && value.matches("\\d{6}-\\d{1,8}") ? value.substring(7) : value;
        return parse(v, 8, "accountId");
    }
}
