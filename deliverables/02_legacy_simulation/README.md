# Phase 2 — Legacy Mainframe Application Simulation

A browser-based 3270-style terminal simulation reconstructed **from the BMS mapsets** in `src/base/cics/bms` (not invented — screen layout, fields, colors and captions come from the maps).

## Run it

```
cd deliverables/02_legacy_simulation/simulator
python3 -m http.server 3222
# open http://localhost:3222/index.html
```

No backend needed. Keys: **Enter** submit · **F3/Esc** main menu · menu options **1–7, A**.

## What is simulated

| Menu option | Screen (BMS map) | Behavior simulated |
|---|---|---|
| 1 Display customer | BNK1DCM | Enter customer number → customer details from sample data; not-found error |
| 2 Display account | BNK1DAM | Account number → balances, type, dates |
| 3 Create customer | BNK1CCM | Name/address/DOB entry → credit-check pass + sequential number |
| 4 Create account | BNK1CAM | Customer number + type/interest/overdraft → new account number |
| 5 Update account | BNK1UAM | Load account → edit type/interest/overdraft |
| 6 Credit/Debit | BNK1CDM | Account, +/- amount → balance update; insufficient-funds rejection |
| 7 Transfer | BNK1TFM | From/To + amount → atomic move; insufficient-funds rollback message |
| A Accounts by customer | BNK1ACC | Customer number → account list |

Rules mirrored from the COBOL: fixed sort code `987654`, numeric key fields, available-balance check before debit/transfer, transaction record on success.

## Files

- `simulator/` — self-contained app (`index.html`, `sim.css`, `sim.js`, `screens.js`)
- `parse-bms.py` — regenerates `screens.json` from `src/base/cics/bms`
- `screens.json` — parsed field model (name, position, length, initial text, ATTRB, COLOR)
- `screen-*.png` — captured simulation screens (menu, customer display, transfer)
- `sample-data.md` — the simulated customers/accounts/transactions

## Reconstructed vs illustrative

- **Reconstructed from code:** screen layouts and captions (BMS), fixed sort code, available-balance logic, PROCTRAN-style transaction records, F3/F12 keys, menu options.
- **Illustrative (labeled):** sample data values; the "enter key then Enter" flow on screens whose real map used a preceding dialog; credit-check is a simulated pass (the real `CRDTAGY1–5` logic is external); dates use simulation day 04/11/2026.

## Known limitations

- No real CICS transaction routing — screens map menu-option → map; commarea/LINK flow is simulated in JS.
- BNK1B2M (alternate transfer confirmation map) folds into the transfer flow.
- No PF-key terminal emulation beyond F3/Esc.
- Session state resets on reload; no persistence.
