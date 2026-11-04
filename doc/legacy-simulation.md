# Bank of Z — Legacy Application Simulation

Single consolidated document for the Phase 2 deliverable: a runnable 3270-style simulation of the Bank of Z mainframe application, reconstructed from the BMS screen definitions and COBOL program logic in `src/base`.

**Verification legend:** ✅ verified in source (BMS map / COBOL logic) · 🔶 inferred from code · ⚠️ illustrative (no original UI evidence — labeled as simulation).

All screens below are **simulations** — no original 3270 execution evidence existed in the repository; layouts and behavior are reconstructed from the BMS mapsets (`src/base/cics/bms`) and program source.

---

## Table of Contents

1. [How the Simulation Was Built](#1-how-the-simulation-was-built)
2. [Run Instructions](#2-run-instructions)
3. [Screen Inventory](#3-screen-inventory)
4. [Workflows](#4-workflows)
5. [Input Fields & Validation Rules](#5-input-fields--validation-rules)
6. [Expected Outputs](#6-expected-outputs)
7. [Sample Test Data](#7-sample-test-data)
8. [Reconstructed vs Illustrative Behavior](#8-reconstructed-vs-illustrative-behavior)
9. [Known Limitations](#9-known-limitations)

---

## 1. How the Simulation Was Built

- **Source:** 10 BMS mapsets in `src/base/cics/bms` (`.bms` files) — the authoritative 3270 screen definitions: field names, `POS=(row,col)`, lengths, `ATTRB` (PROT/UNPROT/NUM/ASKIP), `COLOR`, `HILIGHT`, `INITIAL` literals.
- **Parser:** `deliverables/02_legacy_simulation/parse-bms.py` joins assembler continuation lines, extracts `DFHMDF` field records, and emits `screens.json` — a field-level model of every map.
- **Renderer:** `deliverables/02_legacy_simulation/simulator/` (self-contained HTML/CSS/JS) draws each map on a 24×80 terminal grid, honoring protected vs input fields and BMS colors.
- **Logic:** `sim.js` implements menu routing, the in-memory datastore, validation, balance math and transaction records, mirroring the COBOL programs' behavior (see Section 5).

### Field counts (measured from screens.json)

| Mapset | Map | Fields | Purpose |
|---|---|---|---|
| BNK1MAI | Main menu | 16 | Menu of 8 options (1–7, A) |
| BNK1CCM | Create customer | 52 | Name/address/DOB entry |
| BNK1CAM | Create account | 20 | Account type, interest, overdraft |
| BNK1CDM | Credit/Debit | 18 | Account + sign + amount |
| BNK1DAM | Display account | 22 | Read-only account details |
| BNK1DCM | Display customer | 24 | Customer lookup + display |
| BNK1UAM | Update account | 20 | Edit account fields |
| BNK1TFM | Transfer | 20 | From/to account + amount |
| BNK1B2M | Transfer (variant) | 53 | Alternate transfer confirmation map |
| BNK1ACC | Accounts by customer | 14 | Account list |

## 2. Run Instructions

```
cd deliverables/02_legacy_simulation/simulator
python3 -m http.server 3222
# open http://localhost:3222/index.html
```

No backend or install needed — static files only.

**Keys:** **Enter** submits the screen · **F3 or Esc** returns to the main menu · menu accepts options **1–7** and **A**. Input fields appear as editable yellow-bordered cells; protected fields are display-only, matching BMS `ATTRB`.

**Regenerate the field model** (after changing any `.bms` file):

```
python3 deliverables/02_legacy_simulation/parse-bms.py > deliverables/02_legacy_simulation/screens.json
```

## 3. Screen Inventory

Verified-rendered screens (screenshots in `deliverables/02_legacy_simulation/`):

- **`screen-menu.png`** — BNK1MAI main menu: blue screen ID, red application title, cyan option text, yellow field prompts, `F3=Exit F12=Cancel` footer, message line at row 23.
- **`screen-customer.png`** — BNK1DCM: `CUSTOMER NUMBER` input plus displayed sort code 987654, name, address, DOB, credit score, review date; `CUSTOMER FOUND` message.
- **`screen-transfer.png`** — BNK1TFM: FROM/TO/AMOUNT inputs and post-transfer balances with `TRANSFERRED 100.00 FROM 00000002 TO 00000001`.

All 10 maps render; the three above were captured during an automated verification run (`simshot.py`: menu → option 1 → customer 1 → Esc → option 7 → transfer 100.00).

## 4. Workflows

| Option | Flow | Program(s) mirrored |
|---|---|---|
| 1 — Display customer | Menu → CUSTNO input → details + `CUSTOMER FOUND` | `INQCUST`, `BNK1DCM` |
| 2 — Display account | Menu → ACCNO input → balances/type/dates | `INQACC`, `BNK1DAM` |
| 3 — Create customer | Menu → enter name/address/DOB → credit check → assigned number | `CRECUST` + `CRDTAGY1–5`, `BNK1CCM` |
| 4 — Create account | Menu → CUSTNO + ACCTYP/INTRT/OVERDR → new account number | `CREACC`, `BNK1CAM` |
| 5 — Update account | Menu → ACCNO → edit type/interest/overdraft → confirm | `UPDACC`, `BNK1UAM` |
| 6 — Credit/Debit | Menu → ACCNO + `+`/`-` sign + amount → balance update | `DBCRFUN`, `BNK1CDM` |
| 7 — Transfer | Menu → FROM/TO/AMOUNT → atomic move or INSUFFICIENT FUNDS | `XFRFUN`, `BNK1TFM`/`BNK1B2M` |
| A — Accounts by customer | Menu → CUSTNO → account list | `INQACCS`/`INQACCCU`, `BNK1ACC` |

## 5. Input Fields & Validation Rules

BMS field semantics: `UNPROT` = user-input field; `PROT`/`ASKIP` = display-only. `NUM` fields accept digits only. All key fields are fixed-width per the map lengths.

| Rule | Source | Simulated behavior |
|---|---|---|
| Fixed sort code `987654` | COBOL BR-1 | All accounts carry sort code 987654; shown on detail screens |
| Customer number `9999999999` = last customer; `0` = random | `INQCUST` | Same special values honored |
| Required fields | BMS `UNPROT` + program checks | Missing customer/account numbers and amount = error message on row 23 |
| Not found | `INQCUST`/`INQACC` not-found path | `CUSTOMER NOT FOUND` / account not-found message |
| Sufficient available balance | `DBCRFUN`/`XFRFUN` | Debit/transfer larger than available balance → `INSUFFICIENT FUNDS`, no balance change |
| Transfer atomicity | `XFRFUN` SYNCPOINT/ROLLBACK | Both balances update together or neither; message confirms or rejects |
| Transaction record | `PROCTRAN` write | Successful debit/credit/transfer appends a numbered transaction |
| Credit-agency checks | `CRDTAGY1–5` | Simulated pass; real external agency call cannot be reproduced (⚠️) |
| Date format | `YYMMDD`/`DD MM YYYY` commareas | Displayed `DD / MM / YYYY`; simulation day 04/11/2026 |
| Menu routing | `BNKMENU` | Options 1–7, A valid; anything else → `INVALID OPTION - ENTER 1-7 OR A` |

## 6. Expected Outputs

- **Display customer (1):** full customer record — sort code, customer number, title, name, three address lines, city, postcode, country, DOB, credit score, review date.
- **Display account (2):** account number, customer number, type, interest rate, overdraft, opened/last-statement dates, actual and available balances.
- **Credit/Debit (6):** updated balances echoed on screen; PROCTRAN entry recorded.
- **Transfer (7):** both accounts' actual + available balances shown after the move; `TRANSFERRED <amt> FROM <a> TO <b>` confirmation.
- **Accounts by customer (A):** list of account numbers + types for the customer.
- **Errors:** yellow message line (row 23) for invalid option, not found, insufficient funds, missing input.

## 7. Sample Test Data

Same demo data as the modern Spring Boot app (`--spring.profiles.active=demo`). All fictional.

### Customers (sort code 987654)

| Number | Name | DOB | Address | Credit |
|---|---|---|---|---|
| 0000000001 | Mr John P Smith | 01/05/1980 | 14 Hill Street, LONDON EC1 4AB, UK | 720 |
| 0000000002 | Mrs Mary A Jones | 22/03/1975 | 5 Bank Lane, LEEDS LS1 2CD, UK | 650 |

### Accounts

| Number | Customer | Type | Interest | Overdraft | Actual | Available |
|---|---|---|---|---|---|---|
| 00000001 | 1 | ISA | 1.50% | 0 | 1,250.00 | 1,250.00 |
| 00000002 | 1 | CURRENT | 0.25% | 500 | 2,310.55 | 2,310.55 |
| 00000003 | 2 | MORTGAGE | 4.10% | 0 | −150,000.00 | −150,000.00 |

### Transactions

| Number | Account | Type | Date | Amount | Description |
|---|---|---|---|---|---|
| 0000000001 | 00000002 | CREDIT | 01/11/2026 | 500.00 | Salary |
| 0000000002 | 00000002 | DEBIT | 01/11/2026 | 89.45 | Utilities |
| 0000000003 | 00000001 | CREDIT | 01/11/2026 | 1,000.00 | Deposit |

## 8. Reconstructed vs Illustrative Behavior

**Reconstructed from source (✅/🔶):**

- Screen layouts, field names, captions, colors, positions — direct from BMS maps.
- Menu options and routing — from `BNKMENU`/BNK1* programs.
- Sort code 987654, last/random customer values, available-balance checks, transaction records, F3/F12 keys — from COBOL logic.
- `INVALID OPTION`, `INSUFFICIENT FUNDS`, not-found messages — from program message text.

**Illustrative (⚠️ labeled):**

- Sample data values (no production/customer data in the repo).
- Credit-agency check always passes — the real `CRDTAGY1–5` external call cannot be reproduced.
- Enter-key submission flow — real maps use CICS pseudo-conversational LINKs; key handling is simulated.
- BNK1B2M alternate confirmation map is folded into the single transfer flow.
- Simulation date 04/11/2026 stands in for run-date fields.

## 9. Known Limitations

- No real CICS transaction routing or commarea LINK semantics — screens are invoked via menu routing in JS.
- No full PF-key terminal emulation (only F3/Esc and Enter).
- Session state resets on page reload; no persistence between runs.
- IMS transaction screens (`IB*` programs) are not covered — the simulator reproduces the CICS BMS layer only.
- Screen fidelity is field-accurate, not pixel-identical to a real 3270 emulator.

## Files

| Path | Contents |
|---|---|
| `deliverables/02_legacy_simulation/simulator/` | `index.html`, `sim.css`, `sim.js`, `screens.js` — self-contained app |
| `deliverables/02_legacy_simulation/parse-bms.py` | BMS → `screens.json` parser |
| `deliverables/02_legacy_simulation/screens.json` | Parsed field model of all 10 maps |
| `deliverables/02_legacy_simulation/screen-*.png` | Captured simulation screens |
| `deliverables/02_legacy_simulation/README.md` | Run instructions |
