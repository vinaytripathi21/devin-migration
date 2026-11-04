# Functional Requirements Document (FRD)

Derived from `src/base` program code and `src/api/src/main/api/openapi.yaml`. Requirement IDs used by the backlog and traceability matrix.

## FR-CUS — Customer management

| ID | Requirement | Legacy source |
|---|---|---|
| FR-CUS-01 | Create a customer: name (title, first, last), DOB, house name/number, postcode; run credit checks; assign next customer number from `CONTROL`. | `CRECUST`, `BNK1CAM` |
| FR-CUS-02 | Reject creation when any of `CRDTAGY1–5` fails; return the failing agency reason. | `CRECUST`, `CRDTAGY*` |
| FR-CUS-03 | Inquire a customer by number; `9999999999` = last created; `0` = random. | `INQCUST` |
| FR-CUS-04 | Return not-found (`CVR1`-style failure) for unknown numbers. | `INQCUST`, `ABNDPROC` |
| FR-CUS-05 | Update name/address/DOB fields of an existing customer. | `UPDCUST` |
| FR-CUS-06 | Delete a customer only when no accounts remain. | `DELCUS` |
| FR-CUS-07 | List a customer's accounts. | `INQACCCU` |

## FR-ACC — Account management

| ID | Requirement | Legacy source |
|---|---|---|
| FR-ACC-01 | Create account for existing customer: type, interest rate, overdraft limit, opening balance; number from `CONTROL`. | `CREACC` |
| FR-ACC-02 | Inquire single account by sortcode+number. | `INQACC` |
| FR-ACC-03 | List all accounts. | `INQACCS` |
| FR-ACC-04 | Update type/interest/overdraft of an account. | `UPDACC` |
| FR-ACC-05 | Delete an account (subject to balance/transaction checks). | `DELACC` |

## FR-PAY — Payments and transactions

| ID | Requirement | Legacy source |
|---|---|---|
| FR-PAY-01 | Debit or credit an account by amount; adjust available and actual balance; write a `PROCTRAN` record. | `DBCRFUN` |
| FR-PAY-02 | Reject a debit that would breach available balance. | `DBCRFUN` |
| FR-PAY-03 | Transfer amount between two accounts in one unit of work; on failure roll back both sides. | `XFRFUN` |
| FR-PAY-04 | Reject transfer when FROM account lacks available funds. | `XFRFUN` |
| FR-PAY-05 | Inquire transactions for an account, newest first; and full-detail view of a transaction. | `INQTRANL`, `INQTRAND` |

## FR-INF — Reference data

| ID | Requirement | Legacy source |
|---|---|---|
| FR-INF-01 | Return company name and sort code. | `GETCOMPY` |
| FR-INF-02 | Return the branch sort code. | `GETSCODE` |
| FR-INF-03 | Route failures through the abend program returning code + description. | `ABNDPROC` |

## FR-BAT — Batch

| ID | Requirement | Legacy source |
|---|---|---|
| FR-BAT-01 | Generate monthly statement per customer: header, accounts with balances, processed transactions. | `BNKSTMTC`/`BNKSTMTP`, JCL |
| FR-BAT-02 | Generate/seed test customers, accounts, transactions. | `BANKDATA` |

## FR-IMS — IMS channel

| ID | Requirement | Legacy source |
|---|---|---|
| FR-IMS-01 | IMS login/logout. | `IBLOGIN(.pli)`, `IBLOGIN1`, `IBLOGOUT` |
| FR-IMS-02 | Retrieve customer details and accounts (`IBGCUDAT` get, `IBSCUDAT` update). | IMS COBOL |
| FR-IMS-03 | Account summary and transaction history. | `IBACSUM`, `IBTRAN` |
| FR-IMS-04 | Load utilities populate IMS segments from `LoadData/*.data`. | `LOAD*` |

## Input/output specifications

- Keys: `sortCode` 6-digit (`987654`), `customerNumber` 10-digit, `accountNumber` 8-digit.
- Amounts: `PIC S9(10)V99` → decimal(12,2); dates `YYYYMMDD` → ISO date in API.
- Commarea responses map to `response_200.yaml` JSON in `openapi.yaml` (fields: `INQCUST_*`, `COMM_*`).

## Validation rules (code-verified)

- Customer number numeric, within range; special values 0 / 9999999999 handled (FR-CUS-03).
- Account must exist for debit/credit/transfer; transfer FROM≠TO.
- Credit-agency sequence aborts on first failure.
- Delete operations check dependents first.
- IMS login required before IMS data access.

## Exception handling

- CICS failures → `ABNDPROC` with `ABND-CODE` + message; commarea `COMM-SUCCESS='N'`, `COMM-FAIL-CODE`.
- SQL errors checked after each `EXEC SQL` (`SQLCODE`/`WHENEVER`).
- Transfer failure → `SYNCPOINT ROLLBACK`.

## Integration requirements

- DB2 via `EXEC SQL` (target: JPA/PostgreSQL).
- IMS DL/I calls via `CBLTDLI`/`AIBTDLI` (target: relational tables V3 migration).
- MQ for Bank-of-Q transfers — out of scope, not converted.
- z/OS Connect REST layer per `openapi.yaml` (target: Spring `@RestController`).
