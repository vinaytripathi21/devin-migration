package com.bankz.payment.service;

import com.bankz.common.BusinessFailureException;
import com.bankz.common.LegacyFormat;
import com.bankz.common.Proctran;
import com.bankz.common.ProctranRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/** Conversion of INQTRANL (transaction list) and INQTRAND (single transaction). */
@Service
@Transactional(readOnly = true)
public class TransactionInquiryService {

    public static final int NO_FROM_DATE = 0;
    public static final int NO_TO_DATE = 99_999_999;
    static final int DEFAULT_LIMIT = 50;
    static final int MAX_LIMIT = 100;

    private final ProctranRepository repository;
    private final long sortCode;

    public TransactionInquiryService(ProctranRepository repository,
                                     @Value("${bankz.sort-code:987654}") long sortCode) {
        this.repository = repository;
        this.sortCode = sortCode;
    }

    public record TransactionPage(long totalCount, int limit, int offset, List<Proctran> transactions) {
    }

    /** INQTRANL: dates are yyyymmdd (0 / 99999999 = open), limit 0 means 50 and is capped at 100. */
    public TransactionPage list(long accountNumber, int fromDate, int toDate, int limit, int offset) {
        int effectiveLimit = limit <= 0 ? DEFAULT_LIMIT : Math.min(limit, MAX_LIMIT);
        int effectiveOffset = Math.max(offset, 0);
        LocalDate from = fromDate == NO_FROM_DATE ? LocalDate.of(1, 1, 1) : LegacyFormat.fromYyyymmdd(fromDate);
        LocalDate to = toDate == NO_TO_DATE ? LocalDate.of(9999, 12, 31) : LegacyFormat.fromYyyymmdd(toDate);
        String sort = LegacyFormat.zeroPad(sortCode, 6);
        String account = LegacyFormat.zeroPad(accountNumber, 8);
        long total = repository.countForAccount(sort, account, from, to);
        List<Proctran> rows = repository.findForAccount(sort, account, from, to).stream()
                .skip(effectiveOffset).limit(effectiveLimit).toList();
        return new TransactionPage(total, effectiveLimit, effectiveOffset, rows);
    }

    /** INQTRAND: the transaction id is date(8) + time(6) + reference(12), the cursor key of TRAND-CURSOR. */
    public Proctran find(long accountNumber, String transactionId) {
        if (transactionId == null || !transactionId.matches("\\d{26}")) {
            throw new BusinessFailureException("1", "Transaction id must be 26 digits", HttpStatus.BAD_REQUEST);
        }
        LocalDate date = LegacyFormat.fromYyyymmdd(Integer.parseInt(transactionId.substring(0, 8)));
        return repository.findFirstBySortCodeAndAccountNumberAndDateAndTimeAndReference(
                        LegacyFormat.zeroPad(sortCode, 6), LegacyFormat.zeroPad(accountNumber, 8), date,
                        transactionId.substring(8, 14), transactionId.substring(14))
                .orElseThrow(() -> new BusinessFailureException("1", "Transaction " + transactionId + " not found",
                        HttpStatus.NOT_FOUND));
    }

    public static String transactionId(Proctran p) {
        return LegacyFormat.yyyymmdd(p.getDate()) + p.getTime() + LegacyFormat.trim(p.getReference());
    }
}
