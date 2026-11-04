package com.bankz.payment.web;

import com.bankz.common.InvalidRequestException;
import com.bankz.common.LegacyFormat;
import com.bankz.common.Proctran;
import com.bankz.common.web.Ids;
import com.bankz.payment.service.PaymentService;
import com.bankz.payment.service.PaymentService.PaymentResult;
import com.bankz.payment.service.PaymentService.TransferResult;
import com.bankz.payment.service.TransactionInquiryService;
import com.bankz.payment.service.TransactionInquiryService.TransactionPage;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

/** REST replacement for deposit (DBCRFUN), transfer (XFRFUN) and transaction inquiry (INQTRANL, INQTRAND). */
@RestController
@RequestMapping("/api/accounts/{accountId}")
public class PaymentController {

    private final PaymentService payments;
    private final TransactionInquiryService transactions;

    public PaymentController(PaymentService payments, TransactionInquiryService transactions) {
        this.payments = payments;
        this.transactions = transactions;
    }

    public record DepositRequest(BigDecimal amount, String sortCode, String description, Integer facilityType) {
    }

    public record DepositResponse(String accountId, BigDecimal availableBalance, BigDecimal actualBalance,
                                  String transactionReference) {
    }

    public record TransferRequest(String toAccountId, BigDecimal amount, String fromSortCode, String toSortCode) {
    }

    public record TransferResponse(BigDecimal fromAccountAvailableBalance, BigDecimal fromAccountActualBalance,
                                   BigDecimal toAccountAvailableBalance, BigDecimal toAccountActualBalance,
                                   String transactionReference) {
    }

    public record TransactionResponse(String transactionId, String accountId, BigDecimal amount, String currency,
                                      String creditDebitIndicator, String status, String bookingDateTime,
                                      String transactionType, String transactionInformation) {
        static TransactionResponse from(Proctran p) {
            String t = p.getTime();
            return new TransactionResponse(TransactionInquiryService.transactionId(p),
                    LegacyFormat.trim(p.getAccountNumber()), p.getAmount(), "GBP",
                    p.getAmount().signum() < 0 ? "DEBIT" : "CREDIT", "BOOKED",
                    p.getDate() + "T" + t.substring(0, 2) + ":" + t.substring(2, 4) + ":" + t.substring(4, 6) + "Z",
                    p.getType(), LegacyFormat.trim(p.getDescription()));
        }
    }

    public record TransactionList(List<TransactionResponse> transactions, long totalCount, int limit, int offset) {
    }

    @PostMapping("/deposit")
    @ResponseStatus(HttpStatus.CREATED)
    public DepositResponse deposit(@PathVariable String accountId, @RequestBody DepositRequest r) {
        if (r.amount() == null) {
            throw new InvalidRequestException("amount is required");
        }
        PaymentResult result = payments.debitCredit(Ids.account(accountId), r.amount(),
                r.facilityType() == null ? 0 : r.facilityType(), r.description());
        return new DepositResponse(LegacyFormat.trim(result.account().getKey().number()),
                result.account().getAvailableBalance(), result.account().getActualBalance(),
                TransactionInquiryService.transactionId(result.transaction()));
    }

    @PostMapping("/transfer")
    @ResponseStatus(HttpStatus.CREATED)
    public TransferResponse transfer(@PathVariable String accountId, @RequestBody TransferRequest r) {
        TransferResult result = payments.transfer(Ids.account(accountId),
                Ids.account(r.toAccountId()), r.amount());
        return new TransferResponse(result.from().getAvailableBalance(), result.from().getActualBalance(),
                result.to().getAvailableBalance(), result.to().getActualBalance(),
                TransactionInquiryService.transactionId(result.transaction()));
    }

    @GetMapping("/transactions")
    public TransactionList list(@PathVariable String accountId,
                                @RequestParam(required = false) String fromDate,
                                @RequestParam(required = false) String toDate,
                                @RequestParam(defaultValue = "0") int limit,
                                @RequestParam(defaultValue = "0") int offset) {
        TransactionPage page = transactions.list(Ids.account(accountId),
                date(fromDate, TransactionInquiryService.NO_FROM_DATE),
                date(toDate, TransactionInquiryService.NO_TO_DATE), limit, offset);
        return new TransactionList(page.transactions().stream().map(TransactionResponse::from).toList(),
                page.totalCount(), page.limit(), page.offset());
    }

    @GetMapping("/transactions/{transactionId}")
    public TransactionResponse get(@PathVariable String accountId, @PathVariable String transactionId) {
        return TransactionResponse.from(transactions.find(Ids.account(accountId), transactionId));
    }

    /** Accepts YYYY-MM-DD or YYYYMMDD. */
    private static int date(String value, int whenAbsent) {
        if (value == null || value.isBlank()) {
            return whenAbsent;
        }
        String digits = value.replace("-", "");
        if (!digits.matches("\\d{8}")) {
            throw new InvalidRequestException("Dates must be YYYY-MM-DD: " + value);
        }
        return Integer.parseInt(digits);
    }
}
