package com.bankz.customer.service;

/** CRECUST commarea input. The date of birth is kept as separate fields so invalid dates can be reported. */
public record NewCustomer(String title, String firstName, String lastName, int dobYear, int dobMonth, int dobDay,
                          String phone, String addressLine1, String addressLine2, String city, String postcode,
                          String country, String status) {
}
