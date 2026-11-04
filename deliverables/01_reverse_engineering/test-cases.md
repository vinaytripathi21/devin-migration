# Test Documentation — Functional Test Cases

Cases marked ✅ are implemented and passing in `src/modern/bankz-app` (Maven Surefire). Others are parity/boundary scenarios proposed for the z/OS run.

## Customer

| ID | Type | Scenario | Expected | Status |
|---|---|---|---|---|
| TC-CUS-01 | Positive | Inquiry customer 1 | 200 + customer fields (name, DOB, address) | ✅ `CustomerInquiryServiceTest`, `CustomerControllerIntegrationTest` |
| TC-CUS-02 | Positive | Inquiry 9999999999 | Most recently created customer | ✅ |
| TC-CUS-03 | Positive | Inquiry 0 | A random existing customer | ✅ |
| TC-CUS-04 | Negative | Inquiry unknown id | Not-found error response | ✅ |
| TC-CUS-05 | Positive | Create valid customer | Persisted; sequential number; credit checks pass | ✅ (BankFlow) |
| TC-CUS-06 | Negative | Create with failing credit agency | Creation aborted with agency failure | ✅ (BankFlow) |
| TC-CUS-07 | Positive | Update name/address | Fields updated | ✅ |
| TC-CUS-08 | Negative | Delete customer with accounts | Rejected | ✅ |
| TC-CUS-09 | Boundary | Customer number at max value | Handled, no overflow | Proposed (parity) |

## Accounts

| ID | Type | Scenario | Expected | Status |
|---|---|---|---|---|
| TC-ACC-01 | Positive | Create account | Number generated; linked to customer | ✅ (BankFlow) |
| TC-ACC-02 | Positive | Inquiry account | Correct balances/type | ✅ |
| TC-ACC-03 | Positive | List all / by customer | Consistent sets | ✅ |
| TC-ACC-04 | Positive | Update overdraft/type | Updated | ✅ |
| TC-ACC-05 | Negative | Delete with balance | Rejected or guarded | ✅ (BankFlow) |

## Payments

| ID | Type | Scenario | Expected | Status |
|---|---|---|---|---|
| TC-PAY-01 | Positive | Credit account | Balances +amount; PROCTRAN row | ✅ |
| TC-PAY-02 | Positive | Debit within available balance | Balances −amount; PROCTRAN row | ✅ |
| TC-PAY-03 | Negative | Debit beyond available balance | Rejected, no PROCTRAN | ✅ |
| TC-PAY-04 | Positive | Transfer A→B | A−amt, B+amt, one PROCTRAN, atomic | ✅ (BankFlow) |
| TC-PAY-05 | Negative | Transfer exceeding balance | Rejected; no partial update (rollback) | ✅ |
| TC-PAY-06 | Boundary | Transfer amount 0 / max precision | Rejected or exact decimal handling | Proposed (parity) |
| TC-PAY-07 | Positive | Transaction list newest-first | Order + fields | ✅ |
| TC-PAY-08 | Positive | Transaction detail | Full record | ✅ |

## Reference

| ID | Scenario | Expected | Status |
|---|---|---|---|
| TC-INF-01 | `/api/company` | Company name + sortcode 987654 | ✅ |
| TC-INF-02 | Abend path | Failure code/description surfaced | ✅ |

## Batch

| ID | Scenario | Expected | Status |
|---|---|---|---|
| TC-BAT-01 | Statement for customer with accounts+transactions | Header, per-account sections, totals | ✅ `BatchIntegrationTest` |
| TC-BAT-02 | Statement for customer without data | Empty sections, no error | ✅ |
| TC-BAT-03 | Seed data generation | Customers/accounts/transactions created | ✅ |

## IMS

| ID | Scenario | Expected | Status |
|---|---|---|---|
| TC-IMS-01 | Login/logout | Session lifecycle | ✅ `ImsIntegrationTest` |
| TC-IMS-02 | IMS customer inquiry | Customer + accounts from ims tables | ✅ |
| TC-IMS-03 | IMS account summary | Balances | ✅ |
| TC-IMS-04 | IMS transactions | History list | ✅ |
| TC-IMS-05 | LoadData loading | Record counts match `.data` files | ✅ |

## Parity scenarios (require z/OS run — OPEN)

| ID | Scenario | Compare |
|---|---|---|
| PT-01 | Same customer lifecycle both systems | Field-for-field responses |
| PT-02 | Same transfer sequence | Ending balances + PROCTRAN counts |
| PT-03 | Statement output vs SYSOUT | Section-for-section |
| PT-04 | Failure injection (missing keys, duplicates) | Error codes/messages |
| PT-05 | Packed-decimal edge amounts | Calculation equality |
