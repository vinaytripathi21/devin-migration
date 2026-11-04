package com.bankz;

import com.bankz.common.ProctranRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** End-to-end checks of the converted CICS programs against the demo data; each test rolls back. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"test", "demo"})
@Transactional
class BankFlowIntegrationTest {

    private static final String NEW_CUSTOMER = """
            {"title":"Mrs","firstName":"Ada","lastName":"Lovelace","dateOfBirth":"1990-12-10",
             "phoneNumber":"0123","address":{"addressLine1":"1 Lane","city":"Leeds","postalCode":"LS1","country":"UK"},
             "customerStatus":"ACTIVE"}""";

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper json;
    @Autowired
    private ProctranRepository proctran;

    private ResultActions send(org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder b,
                               String body) throws Exception {
        return mvc.perform(b.contentType(MediaType.APPLICATION_JSON).content(body));
    }

    private JsonNode body(ResultActions r) throws Exception {
        return json.readTree(r.andReturn().getResponse().getContentAsString());
    }

    @Test
    void crecustAllocatesNextNumberAndWritesProctran() throws Exception {
        send(post("/api/customers"), NEW_CUSTOMER)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerId").value("0000000043"))
                .andExpect(jsonPath("$.sortCode").value("987654"));
        mvc.perform(get("/api/customers/43"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Ada"))
                .andExpect(jsonPath("$.dateOfBirth").value("1990-12-10"));
        assertThat(proctran.findBySortCodeAndAccountNumberOrderByIdAsc("987654", "00000000"))
                .anySatisfy(p -> {
                    assertThat(p.getType()).isEqualTo("OCC");
                    assertThat(p.getDescription()).startsWith("9876540000000043Ada Lovelace  10/12/1990");
                });
    }

    @Test
    void crecustValidation() throws Exception {
        send(post("/api/customers"), NEW_CUSTOMER.replace("Mrs", "King"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.failCode").value("T"))
                .andExpect(jsonPath("$.message").value("Invalid title: King"));
        send(post("/api/customers"), NEW_CUSTOMER.replace("1990-12-10", "2990-01-01"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.failCode").value("Y"));
        send(post("/api/customers"), NEW_CUSTOMER.replace("1990-12-10", "1990-02-30"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.failCode").value("Z"));
        send(post("/api/customers"), NEW_CUSTOMER.replace("1990-12-10", "1500-01-01"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.failCode").value("O"));
    }

    @Test
    void updcustChangesOnlyNonBlankGroups() throws Exception {
        send(put("/api/customers/1"), "{\"title\":\"\",\"firstName\":\"\",\"lastName\":\"\",\"phoneNumber\":\"999\","
                + "\"address\":{\"addressLine1\":\"2 New Road\",\"city\":\"Bath\",\"postalCode\":\"BA1\",\"country\":\"UK\"}}")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.phoneNumber").value("999"))
                .andExpect(jsonPath("$.address.city").value("Bath"));
        send(put("/api/customers/7"), "{\"firstName\":\"X\"}")
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.failCode").value("1"));
        send(put("/api/customers/1"), "{\"firstName\":\"\",\"lastName\":\"\"}")
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.failCode").value("4"));
    }

    @Test
    void accountLifecycle() throws Exception {
        JsonNode created = body(send(post("/api/accounts"),
                "{\"customerNumber\":\"2\",\"accountType\":\"SAVING\",\"interestRate\":1.5,\"initialBalance\":100}")
                .andExpect(status().isCreated()));
        assertThat(created.get("accountId").asText()).isEqualTo("00000005");

        mvc.perform(get("/api/customers/2/accounts"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.totalCount").value(2));
        mvc.perform(get("/api/accounts/99999999"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.accountId").value("00000005"));
        send(put("/api/accounts/5"), "{\"accountType\":\"ISA\",\"interestRate\":2.25,\"overdraftLimit\":50}")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountType").value("ISA"))
                .andExpect(jsonPath("$.overdraftLimit").value(50));
        mvc.perform(get("/api/accounts")).andExpect(jsonPath("$.totalCount").value(5));

        mvc.perform(delete("/api/accounts/5")).andExpect(status().isNoContent());
        mvc.perform(get("/api/accounts/5"))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.failCode").value("1"));
        assertThat(proctran.findBySortCodeAndAccountNumberOrderByIdAsc("987654", "00000005"))
                .extracting(p -> p.getType()).containsExactly("OCA", "ODA");
    }

    @Test
    void creaccValidation() throws Exception {
        send(post("/api/accounts"), "{\"customerNumber\":\"7\",\"accountType\":\"SAVING\"}")
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.failCode").value("1"));
        send(post("/api/accounts"), "{\"customerNumber\":\"1\",\"accountType\":\"BITCOIN\"}")
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.failCode").value("A"));
    }

    @Test
    void dbcrfunDepositWithdrawAndPaymentRules() throws Exception {
        JsonNode deposit = body(send(post("/api/accounts/1/deposit"), "{\"amount\":100.25,\"sortCode\":\"987654\"}")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.availableBalance").value(1351.00)));
        mvc.perform(get("/api/accounts/1/transactions/" + deposit.get("transactionReference").asText()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionType").value("CRE"))
                .andExpect(jsonPath("$.transactionInformation").value("COUNTER RECVED"));

        send(post("/api/accounts/1/deposit"), "{\"amount\":-1.00,\"sortCode\":\"987654\"}")
                .andExpect(status().isCreated()).andExpect(jsonPath("$.actualBalance").value(1350.00));
        send(post("/api/accounts/1/deposit"), "{\"amount\":-99999,\"sortCode\":\"987654\",\"facilityType\":496}")
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.failCode").value("3"));
        send(post("/api/accounts/4/deposit"), "{\"amount\":10,\"sortCode\":\"987654\",\"facilityType\":496}")
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.failCode").value("4"));
        send(post("/api/accounts/9/deposit"), "{\"amount\":10,\"sortCode\":\"987654\"}")
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.failCode").value("1"));
    }

    @Test
    void xfrfunMovesMoneyAndLogsOnFromAccount() throws Exception {
        send(post("/api/accounts/2/transfer"), "{\"toAccountId\":\"1\",\"amount\":400}")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fromAccountAvailableBalance").value(8000.00))
                .andExpect(jsonPath("$.toAccountAvailableBalance").value(1650.75));
        assertThat(proctran.findBySortCodeAndAccountNumberOrderByIdAsc("987654", "00000002"))
                .anySatisfy(p -> {
                    assertThat(p.getType()).isEqualTo("TFR");
                    assertThat(p.getAmount()).isEqualByComparingTo("-400");
                    assertThat(p.getDescription()).isEqualTo(String.format("%-26s", "TRANSFER") + "98765400000001");
                });
        send(post("/api/accounts/2/transfer"), "{\"toAccountId\":\"1\",\"amount\":0}")
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.failCode").value("4"));
        send(post("/api/accounts/2/transfer"), "{\"toAccountId\":\"9\",\"amount\":5}")
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.failCode").value("2"));
        send(post("/api/accounts/2/transfer"), "{\"toAccountId\":\"2\",\"amount\":5}")
                .andExpect(status().isInternalServerError()).andExpect(jsonPath("$.abendCode").value("SAME"));
    }

    @Test
    void inqtranlFiltersPagesAndOrdersNewestFirst() throws Exception {
        mvc.perform(get("/api/accounts/1/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCount").value(2))
                .andExpect(jsonPath("$.limit").value(50))
                .andExpect(jsonPath("$.transactions[0].transactionType").value("DEB"));
        mvc.perform(get("/api/accounts/1/transactions?fromDate=2026-10-29&limit=500"))
                .andExpect(jsonPath("$.totalCount").value(1))
                .andExpect(jsonPath("$.limit").value(100));
        mvc.perform(get("/api/accounts/987654-00000001/transactions"))
                .andExpect(jsonPath("$.totalCount").value(2));
        mvc.perform(get("/api/accounts/1/transactions?offset=1"))
                .andExpect(jsonPath("$.transactions.length()").value(1))
                .andExpect(jsonPath("$.transactions[0].transactionType").value("CRE"));
    }

    @Test
    void delcusRemovesAccountsThenCustomer() throws Exception {
        mvc.perform(delete("/api/customers/1")).andExpect(status().isNoContent());
        mvc.perform(get("/api/customers/1")).andExpect(status().isNotFound());
        mvc.perform(get("/api/accounts/1")).andExpect(status().isNotFound());
        mvc.perform(get("/api/accounts/2")).andExpect(status().isNotFound());
        assertThat(proctran.findBySortCodeAndAccountNumberOrderByIdAsc("987654", "00000000"))
                .extracting(p -> p.getType()).contains("ODC");
        mvc.perform(delete("/api/customers/7"))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.failCode").value("1"));
    }

    @Test
    void companyAndSortCode() throws Exception {
        mvc.perform(get("/api/company"))
                .andExpect(jsonPath("$.companyName").value("CICS Bank Sample Application"))
                .andExpect(jsonPath("$.sortCode").value("987654"));
    }
}
