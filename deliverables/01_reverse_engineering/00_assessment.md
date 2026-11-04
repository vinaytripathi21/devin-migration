# Phase 1 — Initial Repository Assessment

Repository: `Bank-of-Z` (IBM CICS Banking Sample Application derivative)
Assessment basis: physical scan of `src/` (measured, not inferred)

## Detected technologies

| Technology | Evidence | Location |
|---|---|---|
| COBOL (CICS online) | 32 programs, `EXEC CICS`, `EXEC SQL` | `src/base/cics/cobol/` |
| COBOL (batch + IMS DL/I) | 1 batch + 11 IMS programs | `src/base/batch/cobol/`, `src/base/ims/cobol/` |
| PL/I (batch + IMS) | `BNKSTMTP.pli`, `IBLOGIN.pli` | `src/base/batch/pli/`, `src/base/ims/pli/` |
| COBOL copybooks | 42 record/map/layout definitions | `src/base/cics/copy/` |
| IMS copybooks | 4 | `src/base/ims/copy/` |
| BMS maps (3270 screens) | 10 mapsets | `src/base/cics/bms/` |
| JCL | 2 batch jobs (`BNKSTMTC`, `BNKSTMTP`) | `src/base/batch/jcl/` |
| Assembler (IMS DBD/PSB) | 9 DBD + 8 PSB | `src/base/ims/DBD/`, `src/base/ims/PSB/` |
| DB2 DDL templates | `*.setup` files | `src/base/.setup/` (via `conversion/input/`) |
| Java (existing, unconverted) | 8 IMS Java + 3 MQ client classes | `src/base/ims/java/`, `src/base/mq/` |
| REST contract | `openapi.yaml` (z/OS Connect) | `src/api/src/main/api/` |
| Web frontend | Static HTML/JS, Carbon Design | `src/frontend/` |

## Application modules

1. **CICS online banking** (`BNK1*` screen programs + function programs): customer inquiry/create/update/delete, credit-agency checks, account create/inquire/list/update/delete, debit/credit, transfer, transaction inquiry, company/sort-code info, abend handler.
2. **Batch**: `BANKDATA` data generator, `BNKSTMTC`/`BNKSTMTP` monthly statement programs driven by JCL.
3. **IMS**: `IB*` transactions (login, customer data, account summary, transactions), `LOAD*` load programs, DBD/PSB definitions.
4. **MQ**: Bank-of-Q transfer clients (`TransferMoneyToBOZ`, etc.).
5. **Frontend**: existing web pages that call the z/OS Connect REST API.

## Business processes identified (from code)

- Customer lifecycle: create (with credit-agency validation `CRDTAGY1–5`), inquire (incl. "last customer" 9999999999 and random selection 0), update, delete.
- Account lifecycle: create, inquire, list all, list by customer, update (account type/interest/overdraft), delete.
- Money movement: debit/credit (`DBCRFUN`), transfer (`XFRFUN`) with available-balance check, transaction record writing and rollback on failure.
- Inquiries: transactions by account/date (`INQTRAND`/`INQTRANL`), company info (`GETCOMPY`), sort code (`GETSCODE`).
- Batch: monthly statements per customer; sequential `BANKDATA` test-data generation.
- IMS: login/logout, customer data, account summary, transaction history (`IBTRAN`), load utilities.

## Available screen definitions

10 BMS mapsets, 24×80, 3270-2: `BNK1MAI` (menu), `BNK1CAM`/`BNK1CCM`/`BNK1CDM` (customer create/change/delete+inquiry), `BNK1ACC`/`BNK1UAM`/`BNK1DAM`/`BNK1DCM` (account create/update/delete/inquiry), `BNK1TFM`/`BNK1B2M` (transfer). Parseable with `DFHMDF POS/INITIAL/ATTRB` — sufficient for a faithful 3270-style simulation.

## Data dependencies

- DB2 tables: `CUSTOMER`, `ACCOUNT`, `PROCTRAN`, `CONTROL` (sort code, counters).
- IMS segments: `CUSTOMER`, `ACCOUNT`, `CUSTACCS`, `ACCTYPE`, `CUSTTYPE`, `HISTORY`, `TSTAT`, `TSTATTYP`, `TTYPE`.
- Fixed sort code `987654` (validated in `GETSCODE`/`INQCUST`).
- Record layouts via copybooks (`CUSTOMER.cpy`, `ACCDB2.cpy`, `INQCUSTZ.cpy` commarea, etc.).

## Migration complexity

| Area | Complexity | Notes |
|---|---|---|
| CICS business logic → Spring services | Medium | Straight SQL/JPA mapping; CICS abend → HTTP errors |
| 3270 BMS screens → React web | Medium | Replaced by existing frontend, not 1:1 translated |
| DB2 → PostgreSQL | Low–medium | DECIMAL/date types map directly; EBCDIC data not migrated |
| Batch JCL → REST-triggered batch | Medium | `BNKSTMT*` converted; JCL orchestration replaced by endpoint |
| IMS DL/I → JPA | Medium–high | Hierarchical segments flattened to relational tables |
| MQ clients | Open | Not converted (Bank-of-Q side not in scope of this repo) |
| IMS Java helpers | Open | Not converted |

## Missing information

- Real DB2/IMS production data (EBCDIC/packed) — demo data used instead.
- z/OS runtime for parity testing and performance baseline.
- Original nonfunctional requirements (volumes, SLAs) — inferred only.
- Bank-of-Q MQ counterpart configuration.

## Proposed implementation plan

1. Reverse-engineer `src/base` (this phase: BRD, FRD, backlog, tech docs, test cases, traceability).
2. Simulate 3270 screens from BMS maps (Phase 2).
3. Convert to one Spring Boot + PostgreSQL app (`src/modern/bankz-app`, done) + existing web frontend (Phase 3).
4. Compare legacy vs modern on identical scenarios (Phase 4).
5. QA report from real test artifacts (Phase 5).
6. Demo package (Phase 6), executive report (Phase 7).

## Risk register (initial)

| Risk | Impact | Mitigation |
|---|---|---|
| No z/OS parity run | Functional correctness unproven vs mainframe | Label all results; keep parity matrix open |
| Demo data only | Calculations validated, data realism limited | Note limitation; load IMS `LoadData` files |
| Screen translation | BNK1* not literally converted | Reconstruct via BMS simulation + modern UI |
