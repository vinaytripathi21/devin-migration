# Bank of Z — Recommended Demo Video Structure

Single consolidated document for the video deliverable. Principle: **record the actual Devin interactions and intermediate outputs, not only the final screens.** Produce a short edited cut for leadership (≈8–10 min) and keep the full unedited recording available for technical review.

---

## Table of Contents

1. [Two Deliverables](#1-two-deliverables)
2. [Scene-by-Scene Structure (Leadership Cut)](#2-scene-by-scene-structure-leadership-cut)
3. [What to Capture — Devin Interactions](#3-what-to-capture--devin-interactions)
4. [Recording Checklist](#4-recording-checklist)
5. [Editing & Assembly](#5-editing--assembly)
6. [Assets Already Available](#6-assets-already-available)

---

## 1. Two Deliverables

| Cut | Audience | Length | Contents |
|---|---|---|---|
| **Leadership cut** (edited) | Executives / steering | 8–10 min | Narrated scenes 1–7 below, with key Devin exchanges shown |
| **Full recording** (unedited) | Architects / reviewers | As long as needed | Complete session capture: prompts, scans, code diffs, builds, test runs, errors and fixes |

## 2. Scene-by-Scene Structure (Leadership Cut)

| # | Scene | Record | Show on screen | Narration point |
|---|---|---|---|---|
| 0 | Cold open (15s) | Split frame: legacy sim menu vs modern web app | `screen-menu.png` left, web app right | "Same bank, two eras — one POC migrated it." |
| 1 | The legacy estate (45s) | Devin scanning `src/base`; inventory JSON produced | Devin session running `migration-inventory.py`; counts appearing | "Devin analyzed all 132 files — COBOL, PL/I, JCL, IMS, DB2, BMS — 33,624 lines." |
| 2 | Requirements from code (45s) | Devin deriving BRD/FRD + traceability; opening `doc/reverse-engineering.md` | BR table citing `INQCUST`, `XFRFUN` source lines | "Requirements weren't invented — each rule cites its source line." |
| 3 | Legacy simulation (60s) | Terminal interaction: menu → option 1 → customer 1; option 7 → transfer | Live typing in the 3270 simulator, `CUSTOMER FOUND`, `TRANSFERRED` | "A working 3270 rebuilt from the actual BMS maps — our before-picture." |
| 4 | The migration itself (90s) — **key scene** | Devin converting waves: COBOL next to generated Java; `mvn test` running; progress in session | `INQCUST.cbl` beside `CustomerInquiryService.java`; test counts ticking up | "Devin converted 99 files — 95% of convertible lines — preserving commarea semantics." |
| 5 | Validation (60s) | Live scenario run: same customer + transfer on API; dashboard report | Curl output side-by-side with sim screenshot; dashboard KPIs | "Identical scenarios, measured — 36/36 tests, 88.5% coverage, 4.6 ms median." |
| 6 | Honest gaps (30s) | Devin's comparison doc listing G-1 defect + open items | Gap table incl. overdraft check defect found by testing | "Testing found a real defect — evidence over claims." |
| 7 | Close (30s) | `deliverables/` tree + dashboard | Repo tree, dashboard bottom "Still to do" | "Everything reproducible: scripts, docs, and the converted code." |

## 3. What to Capture — Devin Interactions

Record these interactions verbatim (screen-record the Devin session):

- The prompt given to Devin ("convert all code in Java" → wave plan) and Devin's plan response.
- The inventory scan producing real numbers (132 files / 33,624 lines).
- A conversion diff: COBOL source → Java service being written; tests appearing.
- `mvn test` running to 36/36 with JaCoCo coverage.
- The BMS parser generating `screens.json`, then the simulator rendering a screen.
- The live comparison run: customer lookup + transfer + the insufficient-funds scenario that exposed gap G-1.
- Devin generating the `deliverables/` docs and the dashboard regeneration.

## 4. Recording Checklist

- [ ] Fresh session or scrollback: Devin session URL open (`app.devin.ai/sessions/…`)
- [ ] Simulator running: `cd deliverables/02_legacy_simulation/simulator && python3 -m http.server 3222`
- [ ] Modern stack up: `docker start bankz-postgres`; bankz-app on `:8085`; frontend on `:3001`
- [ ] Dashboard: `http://localhost:3001/migration-dashboard.html`
- [ ] Terminal ready for: `mvn test`, curl scenario calls, `python3 scripts/conversion-report.py`
- [ ] 1080p capture, hide notifications, zoom terminal text
- [ ] Keep raw recording file for the technical-review cut

## 5. Editing & Assembly

1. Record each scene separately; keep raw files.
2. Leadership cut: narrate over scenes 0–7 (use captions if no VO); insert `screen-*.png` and dashboard screenshots as stills where recording is thin.
3. Full cut: concatenate raw captures, add chapter markers at each phase.
4. Export `demo-leadership.mp4` and `demo-full.mp4` into `deliverables/06_demo/`.
5. **Honesty rule:** label the simulator as a simulation; never present a scripted clip as a live mainframe run.

## 6. Assets Already Available

- Legacy sim screenshots: `deliverables/02_legacy_simulation/screen-*.png`
- Modern UI + dashboard screenshots: `~/Downloads/bank-of-z-screens.zip`, `~/Desktop/Bank-of-Z` dashboard page
- Verified curl outputs: `doc/comparison-validation.md` Section 3 (usable as terminal-recording script)
- Narration base: `deliverables/06_demo/storyboard-and-script.md`
