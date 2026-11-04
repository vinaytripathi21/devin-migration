package com.bankz.ims;

import com.bankz.common.web.Ids;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

/** REST replacement for the IMS transactions (IMS customer ids use the "I" prefix in the frontend). */
@RestController
@RequestMapping("/api/ims")
public class ImsController {

    private final ImsService service;

    public ImsController(ImsService service) {
        this.service = service;
    }

    public record LoginRequest(String customerId, String password) {
    }

    public record TransactionRequest(String customerId, BigDecimal amount, String type) {
    }

    @GetMapping("/customers/{customerId}")
    public ImsService.CustomerReply customer(@PathVariable String customerId) {
        return service.getCustomer(id(customerId));
    }

    @PutMapping("/customers/{customerId}")
    public ImsService.CustomerReply update(@PathVariable String customerId, @RequestBody ImsService.ImsCustomer c) {
        return service.updateCustomer(new ImsService.ImsCustomer(id(customerId), c.lastName(), c.firstName(),
                c.address(), c.city(), c.state(), c.zipCode(), c.phone()));
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginRequest r) {
        return Map.of("message", service.login(id(r.customerId()), r.password()));
    }

    @PostMapping("/logout")
    public Map<String, String> logout(@RequestBody LoginRequest r) {
        return Map.of("message", service.logout(id(r.customerId())));
    }

    @GetMapping("/customers/{customerId}/accounts")
    public ImsService.SummaryReply accounts(@PathVariable String customerId) {
        return service.accountSummary(id(customerId));
    }

    @PostMapping("/accounts/{accountId}/transactions")
    public ImsService.TransactionReply transaction(@PathVariable String accountId,
                                                   @RequestBody TransactionRequest r) {
        return service.transaction(Ids.parse(accountId, 18, "accountId"), r.amount() == null ? BigDecimal.ZERO
                : r.amount(), r.type(), r.customerId() == null ? 0 : id(r.customerId()));
    }

    /** Paths used by src/frontend/js/api.js for IMS customers. */
    @GetMapping({"/accounts/{customerId}", "/accounts/{customerId}/balances"})
    public ImsService.SummaryReply accountsByCustomer(@PathVariable String customerId) {
        return service.accountSummary(id(customerId));
    }

    @PostMapping("/accounts/{customerId}/{accountId}/deposit")
    public ImsService.TransactionReply deposit(@PathVariable String customerId, @PathVariable String accountId,
                                               @RequestBody TransactionRequest r) {
        BigDecimal amount = r.amount() == null ? BigDecimal.ZERO : r.amount();
        return service.transaction(Ids.parse(accountId, 18, "accountId"), amount.abs(),
                amount.signum() < 0 ? "w" : "d", id(customerId));
    }

    private static int id(String customerId) {
        String digits = customerId != null && customerId.toUpperCase().startsWith("I")
                ? customerId.substring(1) : customerId;
        return (int) Ids.parse(digits, 9, "customerId");
    }
}
