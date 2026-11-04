package com.bankz.batch;

import com.bankz.customer.service.CustomerInquiryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** BNKSTMTC, BNKSTMTP and BANKDATA against the demo data. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"test", "demo"})
@Transactional
class BatchIntegrationTest {

    @Autowired
    private StatementService statements;
    @Autowired
    private BankDataGenerator bankData;
    @Autowired
    private CustomerInquiryService customers;
    @Autowired
    private MockMvc mvc;

    @Test
    void bnkstmtcPrintsCustomerStatement() {
        StatementResult r = statements.customerStatement("C000000001 2026-10");
        assertThat(r.returnCode()).isZero();
        assertThat(r.report()).contains("CUSTOMER ID     : C000000001", "*** END OF STATEMENT ***",
                "STATEMENT PERIOD: 2026-10-01 TO 2026-10-31");
        assertThat(r.report()).anyMatch(l -> l.startsWith("BANK OF Z"));
        assertThat(r.report()).anyMatch(l -> l.startsWith("ACCOUNT: 987654-00000001"));
        assertThat(r.report()).anyMatch(l -> l.startsWith("NAME            : Mr John Smith"));
    }

    @Test
    void bnkstmtcValidatesSysin() {
        StatementResult bad = statements.customerStatement("X12 2026-13");
        assertThat(bad.returnCode()).isEqualTo(8);
        assertThat(bad.messages()).anyMatch(m -> m.startsWith("BNKZE0002"))
                .anyMatch(m -> m.startsWith("BNKZE0006"));
        assertThat(statements.customerStatement("C1 2999-01").messages()).anyMatch(m -> m.startsWith("BNKZE0008"));
        assertThat(statements.customerStatement("C1A").messages()).anyMatch(m -> m.startsWith("BNKZE0003"));
        assertThat(statements.customerStatement("").returnCode()).isEqualTo(4);
        assertThat(statements.customerStatement("C7 2026-10").returnCode()).isEqualTo(4);
        assertThat(statements.customerStatement("C1").messages()).anyMatch(m -> m.startsWith("BNKZI0001"));
    }

    @Test
    void bnkstmtpPrintsOneStatementPerAccount() {
        StatementResult r = statements.sortCodeStatements("987654", "202610");
        assertThat(r.statements()).isEqualTo(4);
        assertThat(r.messages()).contains("TOTAL STATEMENTS GENERATED: 4", "STATEMENT PERIOD: 20261001 TO 20261031");
        assertThat(statements.sortCodeStatements(null, "202602").messages())
                .contains("SORTCODE FILE NOT FOUND - USING DEFAULT", "TOTAL STATEMENTS GENERATED: 0");
    }

    @Test
    void bankdataRegeneratesCustomersAndCounters() throws Exception {
        BankDataGenerator.Generated g = bankData.generate(1, 20, 2, 7);
        assertThat(g.customers()).isEqualTo(10);
        assertThat(customers.inquire(CustomerInquiryService.LAST_CUSTOMER).getKey().number()).isEqualTo("0000000019");
        assertThat(bankData.generate(1, 3, 1, 7)).isEqualTo(bankData.generate(1, 3, 1, 7));
    }

    @Test
    void batchEndpoints() throws Exception {
        mvc.perform(get("/api/batch/statements?customerId=C000000002&period=2026-10"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.returnCode").value(0));
        mvc.perform(post("/api/batch/bankdata?start=5&end=1"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.failCode").value("P"));
    }
}
