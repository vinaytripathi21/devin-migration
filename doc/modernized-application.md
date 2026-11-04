# Bank of Z — Modernized Application

Single consolidated document for the Phase 3 deliverable: migration of the legacy mainframe application to the modern stack — **Java 21 · Spring Boot 3 · React/static frontend · PostgreSQL** — with preserved business rules, modular code, REST APIs, automated tests, and migration documentation.

**Verification legend:** ✅ verified/measured · 🔶 inferred · ⚠️ assumption or open item.

---

## Table of Contents

1. [Target Architecture](#1-target-architecture)
2. [Migration Scope & Code Changes Summary](#2-migration-scope--code-changes-summary)
3. [Legacy → Modern Mapping](#3-legacy--modern-mapping)
4. [Preserved Business Rules & Behavior](#4-preserved-business-rules--behavior)
5. [Modular Code Structure](#5-modular-code-structure)
6. [API Documentation](#6-api-documentation)
7. [Frontend Screens](#7-frontend-screens)
8. [Database Migration](#8-database-migration)
9. [Automated Tests & Results](#9-automated-tests--results)
10. [Build & Run Instructions](#10-build--run-instructions)
11. [Migration Execution Log](#11-migration-execution-log)
12. [What Is Not Migrated / Open Items](#12-what-is-not-migrated--open-items)

---

## 1. Target Architecture

```
┌────────────────┐     HTTP/JSON      ┌─────────────────────────────┐
│  src/frontend  │ ───────────────▶   │  src/modern/bankz-app       │
│ (React-style   │  ?backend=spring   │  Spring Boot 3 / Java 21    │
│  static pages) │                    │                             │
└────────────────┘                    │  controller → service → JPA │
                                      │  repository → Flyway schema │
                                      └──────────────┬──────────────┘
                                                     │ JDBC
                                      ┌──────────────▼──────────────┐
                                      │  PostgreSQL 16 (bankzapp)   │
                                      └─────────────────────────────┘
```

Decisions: ✅ one deployable app with domain packages (per project agreement — no microservices); ✅ layered controller → service → repository; ✅ API contract follows `src/api/openapi.yaml` (the z/OS Connect shapes); ✅ externalized config via env (`DB_URL`, `SPRING_PROFILES_ACTIVE`, `SERVER_PORT`).

## 2. Migration Scope & Code Changes Summary

| Metric | Measured |
|---|---|
| Legacy files scanned (`src/base`) | 132 files / 33,624 lines |
| Files converted to Java/SQL | 99 of 132 (90% of eligible files) |
| Lines converted | 20,670 of 21,739 eligible lines (**95.1%**) |
| Replaced (3270/BMS screens → web UI) | 22 files / 11,885 lines |
| Not converted (IMS Java helpers, MQ clients) | 11 files / 1,069 lines |
| New Java code | 53 files / 2,434 lines |
| Test code | 6 files / 512 lines |
| SQL migrations | 5 files / 140 lines |

~13:1 reduction in business-logic lines (legacy → Java+SQL). ✅ All figures measured by `scripts/conversion-report.py`, not estimated.

## 3. Legacy → Modern Mapping

| Legacy (input) | Modern (output) |
|---|---|
| `cics/cobol/INQCUST.cbl` | `customer/service/CustomerInquiryService.java` |
| `cics/cobol/CRECUST.cbl` | `customer/service/CustomerCreateService.java` |
| `cics/cobol/CRDTAGY1–5.cbl` | `customer/service/CreditAgencyService.java` |
| `cics/cobol/UPDCUST.cbl` | `customer/service/CustomerUpdateService.java` |
| `cics/cobol/DELCUS.cbl` | `customer/service/CustomerDeleteService.java` |
| `cics/cobol/CREACC, INQACC, INQACCS, INQACCCU, UPDACC, DELACC.cbl` | `account/service/AccountService.java` |
| `cics/cobol/DBCRFUN.cbl`, `XFRFUN.cbl` | `payment/service/PaymentService.java` |
| `cics/cobol/INQTRAND.cbl`, `INQTRANL.cbl` | `payment/service/TransactionInquiryService.java` |
| `cics/cobol/GETCOMPY.cbl`, `GETSCODE.cbl` | `payment/service/CompanyService.java` |
| `cics/cobol/ABNDPROC.cbl` | `common/AbendService.java` |
| `cics/cobol/BANKDATA.cbl` | `batch/BankDataGenerator.java` |
| `batch/cobol/BNKSTMTC.cbl`, `batch/pli/BNKSTMTP.pli` | `batch/StatementService.java` |
| `batch/jcl/BNKSTMTC.jcl`, `BNKSTMTP.jcl` | `batch/StatementJobRunner.java` (REST-triggered job) |
| `ims/cobol/IB*`, `ims/pli/IBLOGIN.pli` | `ims/ImsService.java` |
| `ims/cobol/LOAD*.cbl` | `ims/ImsDataLoader.java` |
| DB2 table DDL, `ims/DBD/*.asm` | `db/migration/V1–V3*.sql` (PostgreSQL) |

Copybooks (`*.cpy`, 39 files) merged into JPA entities/DTOs. Full file-level map: `conversion/README.md`. Input/output copies: `conversion/input`, `conversion/output`.

## 4. Preserved Business Rules & Behavior

| Rule | Legacy implementation | Modern implementation | Status |
|---|---|---|---|
| Fixed sort code 987654 | COBOL literal | Service constant | ✅ test-verified |
| `9999999999` = last customer, `0` = random | INQCUST commarea | Same API semantics | ✅ test-verified |
| CRDTAGY1–5 sequential credit checks | Linked programs | `CreditAgencyService` chain | ✅ implemented |
| Available-balance check before debit/transfer | DBCRFUN/XFRFUN | Same in `PaymentService` | ✅ test-verified |
| Transfer atomicity + SYNCPOINT/ROLLBACK | XFRFUN | Spring `@Transactional` | ✅ test-verified |
| PROCTRAN transaction record | CICS file write | `Transaction` table row | ✅ test-verified |
| DELCUS blocked when accounts exist | COBOL check | Same guard | ✅ implemented |
| ABNDPROC error codes (CVR1…) | Abend handler | `AbendService` + HTTP error body | ✅ implemented |
| Date formats DD MM YYYY / YYYYMMDD | Commarea | `CustomerResponse` conversion | ✅ test-verified |
| IMS channel mirrors | IB* transactions | `ImsService` endpoints | ✅ test-verified |
| CONTROL-table counters | DB2 counters row | `Control` entity | ✅ implemented |
| Statement jobs | JCL + batch | `StatementJobRunner` via REST | ✅ implemented |

## 5. Modular Code Structure

`src/modern/bankz-app` — 59 Java files in `com.bankz.<domain>` packages:

| Package | Responsibility |
|---|---|
| `customer` | domain (`Customer`, `CustomerKey`, `CustomerRepository`), services (inquiry, create, update, delete, credit check), `web/` (controller, response, exception handler) |
| `account` | `AccountService` + CRUD endpoints |
| `payment` | `PaymentService` (debit/credit/transfer), `TransactionInquiryService`, `CompanyService` |
| `batch` | `BankDataGenerator`, `StatementService`, `StatementJobRunner` |
| `ims` | `ImsService`, `ImsDataLoader`, IMS REST endpoints |
| `common` | `AbendService`, shared error handling |

## 6. API Documentation

Base URL: `http://localhost:8085/api` — contract follows `src/api/openapi.yaml`.

| Area | Method & path | Description |
|---|---|---|
| Customers | `GET /customers`, `GET /customers/{id}`, `POST /customers`, `PUT /customers/{id}`, `DELETE /customers/{id}` | Customer lifecycle |
| | `GET /customers/{id}/accounts` | Accounts by customer (INQACCCU) |
| Accounts | `GET /accounts`, `GET /accounts/{id}`, `POST /accounts`, `PUT /accounts/{id}`, `DELETE /accounts/{id}`, `GET /accounts/{id}/balances` | Account management |
| Payments | `POST /accounts/{id}/deposit`, `POST /accounts/{id}/transfer`, `GET /accounts/{id}/transactions` | DBCRFUN/XFRFUN/INQTRAN* |
| Company | `GET /company` | GETCOMPY/GETSCODE |
| Batch | `POST /batch/statements`, `POST /batch/bankdata` | Statement & data-gen jobs |
| IMS | `GET /ims/customers/{id}`, `POST /ims/login`, `GET /ims/accounts/{id}/transactions` | IB* flows |

Errors: HTTP status + `{ "message": ... }` preserving commarea fail-codes and ABNDPROC codes.

## 7. Frontend Screens

The existing `src/frontend` pages replace the 3270 BMS screens (functional replacement — not line-by-line translation). Served on `:3001`; `?backend=spring` routes API calls to the Spring Boot app; `?backend=zos` targets z/OS Connect. Includes `migration-dashboard.html` showing the measured conversion report (converted %, file map, tests, coverage, API timings).

## 8. Database Migration

- **DB2 → PostgreSQL:** `V1__create_customer.sql` etc. — `BANKZ.CUSTOMER`, `ACCOUNT`, `PROCTRAN`, `CONTROL` recreated with equivalent columns/types (packed DECIMAL → `NUMERIC`, EBCDIC CHAR → `VARCHAR`).
- **IMS segments → relational:** `V3` flattens the DBD/PSB hierarchies into `IMS_CUSTOMER`, `IMS_ACCOUNT`, `IMS_TRANSACTION`.
- **Demo data:** seeded under `--spring.profiles.active=demo` (2 customers, 3 accounts, 3 transactions).

## 9. Automated Tests & Results

`mvn test` — ✅ **36/36 tests passed, 0 failures** (Surefire XML in `conversion/output/test-reports`).

| Suite | Tests | Result |
|---|---|---|
| BankFlowIntegrationTest (customers+accounts+payments E2E) | 10 | 10 pass |
| CustomerInquiryServiceTest | 7 | 7 pass |
| ImsIntegrationTest | 6 | 6 pass |
| CustomerControllerIntegrationTest | 5 | 5 pass |
| BatchIntegrationTest | 5 | 5 pass |
| CustomerResponseTest | 3 | 3 pass |

Coverage (JaCoCo): ✅ 88.5% line / 68.7% branch. API timing: ✅ all 9 endpoints HTTP 200, median 4.6 ms, worst p95 72 ms (laptop, Docker PostgreSQL — not a load test).

## 10. Build & Run Instructions

```
docker run -d --name bankz-postgres -e POSTGRES_DB=bankzapp \
  -e POSTGRES_USER=bankz -e POSTGRES_PASSWORD=bankz -p 5432:5432 postgres:16
# (or: docker start bankz-postgres)

cd src/modern/bankz-app && mvn package        # or use existing target/bankz-app-0.1.0.jar
DB_URL=jdbc:postgresql://localhost:5432/bankzapp \
  java -jar target/bankz-app-0.1.0.jar --spring.profiles.active=demo --server.port=8085

python3 -m http.server 3001 --directory src/frontend
# http://localhost:3001/?backend=spring · dashboard: /migration-dashboard.html
```

## 11. Migration Execution Log

| # | Step | Result |
|---|---|---|
| 1 | Scanned `src/base`; classified layers, SQL/CICS/DL-I usage | Inventory JSON |
| 2 | Pilot: `INQCUST` → Spring service | Verified vs COBOL semantics |
| 3 | Customer wave (CRECUST, CRDTAGY1–5, UPDCUST, DELCUS) | Tests pass |
| 4 | Account + payment waves (incl. atomic transfer, transaction inquiry) | Tests pass |
| 5 | Batch wave (BANKDATA, statement service, JCL→job runner) | Tests pass |
| 6 | IMS wave (IB*, LOAD*, V3 schema) | Tests pass |
| 7 | Screens → web frontend (functional replacement) + BMS simulator for comparison | Verified |
| 8 | Full `mvn test`, JaCoCo, API timing run, dashboard regeneration | 36/36 pass |
| 9 | Fixes en route: Maven Central 429 mirror, port 8085 conflict, IMS LoadData trailing newline, batch optimistic locking | Resolved |

## 12. What Is Not Migrated / Open Items

- 11 files (IMS Java helpers, MQ clients) — Java already / out of scope.
- Production data migration (EBCDIC/packed) and reconciliation.
- Live z/OS parity run and mainframe performance baseline.
- AuthN/AuthZ (legacy sample had none), dependency/security scan, load test.
- Screens replaced functionally — pixel-level BMS equivalence is not claimed.
