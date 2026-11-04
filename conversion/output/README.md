# bankz-app — Bank-of-Z mainframe workloads in Java 21 / Spring Boot 3 / PostgreSQL

One Spring Boot application replacing the CICS, IMS and batch programs in `src/base`.
REST paths mirror the z/OS Connect API (`src/api/src/main/api/openapi.yaml`) under `/api`.

## Run

```bash
docker run -d --name bankz-pg -e POSTGRES_DB=bankz -e POSTGRES_USER=bankz -e POSTGRES_PASSWORD=bankz -p 5432:5432 postgres:16
./mvnw spring-boot:run -Dspring-boot.run.profiles=demo -Dspring-boot.run.arguments=--server.port=8085
./mvnw test        # H2 in PostgreSQL mode, no database needed
```

Batch jobs (JCL replacements):

```bash
java -jar target/bankz-app-*.jar --spring.main.web-application-type=none --bankz.batch.job=BNKSTMTC --bankz.batch.sysin="C000000001 2026-10"
java -jar target/bankz-app-*.jar --spring.main.web-application-type=none --bankz.batch.job=BNKSTMTP --bankz.batch.sortcode=987654 --bankz.batch.datecard=202610
```

## Legacy program → Java

| Legacy | Java | REST |
|---|---|---|
| INQCUST | `customer.service.CustomerInquiryService` | `GET /api/customers/{id}` |
| CRECUST, CRDTAGY1-5 | `CustomerCreateService`, `CreditAgencyService` (5 parallel calls, 3 s timeout) | `POST /api/customers` |
| UPDCUST | `CustomerUpdateService` | `PUT /api/customers/{id}` |
| DELCUS | `CustomerDeleteService` | `DELETE /api/customers/{id}` |
| INQACCCU | `account.service.AccountService.listForCustomer` | `GET /api/customers/{id}/accounts` |
| CREACC, INQACC, INQACCS, UPDACC, DELACC | `AccountService` | `/api/accounts[/{id}]`, `/balances` |
| DBCRFUN | `payment.service.PaymentService.debitCredit` | `POST /api/accounts/{id}/deposit` |
| XFRFUN | `PaymentService.transfer` | `POST /api/accounts/{id}/transfer` |
| INQTRANL, INQTRAND | `TransactionInquiryService` | `GET /api/accounts/{id}/transactions[/{txId}]` |
| GETCOMPY, GETSCODE | `CompanyService` | `GET /api/company` |
| ABNDPROC (ABNDFILE VSAM) | `common.AbendService` → `abend_record` table | — |
| BNKSTMTC.cbl + BNKSTMTC.jcl | `batch.StatementService.customerStatement`, `StatementJobRunner` | `GET /api/batch/statements` |
| BNKSTMTP.pli + BNKSTMTP.jcl | `StatementService.sortCodeStatements` | `GET /api/batch/statements/sortcode` |
| BANKDATA | `batch.BankDataGenerator` | `POST /api/batch/bankdata` |
| IBGCUDAT, IBSCUDAT | `ims.ImsService` | `GET/PUT /api/ims/customers/{id}` |
| IBLOGIN1, IBLOGIN.pli, IBLOGOUT | `ImsService.login/logout` | `POST /api/ims/login`, `/logout` |
| IBACSUM | `ImsService.accountSummary` | `GET /api/ims/customers/{id}/accounts` |
| IBTRAN | `ImsService.transaction` | `POST /api/ims/accounts/{id}/transactions` |
| LOADCUST, LOADACCT, LOADCUSA, LOADHIST, LOADTSTA | `ims.ImsDataLoader` (IMS LoadData → `ims_*` tables) | startup |
| DB2 BANKZ.* tables, IMS DBDs | Flyway `V1`–`V3` | — |
| BNK1* / BNKMENU 3270 screens (BMS maps) | Served by the existing `src/frontend` pages calling these endpoints | — |

## Known differences from the mainframe

- Credit agencies (CRDTAGY1-5) run as in-process virtual threads instead of CICS async transactions.
- BNKSTMTP's February end date is the real month length (the PL/I always used 28).
- BANKDATA uses its own name lists and a seeded `java.util.Random`, so generated data differs from the COBOL run.
- IMS segments are relational tables; the IBTRAN JNI history call is a direct insert.
- The BMS/3270 programs were not translated line by line: their screens are covered by the web frontend.
