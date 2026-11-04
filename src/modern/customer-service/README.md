# customer-service: INQCUST converted to Java

Java 21 / Spring Boot 3 / PostgreSQL conversion of the CICS COBOL program
`src/base/cics/cobol/INQCUST.cbl` (customer inquiry, DB2 table `BANKZ.CUSTOMER`).

## Run

```bash
docker compose up -d                                  # PostgreSQL 16 on port 5432
./mvnw spring-boot:run -Dspring-boot.run.profiles=demo  # demo profile loads 3 sample customers
curl http://localhost:8080/api/customers/1
```

Use `PORT=8085 ./mvnw spring-boot:run ...` if port 8080 is taken. Database settings come from
`DB_URL`, `DB_USER` and `DB_PASSWORD` (defaults: `jdbc:postgresql://localhost:5432/bankz`, `bankz`, `bankz`).

Run the tests with `./mvnw test`. They use an in-memory H2 database in PostgreSQL mode, so Docker is not needed.

## COBOL to Java mapping

| COBOL / z/OS | Java |
|---|---|
| `INQCUST` procedure division | `service/CustomerInquiryService` |
| DB2 `CUSTOMER` table (`CUSTDB2.cpy`) | `domain/Customer`, Flyway `V1__create_customer.sql` |
| Unique index `CUSTINDX` (sort code, number) | `domain/CustomerKey` (primary key) |
| `GET-LAST-CUSTOMER-DB2` | `CustomerRepository.findFirstByKeySortCodeOrderByKeyNumberDesc` |
| Commarea `INQCUSTZ.cpy` + z/OS Connect `GET /customers/{customerId}` | `web/CustomerController`, `web/CustomerResponse` (same JSON) |
| `SORTCODE.cpy` (987654) | `bankz.sort-code` in `application.yml` |
| `INQCUST-INQ-SUCCESS = 'N'`, fail code 1 or 9 | HTTP 404 with `failCode` |
| SQLCODE other than 0/100, abend `CVR1` | HTTP 500 with `abendCode: CVR1` |

Behavior kept from the COBOL program:

- Customer number `9999999999` returns the customer with the highest number; if that customer was deleted meanwhile, it retries once.
- Customer number `0` returns a random customer using the COBOL formula `((last - 1) * RANDOM) + 1`, retrying up to 1000 times.
- `yyyymmdd` integer dates become `YYYY-MM-DD` using the same divide/modulo arithmetic; a null date becomes `0000-00-00`.

## Differences from the mainframe version

- CICS `SYNCPOINT ROLLBACK`, storm-drain checks and the `ABNDPROC` abend record are replaced by a read-only Spring transaction, an HTTP 500 and an error log.
- If the last customer is still missing after the retry, COBOL keeps looping; Java returns 404.
- Non-numeric or longer than 10-digit IDs return HTTP 400 instead of a z/OS Connect conversion error.
- Credit score and review date are stored but, as in the z/OS Connect API, not returned.
