# Bank of Z — Modernization POC Deliverables

| Folder | Contents |
|---|---|
| `01_reverse_engineering/` | Assessment, BRD, FRD, agile backlog, technical docs, test cases, traceability matrix |
| `02_legacy_simulation/` | BMS-derived 3270 simulator + screenshots + sample data — run: `cd simulator && python3 -m http.server 3222` |
| `03_modernized_application/` | Architecture, build/run, migration mapping, execution log (app itself at `src/modern/bankz-app`) |
| `04_comparison/` | Legacy-vs-modern matrix and scenario evidence |
| `05_quality_assurance/` | Test/coverage/performance report from real artifacts |
| `06_demo/` | Demo storyboard + narration script + assembly steps |
| `07_executive_report/` | POC executive report |

## Run the legacy simulation

```
cd deliverables/02_legacy_simulation/simulator && python3 -m http.server 3222
# http://localhost:3222
```

## Run the modern application

```
docker run -d --name bankz-postgres -e POSTGRES_DB=bankzapp -e POSTGRES_USER=bankz -e POSTGRES_PASSWORD=bankz -p 5432:5432 postgres:16   # or: docker start bankz-postgres
DB_URL=jdbc:postgresql://localhost:5432/bankzapp java -jar src/modern/bankz-app/target/bankz-app-0.1.0.jar \
  --spring.profiles.active=demo --server.port=8085
python3 -m http.server 3001 --directory src/frontend
# http://localhost:3001/?backend=spring   ·   dashboard: /migration-dashboard.html   ·   API: :8085/api
```

Tests: `cd src/modern/bankz-app && mvn test`. Regenerate dashboard data: `python3 scripts/migration-inventory.py && python3 scripts/conversion-report.py`.

Legacy sources in `src/base` were not modified.
