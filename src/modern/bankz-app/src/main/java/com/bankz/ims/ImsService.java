package com.bankz.ims;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Conversion of the IMS message programs IBGCUDAT, IBSCUDAT, IBLOGIN1, IBLOGOUT, IBACSUM and IBTRAN. Like the IMS
 * transactions, every call returns a message (MSG-OUT) rather than failing.
 */
@Service
public class ImsService {

    static final String LOGGED_IN = "1";
    static final String LOGGED_OUT = "0";
    static final String LOGIN_SUCCESSFUL = "LOGIN SUCCESSFUL";
    static final String LOGOFF_SUCCESSFUL = "LOGOFF SUCCESSFUL";
    static final String ALREADY_LOGGED_IN = "CUSTOMER ALREADY LOGGED IN";
    static final String PASSWORD_INVALID = "PASSWORD INVALID";
    static final String NO_CUSTOMER = "CUSTOMER DOES NOT EXIST";
    static final String NO_ACCOUNT = "ACCOUNT DOES NOT EXIST";
    static final String INVALID_TRX_TYPE = "INVALID ACCOUNT ACTION. MUST BE 'w' OR 'd'.";
    static final String BAD_STATUS_GE = "BAD STATUS CODE WAS RECEIVED: GE";
    static final long MULT_FACTOR = 10_000_000_000L;
    static final int MAX_SUMMARY = 6;
    private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SS");

    private final JdbcTemplate jdbc;
    private final Clock clock;

    public ImsService(JdbcTemplate jdbc, Clock clock) {
        this.jdbc = jdbc;
        this.clock = clock;
    }

    public record ImsCustomer(int customerId, String lastName, String firstName, String address, String city,
                              String state, String zipCode, String phone) {
    }

    public record CustomerReply(String message, ImsCustomer customer) {
    }

    public record AccountSummary(long accountId, String accountType, BigDecimal balance) {
    }

    public record SummaryReply(String message, int totalAccounts, List<AccountSummary> accounts) {
    }

    public record TransactionReply(String message, BigDecimal balance, Long transactionId, SummaryReply summary) {
    }

    /** IBGCUDAT. */
    @Transactional(readOnly = true)
    public CustomerReply getCustomer(int customerId) {
        return find(customerId).map(c -> new CustomerReply("", c))
                .orElse(new CustomerReply(BAD_STATUS_GE, null));
    }

    /** IBSCUDAT: replaces the name, address and phone of the CUSTOMER segment. */
    @Transactional
    public CustomerReply updateCustomer(ImsCustomer c) {
        int updated = jdbc.update("update ims_customer set lastname=?, firstnm=?, address=?, city=?, state=?, "
                        + "zipcode=?, phone=? where custid=?", c.lastName(), c.firstName(), c.address(), c.city(),
                c.state(), c.zipCode(), c.phone(), c.customerId());
        return updated == 0 ? new CustomerReply(BAD_STATUS_GE, null) : getCustomer(c.customerId());
    }

    /** IBLOGIN1. */
    @Transactional
    public String login(int customerId, String password) {
        List<Map<String, Object>> rows = jdbc.queryForList(
                "select password, status from ims_customer where custid = ? for update", customerId);
        if (rows.isEmpty()) {
            return NO_CUSTOMER;
        }
        Map<String, Object> row = rows.get(0);
        if (!String.valueOf(row.get("password")).trim().equals(password == null ? "" : password.trim())) {
            return PASSWORD_INVALID;
        }
        if (LOGGED_IN.equals(String.valueOf(row.get("status")).trim())) {
            return ALREADY_LOGGED_IN;
        }
        jdbc.update("update ims_customer set status = ?, lastlogi = ? where custid = ?", LOGGED_IN,
                LocalDateTime.now(clock).format(TIMESTAMP), customerId);
        return LOGIN_SUCCESSFUL;
    }

    /** IBLOGOUT. */
    @Transactional
    public String logout(int customerId) {
        int updated = jdbc.update("update ims_customer set status = ? where custid = ?", LOGGED_OUT, customerId);
        return updated == 0 ? BAD_STATUS_GE : LOGOFF_SUCCESSFUL;
    }

    /** IBACSUM: up to six accounts reached through CUSTACCS. */
    @Transactional(readOnly = true)
    public SummaryReply accountSummary(int customerId) {
        List<AccountSummary> accounts = jdbc.query("""
                        select a.accid, a.acctype, a.balance from ims_custaccs ca
                        join ims_account a on a.accid = ca.accid
                        where ca.custid = ? order by ca.accid""",
                (rs, i) -> new AccountSummary(rs.getLong(1), rs.getString(2), rs.getBigDecimal(3)), customerId);
        List<AccountSummary> limited = accounts.stream().limit(MAX_SUMMARY).toList();
        return new SummaryReply(limited.isEmpty() ? BAD_STATUS_GE : "", limited.size(), limited);
    }

    /** IBTRAN: 'd' deposit or 'w' withdrawal; writes a HISTORY segment and returns the account summary. */
    @Transactional
    public TransactionReply transaction(long accountId, BigDecimal amount, String type, int customerId) {
        if (type == null || !List.of("d", "w").contains(type.toLowerCase())) {
            return new TransactionReply(INVALID_TRX_TYPE, null, null, null);
        }
        List<Map<String, Object>> rows = jdbc.queryForList(
                "select balance, lasttxid from ims_account where accid = ? for update", accountId);
        if (rows.isEmpty()) {
            return new TransactionReply(NO_ACCOUNT, null, null, null);
        }
        BigDecimal balance = (BigDecimal) rows.get(0).get("balance");
        long lastTxId = ((Number) rows.get(0).get("lasttxid")).longValue() + 1;
        String transType = type.toLowerCase();
        balance = transType.equals("w") ? balance.subtract(amount) : balance.add(amount);
        long txId = accountId * MULT_FACTOR + lastTxId;
        jdbc.update("insert into ims_history values (?,?,?,?,?,?)", txId,
                LocalDateTime.now(clock).format(TIMESTAMP), transType, amount, 0L, accountId);
        jdbc.update("update ims_account set balance = ?, lasttxid = ? where accid = ?", balance, lastTxId, accountId);
        return new TransactionReply(String.format("%16.2f", balance), balance, txId, accountSummary(customerId));
    }

    private Optional<ImsCustomer> find(int customerId) {
        return jdbc.query("select custid, lastname, firstnm, address, city, state, zipcode, phone "
                        + "from ims_customer where custid = ?",
                (rs, i) -> new ImsCustomer(rs.getInt(1), trim(rs.getString(2)), trim(rs.getString(3)),
                        trim(rs.getString(4)), trim(rs.getString(5)), trim(rs.getString(6)), trim(rs.getString(7)),
                        trim(rs.getString(8))), customerId).stream().findFirst();
    }

    private static String trim(String s) {
        return s == null ? "" : s.trim();
    }
}
