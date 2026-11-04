# Sample test data (simulation)

Same demo data as the modern app (`--spring.profiles.active=demo`). All fictional.

## Customers (sort code 987654)

| Number | Name | DOB | Address | Credit |
|---|---|---|---|---|
| 0000000001 | Mr John P Smith | 01/05/1980 | 14 Hill Street, LONDON EC1 4AB, UK | 720 |
| 0000000002 | Mrs Mary A Jones | 22/03/1975 | 5 Bank Lane, LEEDS LS1 2CD, UK | 650 |

## Accounts

| Number | Customer | Type | Interest | Overdraft | Actual | Available |
|---|---|---|---|---|---|---|
| 00000001 | 1 | ISA | 1.50% | 0 | 1,250.00 | 1,250.00 |
| 00000002 | 1 | CURRENT | 0.25% | 500 | 2,310.55 | 2,310.55 |
| 00000003 | 2 | MORTGAGE | 4.10% | 0 | −150,000.00 | −150,000.00 |

## Transactions

| Number | Account | Type | Date | Amount | Description |
|---|---|---|---|---|---|
| 0000000001 | 00000002 | CREDIT | 01/11/2026 | 500.00 | Salary |
| 0000000002 | 00000002 | DEBIT | 01/11/2026 | 89.45 | Utilities |
| 0000000003 | 00000001 | CREDIT | 01/11/2026 | 1,000.00 | Deposit |
