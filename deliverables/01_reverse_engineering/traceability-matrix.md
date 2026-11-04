# Traceability Matrix

Legacy Program → Business Rule → Functional Requirement → User Story → Test Case → Modern Component

| Legacy program | BR | FR | Story | Test case | Modern component |
|---|---|---|---|---|---|
| `INQCUST.cbl` | BR-1,2,4 | FR-CUS-03/04 | US-102 | TC-CUS-01..04 | `customer/service/CustomerInquiryService.java` |
| `CRECUST.cbl` + `CRDTAGY1–5` | BR-3 | FR-CUS-01/02 | US-101 | TC-CUS-05/06 | `CustomerCreateService.java`, `CreditAgencyService.java` |
| `UPDCUST.cbl` | BR-1 | FR-CUS-05 | US-103 | TC-CUS-07 | `CustomerUpdateService.java` |
| `DELCUS.cbl` | BR-6 | FR-CUS-06 | US-104 | TC-CUS-08 | `CustomerDeleteService.java` |
| `INQACCCU.cbl` | BR-1 | FR-CUS-07 | US-202 | TC-ACC-03 | `AccountService.java` (+ CustomerController `/accounts`) |
| `CREACC/INQACC/INQACCS/UPDACC/DELACC.cbl` | BR-9 | FR-ACC-01..05 | US-201..204 | TC-ACC-01..05 | `AccountService.java`, `AccountRepository.java` |
| `DBCRFUN.cbl` | BR-5 | FR-PAY-01/02 | US-301 | TC-PAY-01..03 | `payment/service/PaymentService.java` |
| `XFRFUN.cbl` | BR-4 | FR-PAY-03/04 | US-302 | TC-PAY-04/05 | `PaymentService.java` |
| `INQTRAND/INQTRANL.cbl` | — | FR-PAY-05 | US-303 | TC-PAY-07/08 | `TransactionInquiryService.java` |
| `GETCOMPY/GETSCODE.cbl` | BR-1 | FR-INF-01/02 | US-304 | TC-INF-01 | `CompanyService.java` |
| `ABNDPROC.cbl` | BR-8 | FR-INF-03 | US-304 | TC-INF-02 | `common/AbendService.java`, `ApiExceptionHandler.java` |
| `BANKDATA.cbl` | — | FR-BAT-02 | US-402 | TC-BAT-03 | `batch/BankDataGenerator.java` |
| `BNKSTMTC.cbl`/`BNKSTMTP.pli` + JCL | BR-12 | FR-BAT-01 | US-401 | TC-BAT-01/02 | `batch/StatementService.java`, `StatementJobRunner.java` |
| `IB*/IBLOGIN*` (IMS COBOL/PLI) | BR-11 | FR-IMS-01..03 | US-501..503 | TC-IMS-01..04 | `ims/ImsService.java`, `ImsController.java` |
| `LOAD*.cbl` | — | FR-IMS-04 | US-504 | TC-IMS-05 | `ims/ImsDataLoader.java` |
| `ims/DBD,PSB/*.asm` | — | FR-IMS | US-504 | — | `db/migration/V3__create_ims_tables.sql` |
| `BNK1*/BNKMENU + cics/bms/*.bms` | — | FR-CUS/ACC/PAY UI | US-601 | UI flows | `src/frontend` web pages (replaced, not translated) |
| `ims/java/*` | — | — | — | — | Not converted (open) |
| `mq/java/*` | — | — | — | — | Not converted (open) |
| `CUSTOMER.cpy`, `ACCDB2.cpy`, `INQCUSTZ.cpy`, etc. | — | I/O specs | — | — | JPA entities + response DTOs |
| `CONTROLI/ACCTCTRL.cpy` | BR-9 | FR-ACC-01, FR-CUS-01 | US-101/201 | — | `control_table` + repository logic |

Verification flags: BR-4 rollback, BR-2 special IDs, BR-3 agency chain, BR-6 delete guard are code-verified and covered by the 36 passing tests; BR-7 marked *(inferred)* — confirm during z/OS parity run.
