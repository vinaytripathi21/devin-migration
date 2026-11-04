package com.bankz.customer.web;

import com.bankz.customer.service.CustomerInquiryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerInquiryService service;

    public CustomerController(CustomerInquiryService service) {
        this.service = service;
    }

    @GetMapping("/{customerId}")
    public CustomerResponse getCustomer(@PathVariable String customerId) {
        if (!customerId.matches("\\d{1,10}")) {
            throw new InvalidCustomerIdException(customerId);
        }
        return CustomerResponse.from(service.inquire(Long.parseLong(customerId)));
    }
}
