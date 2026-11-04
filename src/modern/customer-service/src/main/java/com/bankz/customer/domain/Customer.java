package com.bankz.customer.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/** Row of the DB2 table BANKZ.CUSTOMER (copybook CUSTDB2), now in PostgreSQL. Dates are DB2-style yyyymmdd integers. */
@Entity
@Table(name = "customer")
public class Customer {

    @EmbeddedId
    private CustomerKey key;

    @Column(name = "customer_eyecatcher")
    private String eyecatcher;
    @Column(name = "customer_title")
    private String title;
    @Column(name = "customer_first_name")
    private String firstName;
    @Column(name = "customer_last_name")
    private String lastName;
    @Column(name = "customer_date_of_birth")
    private Integer dateOfBirth;
    @Column(name = "customer_phone")
    private String phone;
    @Column(name = "customer_addr_line1")
    private String addressLine1;
    @Column(name = "customer_addr_line2")
    private String addressLine2;
    @Column(name = "customer_city")
    private String city;
    @Column(name = "customer_postcode")
    private String postcode;
    @Column(name = "customer_country")
    private String country;
    @Column(name = "customer_status")
    private String status;
    @Column(name = "customer_created_date")
    private Integer createdDate;
    @Column(name = "customer_credit_score")
    private Short creditScore;
    @Column(name = "customer_cs_review_date")
    private Integer creditScoreReviewDate;

    protected Customer() {
    }

    public Customer(CustomerKey key, String title, String firstName, String lastName, Integer dateOfBirth,
                    String phone, String addressLine1, String addressLine2, String city, String postcode,
                    String country, String status, Integer createdDate, Short creditScore,
                    Integer creditScoreReviewDate) {
        this.key = key;
        this.eyecatcher = "CUST";
        this.title = title;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.phone = phone;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.city = city;
        this.postcode = postcode;
        this.country = country;
        this.status = status;
        this.createdDate = createdDate;
        this.creditScore = creditScore;
        this.creditScoreReviewDate = creditScoreReviewDate;
    }

    public CustomerKey getKey() { return key; }
    public String getEyecatcher() { return eyecatcher; }
    public String getTitle() { return title; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public Integer getDateOfBirth() { return dateOfBirth; }
    public String getPhone() { return phone; }
    public String getAddressLine1() { return addressLine1; }
    public String getAddressLine2() { return addressLine2; }
    public String getCity() { return city; }
    public String getPostcode() { return postcode; }
    public String getCountry() { return country; }
    public String getStatus() { return status; }
    public Integer getCreatedDate() { return createdDate; }
    public Short getCreditScore() { return creditScore; }
    public Integer getCreditScoreReviewDate() { return creditScoreReviewDate; }
}
