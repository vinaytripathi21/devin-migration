package com.bankz.batch;

import com.bankz.account.domain.Account;
import com.bankz.account.domain.AccountRepository;
import com.bankz.common.LegacyFormat;
import com.bankz.common.Proctran;
import com.bankz.common.ProctranRepository;
import com.bankz.customer.domain.Customer;
import com.bankz.customer.domain.CustomerKey;
import com.bankz.customer.domain.CustomerRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

/**
 * Conversion of the monthly statement batch: BNKSTMTC (COBOL, one customer from SYSIN) and BNKSTMTP (PL/I, every
 * account of a sort code from the SORTCODE and DATECARD files).
 */
@Service
@Transactional(readOnly = true)
public class StatementService {

    static final int LINES_PER_PAGE = 55;
    private static final String BANNER = "=".repeat(132);
    private static final DateTimeFormatter DAY = DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.ENGLISH);
    private static final Set<String> DEPOSITS = Set.of("CRE", "PCR", "CHI");
    private static final Set<String> WITHDRAWALS = Set.of("DEB", "PDR", "CHO");

    private final AccountRepository accounts;
    private final CustomerRepository customers;
    private final ProctranRepository proctran;
    private final Clock clock;
    private final long sortCode;

    public StatementService(AccountRepository accounts, CustomerRepository customers, ProctranRepository proctran,
                            Clock clock, @Value("${bankz.sort-code:987654}") long sortCode) {
        this.accounts = accounts;
        this.customers = customers;
        this.proctran = proctran;
        this.clock = clock;
        this.sortCode = sortCode;
    }

    /** BNKSTMTC: SYSIN card "C<1-9 digits> [YYYY-MM]". Validation errors give return code 8. */
    public StatementResult customerStatement(String sysin) {
        List<String> messages = new ArrayList<>();
        if (sysin == null || sysin.isBlank()) {
            messages.add("BNKZI0005: SYSIN not specified or empty. USAGE: <CustomerID> <YYYY-MM>, e.g. C000000001 2026-07");
            return new StatementResult(4, messages, List.of(), 0);
        }
        String[] parts = sysin.trim().split("\\s+");
        String id = parts[0];
        int errors = 0;
        char prefix = Character.toUpperCase(id.charAt(0));
        String digits = id.substring(1);
        if (prefix != 'C' && prefix != 'I') {
            messages.add("BNKZE0002: Customer ID prefix must be C or I: " + id);
            errors++;
        }
        if (digits.isEmpty() || !digits.matches("\\d+")) {
            messages.add("BNKZE0003: Non-numeric customer ID digits: " + id);
            errors++;
        } else if (digits.length() > 9 && Long.parseLong(digits) > 999_999_999L) {
            messages.add("BNKZE0004: Customer ID must not exceed 9 significant digits: " + id);
            errors++;
        }
        LocalDate today = LocalDate.now(clock);
        YearMonth period = YearMonth.from(today);
        if (parts.length > 1) {
            String p = parts[1];
            if (!p.matches("\\d{4}-\\d{2}")) {
                messages.add("BNKZE0005: Invalid statement period, expected YYYY-MM: " + p);
                errors++;
            } else {
                int yyyy = Integer.parseInt(p.substring(0, 4));
                int mm = Integer.parseInt(p.substring(5));
                if (mm < 1 || mm > 12) {
                    messages.add("BNKZE0006: Invalid month value MM in " + p);
                    errors++;
                } else if (yyyy < 1900) {
                    messages.add("BNKZE0007: Statement year YYYY is before 1900: " + p);
                    errors++;
                } else if (YearMonth.of(yyyy, mm).isAfter(period)) {
                    messages.add("BNKZE0008: Statement period is in the future: " + p);
                    errors++;
                } else {
                    period = YearMonth.of(yyyy, mm);
                }
            }
        } else {
            messages.add("BNKZI0001: Statement period omitted; using " + period);
        }
        if (errors > 0) {
            return new StatementResult(8, messages, List.of(), 0);
        }
        String customerNumber = LegacyFormat.zeroPad(Long.parseLong(digits), 10);
        List<Account> list = accounts.findTop20ByKeySortCodeAndCustomerNumberOrderByKeyNumberAsc(
                LegacyFormat.zeroPad(sortCode, 6), customerNumber);
        if (list.isEmpty()) {
            messages.add("BNKZI0002: No accounts found for customer " + id);
            return new StatementResult(4, messages, List.of(), 0);
        }
        Report report = new Report(messages);
        report.header(period.atDay(1), period.atEndOfMonth(), prefix + customerNumber.substring(1),
                customer(customerNumber));
        for (Account account : list) {
            report.account(account, period.atDay(1), period.atEndOfMonth());
        }
        report.end();
        return new StatementResult(0, messages, report.lines, 1);
    }

    /**
     * BNKSTMTP: one statement per account of the sort code. DATECARD "YYYYMM" selects the whole month; without it
     * the period is the first of the current month to today. (The PL/I always used 28 for February; this uses the
     * real month length.)
     */
    public StatementResult sortCodeStatements(String sortCodeCard, String dateCard) {
        List<String> messages = new ArrayList<>();
        String sort = sortCodeCard == null || sortCodeCard.isBlank() ? "123456" : sortCodeCard.trim().substring(0, 6);
        if (sortCodeCard == null || sortCodeCard.isBlank()) {
            messages.add("SORTCODE FILE NOT FOUND - USING DEFAULT");
        }
        messages.add("SORT CODE FROM PARAMETER: " + sort);
        LocalDate from;
        LocalDate to;
        if (dateCard == null || dateCard.isBlank()) {
            to = LocalDate.now(clock);
            from = to.withDayOfMonth(1);
            messages.add("DATECARD NOT FOUND - USING CURRENT DATE");
        } else {
            YearMonth month = YearMonth.of(Integer.parseInt(dateCard.substring(0, 4)),
                    Integer.parseInt(dateCard.substring(4, 6)));
            from = month.atDay(1);
            to = month.atEndOfMonth();
            messages.add("REPORTING MONTH FROM DATECARD: " + dateCard.substring(0, 6));
        }
        messages.add("STATEMENT PERIOD: " + LegacyFormat.yyyymmdd(from) + " TO " + LegacyFormat.yyyymmdd(to));
        Report report = new Report(messages);
        int count = 0;
        for (Account account : accounts.findByKeySortCodeOrderByKeyNumberAsc(sort)) {
            report.header(from, to, "C" + LegacyFormat.trim(account.getCustomerNumber()).substring(1),
                    customer(account.getCustomerNumber()));
            report.account(account, from, to);
            report.end();
            count++;
        }
        messages.add("BNKSTMT COMPLETED SUCCESSFULLY");
        messages.add("TOTAL STATEMENTS GENERATED: " + count);
        return new StatementResult(0, messages, report.lines, count);
    }

    private Optional<Customer> customer(String customerNumber) {
        return customers.findById(new CustomerKey(LegacyFormat.zeroPad(sortCode, 6), customerNumber));
    }

    /** SYSPRINT writer: 132 columns, page eject every 55 lines. */
    private final class Report {
        private final List<String> lines = new ArrayList<>();
        private final List<String> messages;
        private int page = 1;
        private int lineCount;

        Report(List<String> messages) {
            this.messages = messages;
        }

        void write(String line) {
            if (lineCount >= LINES_PER_PAGE) {
                page++;
                lines.add("\f" + String.format("%120s", "PAGE " + page));
                lineCount = 0;
            }
            lines.add(line);
            lineCount++;
        }

        void header(LocalDate from, LocalDate to, String customerId, Optional<Customer> customer) {
            write(BANNER);
            write(String.format("%-120s%s", "BANK OF Z", "PAGE " + page));
            write(BANNER);
            write("STATEMENT PERIOD: " + from + " TO " + to);
            write("CUSTOMER ID     : " + customerId);
            write("NAME            : " + customer.map(c -> LegacyFormat.trim(c.getTitle()) + " "
                    + LegacyFormat.fullName(c.getFirstName(), c.getLastName())).orElse("N/A"));
            write("ADDRESS         : " + customer.map(c -> LegacyFormat.trim(c.getAddressLine1()) + ", "
                    + LegacyFormat.trim(c.getCity()) + " " + LegacyFormat.trim(c.getPostcode()) + ", "
                    + LegacyFormat.trim(c.getCountry())).orElse("N/A"));
            write("PHONE           : " + customer.map(c -> LegacyFormat.trim(c.getPhone())).orElse("N/A"));
        }

        void account(Account account, LocalDate from, LocalDate to) {
            write("");
            write("ACCOUNT: " + account.getKey().sortCode() + "-" + account.getKey().number() + "  TYPE: "
                    + LegacyFormat.trim(account.getType()));
            write(String.format("%-12s  %-40s  %14s  %14s  %17s", "DATE", "DESCRIPTION", "WITHDRAWAL", "DEPOSIT",
                    "RUNNING BALANCE"));
            write(String.format("%-12s  %-40s  %14s  %14s  %17s", "-".repeat(12), "-".repeat(40), "-".repeat(14),
                    "-".repeat(14), "-".repeat(17)));
            List<Proctran> rows = new ArrayList<>(proctran.findForAccount(account.getKey().sortCode(),
                    account.getKey().number(), from, to));
            java.util.Collections.reverse(rows);
            BigDecimal running = BigDecimal.ZERO;
            BigDecimal deposits = BigDecimal.ZERO;
            BigDecimal withdrawals = BigDecimal.ZERO;
            for (Proctran p : rows) {
                char cls = classify(p, account);
                BigDecimal abs = p.getAmount().abs();
                String dep = "";
                String wdr = "";
                if (cls == 'D') {
                    deposits = deposits.add(abs);
                    running = running.add(abs);
                    dep = money(abs);
                } else if (cls == 'W') {
                    withdrawals = withdrawals.add(abs);
                    running = running.subtract(abs);
                    wdr = money(abs);
                }
                write(String.format("%-12s  %-40s  %14s  %14s  %17s", p.getDate().format(DAY),
                        LegacyFormat.trim(p.getDescription()), wdr, dep, money(running)));
            }
            if (rows.isEmpty()) {
                write("NO TRANSACTIONS IN STATEMENT PERIOD");
            }
            write("");
            write(String.format("TOTAL WITHDRAWALS: %s   TOTAL DEPOSITS: %s   CLOSING BALANCE: %s",
                    money(withdrawals), money(deposits), money(account.getActualBalance())));
        }

        /** BNKSTMTC 4310-CLASSIFY-TRAN. */
        private char classify(Proctran p, Account account) {
            String type = p.getType();
            int sign = p.getAmount().signum();
            if (DEPOSITS.contains(type)) {
                return 'D';
            }
            if (WITHDRAWALS.contains(type)) {
                return 'W';
            }
            if (type.equals("TFR")) {
                return sign > 0 ? 'D' : sign < 0 ? 'W' : 'I';
            }
            if (sign != 0) {
                messages.add("BNKZI0007: Informational transaction type " + type + " has non-zero amount "
                        + p.getAmount().abs() + " for account " + account.getKey().number()
                        + "; treated as withdrawal");
                return 'W';
            }
            return 'I';
        }

        void end() {
            write("");
            write("*** END OF STATEMENT ***");
        }

        private static String money(BigDecimal amount) {
            return String.format("$%,.2f", amount);
        }
    }
}
