package com.bankz.customer.web;

import com.bankz.customer.domain.Customer;

/**
 * Same JSON as the z/OS Connect operation GET /customers/{customerId}
 * (src/api/src/main/operations/.../get/response_200.yaml).
 */
public record CustomerResponse(
        String customerId,
        String title,
        String firstName,
        String lastName,
        String dateOfBirth,
        String phoneNumber,
        Address address,
        String customerStatus,
        String createdDate) {

    public record Address(String addressLine1, String addressLine2, String city, String postalCode, String country) {
    }

    public static CustomerResponse from(Customer c) {
        return new CustomerResponse(
                c.getKey().number().trim(),
                text(c.getTitle()),
                text(c.getFirstName()),
                text(c.getLastName()),
                date(c.getDateOfBirth()),
                text(c.getPhone()),
                new Address(text(c.getAddressLine1()), text(c.getAddressLine2()), text(c.getCity()),
                        text(c.getPostcode()), text(c.getCountry())),
                text(c.getStatus()),
                date(c.getCreatedDate()) + "T00:00:00Z");
    }

    /** DB2 CHAR columns are blank padded; z/OS Connect returns them without trailing blanks. */
    private static String text(String value) {
        return value == null ? "" : value.stripTrailing();
    }

    /** yyyymmdd integer to YYYY-MM-DD, using the same DIVIDE/MOD arithmetic as INQCUST. */
    static String date(Integer yyyymmdd) {
        int v = yyyymmdd == null ? 0 : yyyymmdd;
        return String.format("%04d-%02d-%02d", v / 10000, (v / 100) % 100, v % 100);
    }
}
