# Business Requirements Document (BRD)

System analyzed: CICS Banking Sample Application ("Bank of Z")
Source: `src/base` (COBOL/PL/I/JCL/IMS/DB2/BMS). Rules below are extracted from code; items marked *(inferred)* cannot be independently verified without production documentation.

## 1. Business objectives

- Provide teller/back-office banking operations for a single branch (sort code `987654`): customer management, account management, deposits/withdrawals, transfers, and transaction inquiry.
- Provide month-end statement generation in batch.
- Provide equivalent customer data access via IMS (parallel legacy channel).
- Modernize to Java 21 / Spring Boot / PostgreSQL / React without losing business behavior.

## 2. Existing business processes

| Process | Legacy implementation | Channel |
|---|---|---|
| Register customer | `CRECUST` + credit checks `CRDTAGY1–5` | CICS `BNK1CAM` |
| View customer | `INQCUST` (direct, "last" 9999999999, random 0) | CICS `BNK1CCM` |
| Change customer | `UPDCUST` | CICS `BNK1CCM` |
| Remove customer | `DELCUS` (blocked when accounts exist) | CICS `BNK1CDM` |
| Open account | `CREACC` (generates account number from `CONTROL`) | CICS `BNK1ACC` |
| View/list accounts | `INQACC`, `INQACCS`, `INQACCCU` | CICS `BNK1DCM` |
| Change account | `UPDACC` (type, interest rate, overdraft) | CICS `BNK1UAM` |
| Close account | `DELACC` (blocked if balance owed/proc. trans outstanding) | CICS `BNK1DAM` |
| Deposit/withdraw | `DBCRFUN` (writes `PROCTRAN`) | CICS `BNK1TFM` path |
| Transfer between accounts | `XFRFUN` (atomic debit+credit, rollback on failure) | CICS `BNK1B2M` |
| Transaction history | `INQTRAND`, `INQTRANL` | CICS |
| Branch info | `GETCOMPY`, `GETSCODE` | CICS |
| Monthly statement | `BNKSTMTC`/`BNKSTMTP` via JCL | Batch |
| Test data seeding | `BANKDATA` | Batch/ad-hoc |
| IMS channel | `IB*` transactions, `LOAD*` utilities | IMS |

## 3. Business capabilities

- Single-branch banking under one sort code; account numbers assigned sequentially from `CONTROL`.
- Credit-worthiness checks during onboarding via five sequential agency programs.
- Same-branch transfers only *(inferred: `XFRFUN` validates both accounts under the same sort code)*.
- Statement reporting of balances and processed transactions per customer.

## 4. Business rules (code-verified)

| # | Rule | Source |
|---|---|---|
| BR-1 | Sort code is fixed `987654`; all keys are (sortcode, number) pairs. | `SORTCODE.cpy`, `INQCUST`, `GETSCODE` |
| BR-2 | Customer number `9999999999` returns the most recently created customer; `0` picks a random existing customer. | `INQCUST` lines ~225–435 |
| BR-3 | New customers pass up to five credit-agency checks (`CRDTAGY1–5`) before insertion; failure sets COMM-SUCCESS/response and aborts. | `CRECUST`, `CRDTAGY*.cbl` |
| BR-4 | `XFRFUN` requires FROM and TO accounts under the same sort code, sufficient available balance, and writes a `PROCTRAN` record; on any failure it issues `EXEC CICS SYNCPOINT ROLLBACK`. | `XFRFUN` ~lines 394–460 |
| BR-5 | `DBCRFUN` supports DEBIT and CREDIT process types, updates available balance, writes `PROCTRAN` with timestamp; insufficient funds rejected. | `DBCRFUN` |
| BR-6 | A customer cannot be deleted while accounts remain (`DELCUS` checks dependent rows). | `DELCUS` |
| BR-7 | An account cannot be deleted if balance is non-zero / transactions exist *(inferred from delete-order checks)*. | `DELACC` |
| BR-8 | CICS abends route through `ABNDPROC` (`CVR1`+ codes) producing a failure commarea. | `ABNDPROC`, `ABNDINFO.cpy` |
| BR-9 | `CONTROL` table supplies branch sort code and next account/customer numbers. | `CONTROLI.cpy`, `ACCTCTRL.cpy`, `CREACC` |
| BR-10 | Dates displayed as `DD MM YYYY`; stored `YYYYMMDD` numeric. | `INQCUSTZ.cpy`, `CustomerResponse` mapping |
| BR-11 | IMS channel mirrors customer/account data access (`IBGCUDAT`, `IBSCUDAT`, `IBACSUM`, `IBTRAN`) with its own login (`IBLOGIN`/`IBLOGIN1`). | `ims/cobol`, `ims/pli` |
| BR-12 | Statement jobs iterate customers and emit account balance + processed-transaction sections. | `BNKSTMTC`, `BNKSTMTP`, `MonthlyStatementSpecification.md` |

## 5. Business constraints

- EBCDIC/packed-decimal data on mainframe; production data not available to this POC.
- 3270 fixed-position screens (24×80); all input through BMS fields.
- Sort code fixed to `987654` (single branch) — multi-branch is out of scope.
- No authentication on CICS REST endpoints in the sample; IMS channel has `IBLOGIN` only.

## 6. Expected modernization outcomes

- Same business rules executed by Java/Spring services over PostgreSQL.
- 3270 screens replaced by web UI (existing `src/frontend`), reconstructed for demo in the 3270 simulator.
- Batch statement exposed as an on-demand operation.
- Measured conversion + test + coverage reporting on the migration dashboard.
