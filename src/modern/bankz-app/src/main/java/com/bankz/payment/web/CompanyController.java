package com.bankz.payment.web;

import com.bankz.payment.service.CompanyService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** GETCOMPY and GETSCODE. */
@RestController
public class CompanyController {

    private final CompanyService service;

    public CompanyController(CompanyService service) {
        this.service = service;
    }

    @GetMapping("/api/company")
    public Map<String, String> company() {
        return Map.of("companyName", service.companyName(), "sortCode", service.sortCode());
    }
}
