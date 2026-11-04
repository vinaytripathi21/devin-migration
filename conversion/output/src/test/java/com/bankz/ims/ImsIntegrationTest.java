package com.bankz.ims;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** IMS programs against the original IMS LoadData. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"test", "demo"})
@Transactional
class ImsIntegrationTest {

    @Autowired
    private ImsService ims;
    @Autowired
    private JdbcTemplate jdbc;
    @Autowired
    private MockMvc mvc;

    @Test
    void loadProgramsLoadedAllLoadData() throws Exception {
        for (String[] t : new String[][]{{"CUSTOMER", "ims_customer"}, {"ACCOUNT", "ims_account"},
                {"CUSTACCS", "ims_custaccs"}, {"HISTORY", "ims_history"}, {"TSTAT", "ims_tstat"}}) {
            long records;
            try (var in = new java.io.BufferedReader(new java.io.InputStreamReader(
                    new org.springframework.core.io.ClassPathResource("ims-data/" + t[0] + ".data").getInputStream()))) {
                records = in.lines().filter(l -> !l.isBlank()).count();
            }
            assertThat(jdbc.queryForObject("select count(*) from " + t[1], Long.class)).as(t[0]).isEqualTo(records);
        }
        assertThat(jdbc.queryForObject("select count(*) from ims_customer", Integer.class)).isEqualTo(100);
        assertThat(jdbc.queryForObject("select count(*) from ims_code", Integer.class)).isEqualTo(9);
    }

    @Test
    void ibgcudatAndIbscudat() throws Exception {
        mvc.perform(get("/api/ims/customers/I1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customer.lastName").value("Antonelli"))
                .andExpect(jsonPath("$.customer.state").value("TX"));
        assertThat(ims.getCustomer(5000).message()).isEqualTo(ImsService.BAD_STATUS_GE);
        ImsService.CustomerReply r = ims.updateCustomer(new ImsService.ImsCustomer(1, "Antonelli", "Marta",
                "1 Main St", "Austin", "TX", "73301", "555-0100"));
        assertThat(r.customer().firstName()).isEqualTo("Marta");
    }

    @Test
    void ibloginAndIblogout() {
        assertThat(ims.login(1, "wrong")).isEqualTo(ImsService.PASSWORD_INVALID);
        assertThat(ims.login(5000, "password")).isEqualTo(ImsService.NO_CUSTOMER);
        assertThat(ims.login(1, "password")).isEqualTo(ImsService.LOGIN_SUCCESSFUL);
        assertThat(ims.login(1, "password")).isEqualTo(ImsService.ALREADY_LOGGED_IN);
        assertThat(ims.logout(1)).isEqualTo(ImsService.LOGOFF_SUCCESSFUL);
        assertThat(ims.login(1, "password")).isEqualTo(ImsService.LOGIN_SUCCESSFUL);
    }

    @Test
    void ibacsumListsUpToSixAccounts() {
        ImsService.SummaryReply r = ims.accountSummary(1);
        assertThat(r.accounts()).extracting(ImsService.AccountSummary::accountId).contains(101L, 102L);
        assertThat(r.totalAccounts()).isLessThanOrEqualTo(6);
        assertThat(ims.accountSummary(5000).message()).isEqualTo(ImsService.BAD_STATUS_GE);
    }

    @Test
    void ibtranDepositsWithdrawsAndWritesHistory() throws Exception {
        ImsService.TransactionReply d = ims.transaction(101, new BigDecimal("100.00"), "d", 1);
        assertThat(d.balance()).isEqualByComparingTo("8930.00");
        assertThat(d.transactionId()).isEqualTo(1010000000002L);
        assertThat(d.message().trim()).isEqualTo("8930.00");
        assertThat(ims.transaction(101, new BigDecimal("30.00"), "W", 1).balance()).isEqualByComparingTo("8900.00");
        assertThat(jdbc.queryForObject("select count(*) from ims_history where accid = 101", Integer.class))
                .isEqualTo(3);
        assertThat(ims.transaction(101, BigDecimal.ONE, "x", 1).message()).isEqualTo(ImsService.INVALID_TRX_TYPE);
        assertThat(ims.transaction(999999, BigDecimal.ONE, "d", 1).message()).isEqualTo(ImsService.NO_ACCOUNT);
        mvc.perform(post("/api/ims/accounts/101/transactions").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customerId\":\"I1\",\"amount\":10,\"type\":\"d\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.balance").value(8910.00));
    }

    @Test
    void parsesQuotedCsv() {
        assertThat(ImsDataLoader.parse("1,\"Smith, Jr\",\"x\"")).containsExactly("1", "Smith, Jr", "x");
    }
}
