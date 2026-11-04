# Agile Backlog

Priorities: P0 = done, P1 = done, P2 = remaining/partial. Story IDs used by traceability matrix.

## Epic E1 — Customer management (P0)

| Story | As a… I want… so that… | Acceptance criteria | Status |
|---|---|---|---|
| US-101 | Teller: create a customer with credit checks | Valid input → customer persisted, number from CONTROL; agency failure → error response | Done (`CustomerCreateService`) |
| US-102 | Teller: view a customer by number, last, or random | 200 with customer fields; 404 not-found; 9999999999/0 semantics preserved | Done (`CustomerInquiryService`) |
| US-103 | Teller: update customer details | Changed fields persisted; not-found → error | Done (`CustomerUpdateService`) |
| US-104 | Teller: delete customer without accounts | Deleted; blocked with accounts → error | Done (`CustomerDeleteService`) |

## Epic E2 — Account management (P0)

| Story | Acceptance criteria | Status |
|---|---|---|
| US-201 | Create account for customer | Number generated; visible in `GET /api/accounts` and customer accounts | Done (`AccountService`) |
| US-202 | Inquire/list accounts | Single + all + per-customer lists | Done |
| US-203 | Update account | Type/interest/overdraft updated | Done |
| US-204 | Delete account | Removed; guard conditions honored | Done |

## Epic E3 — Payments (P0)

| Story | Acceptance criteria | Status |
|---|---|---|
| US-301 | Debit/credit account | Balances updated; PROCTRAN row written; insufficient funds rejected | Done (`PaymentService`) |
| US-302 | Transfer between accounts | Atomic; FROM/TO balances correct; transaction record; rollback on failure | Done |
| US-303 | Transaction inquiry | List + detail endpoints return PROCTRAN data | Done (`TransactionInquiryService`) |
| US-304 | Company/sort-code info | `/api/company`, `/api/sortcode` return reference data | Done (`CompanyService`) |

## Epic E4 — Batch (P1)

| Story | Acceptance criteria | Status |
|---|---|---|
| US-401 | Monthly statement on demand | `/api/batch/statements?customerId=` returns COBOL+PL/I equivalent output | Done (`StatementService`, `StatementJobRunner`) |
| US-402 | Seed demo data | `/api/batch/bankdata` or startup loader populates demo rows | Done (`BankDataGenerator`) |

## Epic E5 — IMS channel (P1)

| Story | Acceptance criteria | Status |
|---|---|---|
| US-501 | IMS login/logout | `/api/ims/login` flow | Done (`ImsService`) |
| US-502 | IMS customer/account data | `/api/ims/customers/{id}`, `/api/ims/accounts/{id}` | Done |
| US-503 | IMS transactions | `/api/ims/accounts/{id}/transactions` | Done |
| US-504 | Load LoadData files into relational schema | V3 migration + `ImsDataLoader` | Done |

## Epic E6 — Screens (P1)

| Story | Acceptance criteria | Status |
|---|---|---|
| US-601 | Replace 3270 UI with web pages | Existing `src/frontend` pages call `/api` | Done (functional replacement) |
| US-602 | 3270 simulation for demo | BMS-derived simulator reproduces menu + screens | Done (Phase 2 deliverable) |

## Epic E7 — Reporting & QA (P1)

| Story | Acceptance criteria | Status |
|---|---|---|
| US-701 | Measured conversion dashboard | conversion-report.json + dashboard sections | Done |
| US-702 | Automated test suite | 36 tests, 0 failures, JaCoCo coverage | Done |
| US-703 | Parity matrix | z/OS run required | Open |
| US-704 | Security/perf assessment | Load test + mainframe baseline | Open |

## Dependencies

US-302→301 (transfer reuses debit/credit rules); US-104→E2 (customer delete needs account check); US-401→US-301/303 (statement reads PROCTRAN); US-602→BMS maps (no code dependency); E7 depends on all.
