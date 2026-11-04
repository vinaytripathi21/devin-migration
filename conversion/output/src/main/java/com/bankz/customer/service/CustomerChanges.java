package com.bankz.customer.service;

/** UPDCUST commarea input. Blank fields are left unchanged; dateOfBirth is yyyymmdd or null. */
public record CustomerChanges(String title, String firstName, String lastName, Integer dateOfBirth, String phone,
                              String addressLine1, String addressLine2, String city, String postcode,
                              String country, String status) {
}
