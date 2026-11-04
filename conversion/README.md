# Conversion: legacy input → Java/Spring Boot output

- `input/` — copies of the legacy mainframe files used as conversion input (COBOL, PL/I, JCL, copybooks, DB2 DDL, IMS DBD/PSB/LoadData, z/OS Connect OpenAPI contract).
- `output/` — copy of the converted Spring Boot 3 / Java 21 / PostgreSQL application (`src/modern/bankz-app`). Build and run instructions: `output/README.md`.

These are copies. The working sources stay in `src/base` (legacy) and `src/modern/bankz-app` (Java).

## Mapping

Java paths are relative to `output/src/main/java/com/bankz/`.

| Input | Output |
|---|---|
| `input/cics/cobol/INQCUST.cbl` | `customer/service/CustomerInquiryService.java` |
| `input/cics/cobol/CRECUST.cbl` | `customer/service/CustomerCreateService.java` |
| `input/cics/cobol/CRDTAGY1-5.cbl` | `customer/service/CreditAgencyService.java` |
| `input/cics/cobol/UPDCUST.cbl` | `customer/service/CustomerUpdateService.java` |
| `input/cics/cobol/DELCUS.cbl` | `customer/service/CustomerDeleteService.java` |
| `input/cics/cobol/CREACC, INQACC, INQACCS, INQACCCU, UPDACC, DELACC.cbl` | `account/service/AccountService.java` |
| `input/cics/cobol/DBCRFUN.cbl, XFRFUN.cbl` | `payment/service/PaymentService.java` |
| `input/cics/cobol/INQTRAND.cbl, INQTRANL.cbl` | `payment/service/TransactionInquiryService.java` |
| `input/cics/cobol/GETCOMPY.cbl, GETSCODE.cbl` | `payment/service/CompanyService.java` |
| `input/cics/cobol/ABNDPROC.cbl` | `common/AbendService.java` |
| `input/cics/cobol/BANKDATA.cbl` | `batch/BankDataGenerator.java` |
| `input/cics/copy/*.cpy` | domain classes (`customer/domain`, `account/domain`, `common/Proctran.java`, `common/ControlRecord.java`) and REST DTOs |
| `input/batch/cobol/BNKSTMTC.cbl`, `input/batch/pli/BNKSTMTP.pli` | `batch/StatementService.java` |
| `input/batch/jcl/BNKSTMTC.jcl, BNKSTMTP.jcl` | `batch/StatementJobRunner.java` |
| `input/ims/cobol/IBGCUDAT, IBSCUDAT, IBLOGIN1, IBLOGOUT, IBACSUM, IBTRAN.cbl`, `input/ims/pli/IBLOGIN.pli` | `ims/ImsService.java`, `ims/ImsController.java` |
| `input/ims/cobol/LOADCUST, LOADACCT, LOADCUSA, LOADHIST, LOADTSTA.cbl` | `ims/ImsDataLoader.java` |
| `input/ims/LoadData/*.data` | `output/src/main/resources/ims-data/*.data` |
| `input/db2/cics-Db2-create.j2` | `output/src/main/resources/db/migration/V1__create_customer.sql`, `V2__create_account_proctran_control.sql` |
| `input/ims/DBD/*.asm`, `input/ims/PSB/*.asm` | `output/src/main/resources/db/migration/V3__create_ims_tables.sql` |
| `input/api/openapi.yaml` | `customer/web`, `account/web`, `payment/web` controllers |

Tests: `output/src/test/java` (36 tests, all passing).

## Not included

The 3270 screen programs (`src/base/cics/cobol/BNK1*.cbl`, `BNKMENU.cbl`, `src/base/cics/bms/*.bms`, and the map copybooks `BANKMAP.cpy`, `CUSTMAP.cpy`, `BNK1DDM.cpy`) were not converted to Java. The web frontend in `src/frontend` replaces them and calls the Java API.
