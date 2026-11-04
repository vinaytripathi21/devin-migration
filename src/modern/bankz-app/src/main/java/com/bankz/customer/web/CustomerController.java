package com.bankz.customer.web;

import com.bankz.account.service.AccountService;
import com.bankz.account.web.AccountController.AccountList;
import com.bankz.common.InvalidRequestException;
import com.bankz.common.LegacyFormat;
import com.bankz.customer.domain.Customer;
import com.bankz.customer.service.CustomerChanges;
import com.bankz.customer.service.CustomerCreateService;
import com.bankz.customer.service.CustomerDeleteService;
import com.bankz.customer.service.CustomerInquiryService;
import com.bankz.customer.service.CustomerUpdateService;
import com.bankz.customer.service.NewCustomer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** REST replacement for the z/OS Connect /customers operations (INQCUST, CRECUST, UPDCUST, DELCUS, INQACCCU). */
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerInquiryService inquiry;
    private final CustomerCreateService create;
    private final CustomerUpdateService update;
    private final CustomerDeleteService delete;
    private final AccountService accounts;

    public CustomerController(CustomerInquiryService inquiry, CustomerCreateService create,
                              CustomerUpdateService update, CustomerDeleteService delete, AccountService accounts) {
        this.inquiry = inquiry;
        this.create = create;
        this.update = update;
        this.delete = delete;
        this.accounts = accounts;
    }

    public record CustomerRequest(String title, String firstName, String lastName, String dateOfBirth,
                                  String phoneNumber, CustomerResponse.Address address, String customerStatus) {
        CustomerResponse.Address addr() {
            return address == null ? new CustomerResponse.Address(null, null, null, null, null) : address;
        }
    }

    public record CreatedCustomer(String customerId, String sortCode) {
    }

    @GetMapping("/{customerId}")
    public CustomerResponse getCustomer(@PathVariable String customerId) {
        return CustomerResponse.from(inquiry.inquire(customerNumber(customerId)));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreatedCustomer createCustomer(@RequestBody CustomerRequest r) {
        if (r.firstName() == null || r.lastName() == null) {
            throw new InvalidRequestException("firstName and lastName are required");
        }
        int[] dob = dateParts(r.dateOfBirth());
        CustomerResponse.Address a = r.addr();
        Customer c = create.create(new NewCustomer(r.title(), r.firstName(), r.lastName(), dob[0], dob[1], dob[2],
                r.phoneNumber(), a.addressLine1(), a.addressLine2(), a.city(), a.postalCode(), a.country(),
                r.customerStatus()));
        return new CreatedCustomer(c.getKey().number(), c.getKey().sortCode());
    }

    @PutMapping("/{customerId}")
    public CustomerResponse updateCustomer(@PathVariable String customerId, @RequestBody CustomerRequest r) {
        Integer dob = null;
        if (r.dateOfBirth() != null && !r.dateOfBirth().isBlank()) {
            int[] p = dateParts(r.dateOfBirth());
            dob = p[0] * 10000 + p[1] * 100 + p[2];
        }
        CustomerResponse.Address a = r.addr();
        return CustomerResponse.from(update.update(customerNumber(customerId), new CustomerChanges(r.title(),
                r.firstName(), r.lastName(), dob, r.phoneNumber(), a.addressLine1(), a.addressLine2(), a.city(),
                a.postalCode(), a.country(), r.customerStatus())));
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable String customerId) {
        delete.delete(customerNumber(customerId));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{customerId}/accounts")
    public AccountList customerAccounts(@PathVariable String customerId) {
        return AccountList.of(accounts.listForCustomer(customerNumber(customerId)));
    }

    private static long customerNumber(String customerId) {
        if (customerId == null || !customerId.matches("\\d{1,10}")) {
            throw new InvalidCustomerIdException(customerId);
        }
        return Long.parseLong(customerId);
    }

    /** YYYY-MM-DD split without validation, so CRECUST can report impossible dates with fail code 'Z'. */
    private static int[] dateParts(String date) {
        if (date == null || !date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new InvalidRequestException("dateOfBirth must be YYYY-MM-DD: " + LegacyFormat.trim(date));
        }
        return new int[]{Integer.parseInt(date.substring(0, 4)), Integer.parseInt(date.substring(5, 7)),
                Integer.parseInt(date.substring(8, 10))};
    }
}
