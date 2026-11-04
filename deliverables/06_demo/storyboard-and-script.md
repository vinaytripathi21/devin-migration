# Phase 6 — Demo Storyboard & Narration Script

A recording could not be produced in this session as a finished video; this is the complete scene-by-scene storyboard with narration and exact steps to record. Target length ~8–10 min.

| # | Scene | Show (what to record) | Narration |
|---|---|---|---|
| 1 | Introduction (45s) | Repo tree: `src/base`, `src/api`, `src/frontend` | "Bank of Z is a CICS banking sample: COBOL, PL/I, JCL, IMS, DB2, BMS — 132 files, 33,624 lines. Goal: modernize to Java, Spring Boot, PostgreSQL and React, with evidence." |
| 2 | Reverse engineering (60s) | Open `INQCUST.cbl`, `XFRFUN.cbl`; then `deliverables/01_reverse_engineering/` (BRD, FRD, backlog, traceability) | "Devin scanned every file, extracted business rules — fixed sort code, credit-agency chain, atomic transfers — and produced traceable requirements." |
| 3 | Legacy simulation (90s) | `deliverables/02_legacy_simulation/simulator` — menu → option 1 → customer 1 → option 7 → transfer | "This is how the app looked on a 3270 — reconstructed from the actual BMS maps, same fields and captions, same balance rules." |
| 4 | Migration (90s) | `conversion/README.md` mapping; open `CustomerInquiryService.java` next to `INQCUST.cbl`; `mvn test` in bankz-app | "Each COBOL program became a Spring service in one app — 99 files converted, 61.5% of lines; screens replaced by the web UI." |
| 5 | Modern app (90s) | `?backend=spring`: home, find customer 1, accounts, transfer; `GET /api/customers/1` JSON | "Same workflows, modern UX, REST API matching the z/OS Connect contract." |
| 6 | Before/after (60s) | Split screen: `screen-customer.png` vs modern Find-customer; `screen-transfer.png` vs web transfer | "Same data, same rules — commarea semantics like 'last customer' and rollback are preserved and tested." |
| 7 | Quality (60s) | `mvn test` run; dashboard KPIs + converted file report + coverage + performance tables | "36 of 36 tests pass, 88.5% line coverage, ~4.6 ms median response — all measured." |
| 8 | Summary (45s) | Dashboard "Still to do" + `deliverables/` tree | "What's left: mainframe parity run, perf baseline, IMS Java/MQ clients, production data — all listed openly. Migration evidence is reproducible via `scripts/conversion-report.py`." |

## Assembly instructions

1. Record screen 1–7 per the "Show" column (QuickTime/OBS; 1080p).
2. Voice-over from the narration column (or captions).
3. Insert screenshots already captured: `deliverables/02_legacy_simulation/screen-*.png`, `~/Downloads/bank-of-z-screens.zip`, `dashboard-full.png`.
4. Export MP4, place in `deliverables/06_demo/demo.mp4`.

## Presentation outline (slides)

1. Title: Mainframe Modernization POC — Bank of Z
2. Legacy estate (132 files, languages, sizes)
3. Approach: 7-phase lifecycle
4. Legacy 3270 simulation
5. Modern architecture (one Spring Boot app + React + PostgreSQL)
6. Evidence: conversion %, tests, coverage, timings
7. Comparison matrix
8. Limitations & open items
9. Recommendations & next steps
