package com.bankz.account.web;

import com.bankz.account.domain.Account;
import com.bankz.account.service.AccountService;
import com.bankz.account.service.NewAccount;
import com.bankz.common.InvalidRequestException;
import com.bankz.common.web.Ids;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/** REST replacement for the z/OS Connect /accounts operations (CREACC, INQACC, INQACCS, UPDACC, DELACC). */
@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService service;

    public AccountController(AccountService service) {
        this.service = service;
    }

    public record CreateAccountRequest(String customerNumber, String accountType, BigDecimal interestRate,
                                       Integer overdraftLimit, BigDecimal initialBalance) {
    }

    public record CreatedAccount(String accountId, String sortCode, String openedDate, BigDecimal availableBalance,
                                 BigDecimal actualBalance) {
    }

    public record UpdateAccountRequest(String accountType, BigDecimal interestRate, Integer overdraftLimit) {
    }

    public record AccountList(List<AccountResponse> accounts, int totalCount) {
        public static AccountList of(List<Account> accounts) {
            return new AccountList(accounts.stream().map(AccountResponse::from).toList(), accounts.size());
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreatedAccount create(@RequestBody CreateAccountRequest r) {
        if (r.accountType() == null) {
            throw new InvalidRequestException("accountType is required");
        }
        BigDecimal balance = r.initialBalance() == null ? BigDecimal.ZERO : r.initialBalance();
        Account a = service.create(new NewAccount(Ids.parse(r.customerNumber(), 10, "customerNumber"),
                r.accountType().toUpperCase(), r.interestRate() == null ? BigDecimal.ZERO : r.interestRate(),
                r.overdraftLimit() == null ? 0 : r.overdraftLimit(), balance, balance));
        return new CreatedAccount(a.getKey().number(), a.getKey().sortCode(), String.valueOf(a.getOpened()),
                a.getAvailableBalance(), a.getActualBalance());
    }

    @GetMapping
    public AccountList list(@RequestParam(required = false) String accountType) {
        List<Account> all = service.listAll();
        if (accountType != null && !accountType.isBlank()) {
            all = all.stream().filter(a -> a.getType().trim().equalsIgnoreCase(accountType.trim())).toList();
        }
        return AccountList.of(all);
    }

    @GetMapping("/{accountId}")
    public AccountResponse get(@PathVariable String accountId) {
        return AccountResponse.from(service.inquire(Ids.account(accountId)));
    }

    @GetMapping("/{accountId}/balances")
    public Map<String, Object> balances(@PathVariable String accountId) {
        Account a = service.inquire(Ids.account(accountId));
        return Map.of("balanceType", "AVAILABLE", "amount", a.getAvailableBalance(), "currency", "GBP",
                "creditDebitIndicator", a.getAvailableBalance().signum() < 0 ? "DEBIT" : "CREDIT",
                "dateTime", a.getLastStatement() + "T00:00:00Z",
                "actualBalance", a.getActualBalance());
    }

    @PutMapping("/{accountId}")
    public AccountResponse update(@PathVariable String accountId, @RequestBody UpdateAccountRequest r) {
        return AccountResponse.from(service.update(Ids.account(accountId),
                r.accountType() == null ? null : r.accountType().toUpperCase(), r.interestRate(),
                r.overdraftLimit()));
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<Void> delete(@PathVariable String accountId) {
        service.delete(Ids.account(accountId));
        return ResponseEntity.noContent().build();
    }
}
