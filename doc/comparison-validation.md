# Bank of Z — Validation & Comparison

Single consolidated document for the Phase 4 deliverable: side-by-side comparison of the **simulated legacy application** (BMS-derived 3270 simulator, `deliverables/02_legacy_simulation`) and the **modernized application** (Spring Boot API on `:8085` + web frontend on `:3001/?backend=spring`), run with identical scenarios and input data.

**Verification legend:** ✅ measured/executed · 🔶 inferred · ⚠️ assumption or open item.

---

## Table of Contents

1. [Method](#1-method)
2. [Side-by-Side Screen Comparison](#2-side-by-side-screen-comparison)
3. [Identical-Scenario Test Results](#3-identical-scenario-test-results)
4. [Business-Rule Traceability Matrix](#4-business-rule-traceability-matrix)
5. [Functional Comparison Matrix](#5-functional-comparison-matrix)
6. [Code-Quality Findings](#6-code-quality-findings)
7. [Unresolved Migration Gaps](#7-unresolved-migration-gaps)

---

## 1. Method

- **Legacy side:** the BMS-derived simulator (fields, captions, colors, validation reconstructed from `src/base/cics/bms` and COBOL logic — labeled simulation, not a real 3270 recording).
- **Modern side:** `src/modern/bankz-app` on PostgreSQL (demo profile) and the existing web frontend.
- **Evidence:** automated run of the simulator (menu → display customer → transfer) plus live API calls on the modern app with the same scenario inputs.
- **⚠️ Caveat:** the simulator sample data and the modern demo seed are equivalent but not byte-identical (different address/balance values); comparisons are therefore behavioral, not data-byte comparisons.

## 2. Side-by-Side Screen Comparison

Screenshots: `deliverables/02_legacy_simulation/` (legacy sim) and `~/Downloads/bank-of-z-screens.zip` (modern UI).

| Workflow | Legacy (simulated 3270) | Modern | Equivalence |
|---|---|---|---|
| Menu → Display customer | `screen-menu.png`, `screen-customer.png` — BNK1MAI menu, BNK1DCM shows CUSTNO input then full record: sort code 987654, name, address, DOB, credit score, review date, `CUSTOMER FOUND` | `GET /api/customers/1` returns the same fields (customerId, title, name, DOB, address, status) — JSON verified below; Find-customer web page renders it | ✅ fields/function equivalent; interaction model differs (24×80 vs web form) |
| Transfer | `screen-transfer.png` — BNK1TFM: FROM/TO/AMOUNT inputs, both balances after move, `TRANSFERRED 100.00` | `POST /api/accounts/2/transfer` returns from/to balances + transactionReference — JSON verified below | ✅ same operation and outputs |
| Debit beyond balance | `INSUFFICIENT FUNDS` message on row 23, balances unchanged | see gap G-1 — overdraft not enforced | ⚠️ discrepancy found |

## 3. Identical-Scenario Test Results

Executed 2026-11-04 against the running app (curl output verbatim):

| # | Scenario & input | Legacy sim result | Modern app result | Match? |
|---|---|---|---|---|
| S1 | Display customer 1 | `CUSTOMER FOUND` — Mr John Smith, sort 987654, DOB, address, credit 720 | `200` — `{"customerId":"0000000001","title":"Mr","firstName":"John","lastName":"Smith","dateOfBirth":"1985-06-15","address":{…},"customerStatus":"ACTIVE"}` | ✅ same fields; ⚠️ data values differ (simulated vs demo seed) |
| S2 | Display account 2 | Sort 987654, type, actual 2310.55, avail 2310.55 | `200` — `{"accountNumber":"00000002","sortCode":"987654","accountType":"SAVING","availableBalance":8400.00,"actualBalance":8400.00,…}` | ✅ same fields |
| S3 | Transfer 100.00 from acct 2 → acct 1 | Balances 2310.55→2210.55, 1250.00→1350.00; `TRANSFERRED 100.00`; PROCTRAN row | `{"fromAccountAvailableBalance":8300.00,"toAccountAvailableBalance":1350.75,"transactionReference":"20261104185647000000000001"}` — debit leg present in `/accounts/2/transactions` (`"transactionType":"TFR"`) | ✅ same math & transaction record |
| S4 | Transfer 9,999,999.99 from acct 1 (avail 1,250.75, overdraft 500) | `INSUFFICIENT FUNDS`, no balance change | ⚠️ **succeeded** — fromAccount became −9,998,649.24, transactionReference issued | ❌ **discrepancy — G-1** |
| S5 | Invalid menu option / not found | `INVALID OPTION`, `CUSTOMER NOT FOUND` messages | `{"error":"accountId must be 1 to 8 digits: null"}` on bad input; 4xx + message on not found | ✅ equivalent error handling |

## 4. Business-Rule Traceability Matrix

| Rule | Legacy (program/file) | Modern (component) | Test | Status |
|---|---|---|---|---|
| BR-1 sort code 987654 | CRECUST/CREACC literals | `Ids`/`PaymentService` | BankFlowIntegrationTest | ✅ verified in run |
| BR-2 last/random customer | INQCUST | CustomerInquiryService | CustomerInquiryServiceTest | ✅ |
| BR-3 credit checks CRDTAGY1–5 | CRECUST LINKs | CreditAgencyService | customer tests | ✅ implemented |
| BR-4 transfer checks + rollback | XFRFUN | PaymentService `@Transactional` | BankFlowIntegrationTest | ⚠️ balance check gap — G-1 |
| BR-5 debit/credit + PROCTRAN | DBCRFUN | PaymentService | BankFlowIntegrationTest | ✅ |
| BR-6 DELCUS blocked with accounts | DELCUS | CustomerDeleteService | customer tests | ✅ |
| BR-7 DELACC guard | DELACC (inferred) | AccountService | account tests | 🔶 inferred rule |
| BR-8 ABNDPROC CVR1 codes | ABNDPROC | common/AbendService | controller tests | ✅ |
| BR-9 CONTROL counters | shared | Control entity | batch tests | ✅ |
| BR-10 date formats | commareas | CustomerResponse | CustomerResponseTest | ✅ |
| BR-11 IMS mirror | IB* | ImsService | ImsIntegrationTest | ✅ |
| BR-12 statement jobs | BNKSTMT* + JCL | batch package | BatchIntegrationTest | ✅ |

Full chain (program → rule → FR → story → test): `deliverables/01_reverse_engineering/traceability-matrix.md`.

## 5. Functional Comparison Matrix

| Evaluation area | Legacy | Modern | Result |
|---|---|---|---|
| Business rules | COBOL/CICS programs | Spring services | ⚠️ 11/12 verified; BR-4 balance-check gap (G-1) |
| Input validation | BMS attrb + program checks | Bean Validation + service checks (e.g. `accountId must be 1 to 8 digits`) | ✅ equivalent |
| Calculations | DECIMAL(12,2) balances, PROCTRAN | BigDecimal, `Transaction` rows | ✅ verified |
| UI | 24×80 BMS (simulated) | Web pages | ✅ functional replacement |
| Data processing | DB2 + IMS DL/I | PostgreSQL/JPA | ✅ structure preserved; ⚠️ no production data |
| Error handling | Commarea codes + ABNDPROC | HTTP status + error body | ✅ verified |
| Code quality | 33,624 legacy lines | 2,434 Java + 140 SQL | ✅ ~13:1 reduction |

## 6. Code-Quality Findings

Measured (JaCoCo + Surefire + scan): 36/36 tests pass; 88.5% line / 68.7% branch coverage; parameterized JPA only; externalized credentials via env; global exception handler; ~2,434 Java lines for the converted surface.

Findings:
- **F-1 (defect):** transfer allows balance beyond available funds + overdraft — see G-1.
- **F-2 (open):** no authN/authZ — same posture as the legacy sample, must change before production.
- **F-3 (open):** dependency/security scan not run.
- **F-4 (quality):** per-package coverage is uneven (com.bankz.web packages lowest); branch coverage 68.7% leaves error paths partly unexercised.
- **F-5 (open):** no static-analysis gate (Checkstyle/SpotBugs) configured.

## 7. Unresolved Migration Gaps

| ID | Gap | Severity | Evidence |
|---|---|---|---|
| G-1 | **Overdraft/available-balance not enforced on transfer** — a 9,999,999.99 transfer succeeded from an account with 1,250.75 available + 500 overdraft, leaving −9,998,649.24. Legacy XFRFUN rejects this (INSUFFICIENT FUNDS). | High | S4 run above |
| G-2 | No live z/OS parity run — all "legacy" results are from the simulator, not the mainframe | High | environment constraint |
| G-3 | Simulated vs demo data sets differ (addresses, balances) — comparisons are behavioral | Medium | S1–S2 |
| G-4 | 11 files unconverted (IMS Java helpers, MQ clients) | Medium | conversion report |
| G-5 | No mainframe performance baseline; modern timings only | Medium | QA report |
| G-6 | Production data (EBCDIC/packed) not migrated; demo seed only | Medium | scope |
| G-7 | Screen equivalence functional, not pixel/keyboard identical | Low | by design |
| G-8 | No auth, no dependency scan, no load test | Medium | QA open items |
