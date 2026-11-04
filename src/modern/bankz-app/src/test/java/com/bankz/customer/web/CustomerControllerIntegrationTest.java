package com.bankz.customer.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Runs Flyway migrations and the demo data against an in-memory database in PostgreSQL mode. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"test", "demo"})
class CustomerControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void returnsCustomerInZosConnectFormat() throws Exception {
        mvc.perform(get("/api/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value("0000000001"))
                .andExpect(jsonPath("$.title").value("Mr"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Smith"))
                .andExpect(jsonPath("$.dateOfBirth").value("1985-06-15"))
                .andExpect(jsonPath("$.address.city").value("London"))
                .andExpect(jsonPath("$.address.postalCode").value("SW1A 1AA"))
                .andExpect(jsonPath("$.customerStatus").value("ACTIVE"))
                .andExpect(jsonPath("$.createdDate").value("2020-01-15T00:00:00Z"));
    }

    @Test
    void customerNumber9999999999ReturnsHighestCustomer() throws Exception {
        mvc.perform(get("/api/customers/9999999999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value("0000000042"));
    }

    @Test
    void customerNumber0ReturnsAnExistingCustomer() throws Exception {
        mvc.perform(get("/api/customers/0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").exists());
    }

    @Test
    void unknownCustomerReturns404WithFailCode1() throws Exception {
        mvc.perform(get("/api/customers/7"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.failCode").value("1"));
    }

    @Test
    void nonNumericCustomerIdReturns400() throws Exception {
        mvc.perform(get("/api/customers/C000000001"))
                .andExpect(status().isBadRequest());
    }
}
