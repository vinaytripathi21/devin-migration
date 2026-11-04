# Phase 5 — Testing & Quality Assurance Report

All figures measured from real artifacts (Maven Surefire XML + JaCoCo CSV in `conversion/output/test-reports`, API timing run of `scripts/conversion-report.py`). No estimated values.

## Test execution (Maven Surefire, mvn test, 2026-11-04)

| Suite | Scope | Tests | Passed | Failed | Skipped | Time |
|---|---|---|---|---|---|---|
| BankFlowIntegrationTest | customers+accounts+payments end-to-end | 10 | 10 | 0 | 0 | 5.90s |
| CustomerInquiryServiceTest | inquiry rules (last/random/not-found) | 7 | 7 | 0 | 0 | 0.55s |
| ImsIntegrationTest | IMS channel + load | 6 | 6 | 0 | 0 | 0.05s |
| CustomerControllerIntegrationTest | REST layer | 5 | 5 | 0 | 0 | 0.03s |
| BatchIntegrationTest | statements + data gen | 5 | 5 | 0 | 0 | 0.14s |
| CustomerResponseTest | commarea→DTO mapping | 3 | 3 | 0 | 0 | 0.00s |
| **Total** | | **36** | **36** | **0** | **0** | **6.67s** |

Tests run against H2 in PostgreSQL mode; demo data seeded via Flyway/demo profile.

## Code coverage (JaCoCo 0.8.12)

- Line coverage: **88.5%** (all `com.bankz.*` packages weighted)
- Branch coverage: **68.7%**
- Per-package detail rendered on the migration dashboard ("Generated Java code and coverage").

## API validation

All 9 measured endpoints returned HTTP 200 during the timing run (`GET /api/customers/1`, `/customers/1/accounts`, `/accounts`, `/accounts/1`, `/accounts/{id}/transactions`, `/company`, `/ims/customers/1`, `/ims/accounts/1`, `/batch/statements`). Response times (20 samples + 3 warm-ups each): median **4.6 ms**; worst p95 72 ms (`/customers/1/accounts`). Single user, laptop, Docker PostgreSQL — not a load test.

## Functional parity status

Business rules BR-1..BR-12 implemented; code-verified rules covered by tests (see traceability matrix). **Parity vs the real mainframe is OPEN** — no z/OS execution was possible in this environment; parity matrix + 5 proposed parity scenarios are recorded in `deliverables/01_reverse_engineering/test-cases.md` and on the dashboard.

## Static analysis / security findings

| Item | Status |
|---|---|
| Input validation | Bean Validation + service checks on all endpoints |
| Error handling | Global `ApiExceptionHandler`; abend codes preserved |
| SQL injection surface | Parameterized JPA only; no string-built SQL |
| Credentials | Externalized via env (`DB_URL`); none in code |
| AuthN/AuthZ | **None** — same as legacy sample; must add before any production use (open item) |
| Dependency check | Not run — open item |

## Unresolved defects / open items

None failing in tests. Open (non-defect): z/OS parity run, mainframe perf baseline, load test, production data migration, OpenAPI contract diff, auth, dependency scan. Listed on the dashboard "Still to do" section.
