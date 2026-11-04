# Phase 4 — Legacy vs Modern Comparison

Basis: simulated 3270 screens (BMS-derived, `deliverables/02_legacy_simulation`) vs the running modern stack (`src/modern/bankz-app` on `:8085` + `src/frontend` on `:3001/?backend=spring`). Equivalent scenarios, identical demo data.

## Comparison matrix (evidence-based)

| Evaluation area | Legacy application | Modern application | Result |
|---|---|---|---|
| Business rules (BR-1..12) | CICS COBOL: fixed sort code, last/random customer, credit checks, delete guards, transfer rollback | Same rules in Spring services, verified by 36 tests | **Pass** (test-verified) |
| Input validation | BMS field attrb (NUM/length), program checks | Bean Validation + service checks; same rejections (not-found, insufficient funds, required fields) | **Pass** |
| Calculations | DECIMAL(12,2) available/actual balance updates, PROCTRAN writes | BigDecimal, same arithmetic, transaction record each success | **Pass** (integration tests) |
| User interface | 24×80 BMS screens (simulated) | Web pages (Carbon Design), same workflows and fields | **Compared** — functionally equivalent, different interaction model |
| Data processing | DB2 (EXEC SQL) + IMS DL/I segments | PostgreSQL via JPA; IMS tables flattened (V3) | **Pass** — structure preserved; real data not migrated |
| Error handling | Commarea `COMM-SUCCESS`/`COMM-FAIL-CODE`, ABNDPROC codes | HTTP status + `{message}` body, `AbendService` codes | **Pass** |
| Response format | `response_200.yaml` commarea JSON | Same field names via `openapi.yaml` | **Pass** |
| Code quality | 33,624 legacy lines (COBOL/PLI/JCL/ASM) | 2,434 Java + 140 SQL + 512 test lines (99 files) | **Measured** — ~13:1 reduction in business-logic lines |
| Coverage | n/a | 88.5% line, 68.7% branch (JaCoCo) | **Measured** |

## Side-by-side scenario evidence

| Scenario | Legacy (sim) | Modern | Identical? |
|---|---|---|---|
| Menu → display customer 1 | `screen-customer.png` (BNK1DCM shows John Smith record) | `GET /api/customers/1` same fields; Find-customer page | Yes — fields match |
| Transfer 100.00 from 00000002 → 00000001 | `screen-transfer.png`: balances 2310.55→2210.55, 1250→1350 | `POST /api/accounts/00000002/transfer` same math + PROCTRAN | Yes |
| Debit beyond available balance | INSUFFICIENT FUNDS message | 4xx + error message | Yes |
| Create customer | credit-check pass, sequential number | same via CRDTAGY logic in service | Yes |

Screenshots in `deliverables/02_legacy_simulation/` (legacy) and `~/Downloads/bank-of-z-screens.zip` (modern UI).

## Traceability

Legacy → modern file map: `conversion/README.md`. Rule-level chain: `deliverables/01_reverse_engineering/traceability-matrix.md`.

## Outstanding issues / limitations

- **No z/OS parity run** — modern-vs-simulation comparison only; real mainframe comparison pending (PT-01..05 in test-cases.md).
- No mainframe performance baseline; only modern timings measured (~4.6 ms median, 9 endpoints).
- Screen equivalence is functional, not pixel/keyboard identical (web UI replaces BMS).
- Demo data, not migrated production (EBCDIC/packed) data.
- IMS Java + MQ clients unconverted (11 files).
