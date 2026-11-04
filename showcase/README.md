# Bank of Z — Modernization Showcase

One folder with everything done by Devin AI on this project.

## Open first

- **`final-results.docx`** — the consolidated results doc (all steps, conversion numbers, charts, tests, gaps)

## Charts (measured, in `charts/`)

- `pie_conversion.png` — how all 33,624 legacy lines were handled
- `bar_waves.png` — legacy vs converted lines per wave
- `line_progress.png` — cumulative conversion "performance" line
- `kpis.png` — tests, coverage, eligible conversion %

## Screenshots

- `screen-menu.png`, `screen-customer.png`, `screen-transfer.png` — the BMS-derived legacy simulator
- `dashboard-full.png` — the migration dashboard

## Dashboard (live)

Run the app, then open `http://localhost:3001/migration-dashboard.html`:
the "Modernization journey" table (all steps), the "Mainframe source → Java" table, the per-file map, and all charts.

## Everything else (in the repo)

- `doc/` — all detailed docs (reverse-engineering, simulation, modernized app, comparison, video structure)
- `deliverables/` — the 7-phase deliverables tree
- `src/modern/bankz-app` — the converted Java/Spring Boot app
- `conversion/` — input (legacy) and output (Java) copies + file map
- `scripts/` — migration-inventory, conversion-report, demo prep/record
