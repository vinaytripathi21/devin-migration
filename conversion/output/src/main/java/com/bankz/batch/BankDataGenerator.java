package com.bankz.batch;

import com.bankz.account.domain.Account;
import com.bankz.account.domain.AccountKey;
import com.bankz.account.domain.AccountRepository;
import com.bankz.common.BusinessFailureException;
import com.bankz.common.ControlRecord;
import com.bankz.common.ControlRepository;
import com.bankz.common.LegacyFormat;
import com.bankz.customer.domain.Customer;
import com.bankz.customer.domain.CustomerKey;
import com.bankz.customer.domain.CustomerRepository;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

/**
 * Conversion of BANKDATA: deletes the bank's rows, then generates random customers from START-KEY to END-KEY by
 * STEP-KEY (batch PARM "start,end,step,seed"), each with 1 to 5 accounts, and resets the CONTROL counters.
 */
@Service
public class BankDataGenerator {

    private static final List<String> TITLES = List.of("Mr", "Mrs", "Miss", "Ms", "Mr", "Mrs", "Miss", "Ms", "Mr",
            "Mrs", "Miss", "Ms", "Mr", "Mrs", "Miss", "Ms", "Mr", "Mrs", "Miss", "Ms", "Dr", "Drs", "Dr", "Ms", "Dr",
            "Ms", "Dr", "Ms", "Professor", "Professor", "Professor", "Lord", "Sir", "Sir", "Lady", "Lady");
    private static final List<String> FORENAMES = List.of("Michael", "Will", "Geoff", "Chris", "Dave", "Luke",
            "Adam", "Giuseppe", "James", "Jon", "Andy", "Lou", "Robert", "Sam", "Frederick", "Buford", "William",
            "Howard", "Anthony", "Bruce", "Peter", "Stephen", "Donald", "Dennis", "Harold", "Amy", "Belinda", "Charlotte",
            "Donna", "Felicia", "Gretchen", "Henrietta", "Imogen", "Josephine", "Kimberley", "Lucy", "Monica",
            "Natalie", "Ophelia", "Patricia", "Querida", "Rachel", "Samantha", "Tanya", "Ulrika", "Virginia",
            "Wendy", "Xaviera", "Yvonne", "Zsa Zsa");
    private static final List<String> SURNAMES = List.of("Jones", "Davidson", "Baker", "Smith", "Taylor", "Evans",
            "Roberts", "Wright", "Walker", "Green", "Price", "Downton", "Gatting", "Tomlinson", "Mantle", "Cobb",
            "Ruth", "Gehrig", "Gibson", "Robinson", "Ripken", "Musial", "Mays", "Aaron", "Williams", "Mays",
            "Bonds", "Rose", "Cooper", "Dixon", "Hendrix", "Joplin", "Lennon", "Morrison", "Jagger", "Bowie");
    private static final List<String> STREETS = List.of("Acacia Road", "Birch Avenue", "Chestnut Close",
            "Downs Road", "Elm Grove", "Fir Tree Lane", "Grange Road", "High Street", "Larch Way", "Oak Drive");
    private static final List<String> TOWNS = List.of("Winchester", "Southampton", "Basingstoke", "Bordon",
            "Alton", "Petersfield", "Portsmouth", "Eastleigh", "Romsey", "Andover", "Fareham", "Gosport",
            "Havant", "Totton", "Chandlers Ford", "Hedge End");
    private static final List<String> ACCOUNT_TYPES = List.of("ISA", "SAVING", "CURRENT", "LOAN", "MORTGAGE");

    private final CustomerRepository customers;
    private final AccountRepository accounts;
    private final ControlRepository controls;
    private final JdbcTemplate jdbc;
    private final EntityManager entityManager;
    private final Clock clock;
    private final long sortCode;

    public BankDataGenerator(CustomerRepository customers, AccountRepository accounts, ControlRepository controls,
                             JdbcTemplate jdbc, EntityManager entityManager, Clock clock, @Value("${bankz.sort-code:987654}") long sortCode) {
        this.customers = customers;
        this.accounts = accounts;
        this.controls = controls;
        this.jdbc = jdbc;
        this.entityManager = entityManager;
        this.clock = clock;
        this.sortCode = sortCode;
    }

    public record Generated(int customers, int accounts) {
    }

    @Transactional
    public Generated generate(long startKey, long endKey, long stepKey, long seed) {
        if (endKey < startKey) {
            throw new BusinessFailureException("P", "Final customer number cannot be smaller than first",
                    HttpStatus.BAD_REQUEST);
        }
        if (stepKey == 0) {
            throw new BusinessFailureException("P", "Gap between customers cannot be zero", HttpStatus.BAD_REQUEST);
        }
        String sort = LegacyFormat.zeroPad(sortCode, 6);
        entityManager.flush();
        entityManager.clear();
        jdbc.update("delete from account where account_sortcode = ?", sort);
        jdbc.update("delete from customer where customer_sortcode = ?", sort);
        jdbc.update("delete from proctran where proctran_sortcode = ?", sort);

        Random random = new Random(seed);
        LocalDate today = LocalDate.now(clock);
        int customerCount = 0;
        int accountNumber = 0;
        long lastCustomer = 0;
        for (long key = startKey; key <= endKey; key += stepKey) {
            LocalDate dob = LocalDate.of(1920 + random.nextInt(85), 1 + random.nextInt(12), 1 + random.nextInt(28));
            customers.save(new Customer(CustomerKey.of(sortCode, key), pick(random, TITLES),
                    pick(random, FORENAMES) + " " + (char) ('A' + random.nextInt(26)), pick(random, SURNAMES),
                    LegacyFormat.yyyymmdd(dob), "0" + (1000000000L + random.nextInt(900000000)),
                    (1 + random.nextInt(99)) + " " + pick(random, STREETS), "", pick(random, TOWNS),
                    "SO" + (10 + random.nextInt(89)) + " " + random.nextInt(10) + "AB", "United Kingdom", "ACTIVE",
                    LegacyFormat.yyyymmdd(today), (short) (1 + random.nextInt(999)),
                    LegacyFormat.yyyymmdd(today.plusDays(1 + random.nextInt(20)))));
            customerCount++;
            lastCustomer = key;
            int accountsForCustomer = 1 + random.nextInt(5);
            for (int i = 0; i < accountsForCustomer; i++) {
                accountNumber++;
                String type = pick(random, ACCOUNT_TYPES);
                boolean lending = type.equals("LOAN") || type.equals("MORTGAGE");
                BigDecimal balance = BigDecimal.valueOf(random.nextInt(1_000_000), 2);
                if (lending) {
                    balance = balance.negate();
                }
                LocalDate opened = today.minusDays(random.nextInt(3650));
                accounts.save(new Account(AccountKey.of(sortCode, accountNumber), LegacyFormat.zeroPad(key, 10),
                        type, BigDecimal.valueOf(random.nextInt(600), 2), opened, lending ? 0 : random.nextInt(500),
                        today.withDayOfMonth(1), today.withDayOfMonth(1).plusMonths(1), balance, balance));
            }
        }
        setControl("BANKZCUST" + sort, (int) lastCustomer);
        setControl(sort + "-ACCOUNT-LAST", accountNumber);
        setControl(sort + "-ACCOUNT-COUNT", accountNumber);
        return new Generated(customerCount, accountNumber);
    }

    private void setControl(String name, int value) {
        ControlRecord record = controls.lockByName(name).orElseGet(() -> controls.save(new ControlRecord(name, 0)));
        record.setValueNum(value);
    }

    private static String pick(Random random, List<String> values) {
        return values.get(random.nextInt(values.size()));
    }
}
