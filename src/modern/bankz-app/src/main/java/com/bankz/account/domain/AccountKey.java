package com.bankz.account.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

/** ACCOUNT key: ACCOUNT_SORTCODE CHAR(6) + ACCOUNT_NUMBER CHAR(8), zero padded. */
@Embeddable
public record AccountKey(
        @Column(name = "account_sortcode", length = 6) String sortCode,
        @Column(name = "account_number", length = 8) String number) implements Serializable {

    public static AccountKey of(long sortCode, long number) {
        return new AccountKey(String.format("%06d", sortCode), String.format("%08d", number));
    }
}
