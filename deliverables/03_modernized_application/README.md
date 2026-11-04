# Phase 3 — Modernized Application

## What was built

One Spring Boot 3 / Java 21 application at `src/modern/bankz-app` (single deployable — no unnecessary microservices), backed by PostgreSQL, converting the `src/base` business logic. A copy lives at `conversion/output`; inputs at `conversion/input`.

## Components

| Package | Converts | REST endpoints |
|---|---|---|
| `com.bankz.customer` | INQCUST, CRECUST, CRDTAGY1–5, UPDCUST, DELCUS, INQACCCU | `/api/customers`, `/api/customers/{id}`, `/api/customers/{id}/accounts` |
| `com.bankz.account` | CREACC, INQACC, INQACCS, UPDACC, DELACC | `/api/accounts`, `/api/accounts/{id}`, `/api/accounts/{id}/balances` |
| `com.bankz.payment` | DBCRFUN, XFRFUN, INQTRAND, INQTRANL, GETCOMPY, GETSCODE, ABNDPROC | `/api/accounts/{id}/deposit|transfer|transactions`, `/api/company` |
| `com.bankz.batch` | BANKDATA, BNKSTMTC/BNKSTMTP + JCL | `/api/batch/statements`, `/api/batch/bankdata` |
| `com.bankz.ims` | IB* transactions, LOAD*, DBD/PSB | `/api/ims/**` |
| `com.bankz.common` | shared error/abend handling | — |

Frontend: existing `src/frontend` web pages call the API (`?backend=spring`); the 3270 BMS screens are replaced, not line-translated.

## Architecture decisions

- Single app with domain packages (per user approval); layered controller → service → JPA repository.
- API contract follows `src/api/src/main/api/openapi.yaml` (z/OS Connect shapes).
- Flyway `V1–V3` creates DB2-equivalent + IMS-flattened tables; demo profile seeds data.
- CICS commarea semantics preserved: `9999999999`=last customer, `0`=random, `COMM-SUCCESS`/abend codes → HTTP status + error body.
- Externalized config via env (`DB_URL`, `SPRING_PROFILES_ACTIVE`, `SERVER_PORT`).

## Build & run

```
docker run -d --name bankz-postgres -e POSTGRES_DB=bankzapp -e POSTGRES_USER=bankz -e POSTGRES_PASSWORD=bankz -p 5432:5432 postgres:16
cd src/modern/bankz-app && mvn package          # or use the jar in target/
DB_URL=jdbc:postgresql://localhost:5432/bankzapp java -jar target/bankz-app-0.1.0.jar \
  --spring.profiles.active=demo --server.port=8085
python3 -m http.server 3001 --directory src/frontend   # open http://localhost:3001/?backend=spring
```

Tests: `mvn test` → 36 tests, 0 failures; JaCoCo report under `target/site/jacoco`.

## Migration mapping

See `conversion/README.md` (legacy file → Java/SQL file) and `deliverables/01_reverse_engineering/traceability-matrix.md` (program → rule → requirement → story → test).

## Modernization execution log (chronological, for the demo)

1. Scanned `src/base` (132 files, 33,624 lines); classified layers and DB2/CICS/DL/I usage — `scripts/migration-inventory.py`.
2. Pilot conversion: `INQCUST.cbl` → `customer-service` (later merged into bankz-app `customer` package) — COBOL→Java semantics verified (random/last customer, not-found abend).
3. Customer wave: CRECUST + CRDTAGY1–5, UPDCUST, DELCUS.
4. Account + payment waves: account CRUD, debit/credit, atomic transfer with rollback, transaction inquiry, company/sort-code, abend handler.
5. Batch wave: BANKDATA generator + statement service replacing JCL jobs (REST-triggered).
6. IMS wave: IB* flows + LoadData loaders + V3 relational schema.
7. Screens: kept web frontend as replacement (BMS not line-translated); built BMS-derived 3270 simulator for demo (Phase 2).
8. Verified: `mvn test` (36 pass), JaCoCo 88.5%/68.7%, API timing run vs running app; dashboard `conversion-report.json` regenerated.
9. Fixes along the way: Maven Central 429 (mirror), port conflict (8085), IMS LoadData missing trailing newline, batch optimistic-locking.
