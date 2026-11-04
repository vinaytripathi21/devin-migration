package com.bankz.ims;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Conversion of LOADCUST, LOADACCT, LOADCUSA, LOADHIST and LOADTSTA: loads the IMS LoadData files (copied to
 * resources/ims-data) into the ims_* tables when they are empty.
 */
@Component
@ConditionalOnProperty(name = "bankz.ims.load-data", havingValue = "true", matchIfMissing = true)
public class ImsDataLoader implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(ImsDataLoader.class);

    private final JdbcTemplate jdbc;

    public ImsDataLoader(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws IOException {
        Integer existing = jdbc.queryForObject("select count(*) from ims_customer", Integer.class);
        if (existing != null && existing > 0) {
            return;
        }
        int customers = load("CUSTOMER", "insert into ims_customer values (?,?,?,?,?,?,?,?,?,?,?,?)", r -> new Object[]{
                Integer.parseInt(r.get(0)), r.get(1), r.get(2), r.get(3), r.get(4), r.get(5), r.get(6), r.get(7),
                r.get(8), r.get(9), r.get(10), r.get(11)});
        load("ACCOUNT", "insert into ims_account values (?,?,?,?)", r -> new Object[]{
                Long.parseLong(r.get(0)), r.get(1), new java.math.BigDecimal(r.get(2)), Long.parseLong(r.get(3))});
        load("CUSTACCS", "insert into ims_custaccs values (?,?,?)", r -> new Object[]{
                Long.parseLong(r.get(0)), Integer.parseInt(r.get(1)), Integer.parseInt(r.get(2))});
        load("HISTORY", "insert into ims_history values (?,?,?,?,?,?)", r -> new Object[]{
                Long.parseLong(r.get(0)), r.get(1), r.get(2), new java.math.BigDecimal(r.get(3)),
                Long.parseLong(r.get(4)), Long.parseLong(r.get(5))});
        load("TSTAT", "insert into ims_tstat values (?,?,?,?,?)", r -> new Object[]{
                Long.parseLong(r.get(0)), r.get(1), r.get(2), r.get(3), Integer.parseInt(r.get(4))});
        for (String dbd : List.of("ACCTYPE", "CUSTTYPE", "TSTATTYP", "TTYPE")) {
            load(dbd, "insert into ims_code values (?,?,?)", r -> new Object[]{dbd, r.get(0), r.get(1).trim()});
        }
        log.info("Loaded IMS LoadData: {} customers", customers);
    }

    private int load(String dbd, String sql, java.util.function.Function<List<String>, Object[]> mapper)
            throws IOException {
        List<Object[]> rows = new ArrayList<>();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(
                new ClassPathResource("ims-data/" + dbd + ".data").getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = in.readLine()) != null) {
                if (!line.isBlank()) {
                    rows.add(mapper.apply(parse(line)));
                }
            }
        }
        jdbc.batchUpdate(sql, rows);
        return rows.size();
    }

    /** Comma-separated values with optional double quotes. */
    static List<String> parse(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean quoted = false;
        for (char c : line.toCharArray()) {
            if (c == '"') {
                quoted = !quoted;
            } else if (c == ',' && !quoted) {
                fields.add(field.toString());
                field.setLength(0);
            } else {
                field.append(c);
            }
        }
        fields.add(field.toString().stripTrailing());
        return fields;
    }
}
