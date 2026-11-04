package com.bankz.customer.service;

import java.util.Set;

/** Title validation shared by CRECUST and UPDCUST (fail code 'T'). */
final class Titles {

    private static final Set<String> VALID = Set.of(
            "Professor", "Mr", "Mrs", "Miss", "Ms", "Dr", "Drs", "Lord", "Sir", "Lady", "");

    private Titles() {
    }

    static boolean isValid(String title) {
        return VALID.contains(title == null ? "" : title.trim());
    }
}
