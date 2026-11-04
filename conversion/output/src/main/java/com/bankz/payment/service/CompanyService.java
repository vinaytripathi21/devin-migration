package com.bankz.payment.service;

import com.bankz.common.LegacyFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/** Conversion of GETCOMPY and GETSCODE. */
@Service
public class CompanyService {

    static final String COMPANY_NAME = "CICS Bank Sample Application";

    private final long sortCode;

    public CompanyService(@Value("${bankz.sort-code:987654}") long sortCode) {
        this.sortCode = sortCode;
    }

    public String companyName() {
        return COMPANY_NAME;
    }

    public String sortCode() {
        return LegacyFormat.zeroPad(sortCode, 6);
    }
}
