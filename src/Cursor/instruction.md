# Bank of Z — `src/` Instructions

Guide to the application source under `src/` and how to run the browser UI from `src/frontend`.

## What's in `src/`

| Path | Role |
|------|------|
| `src/frontend/` | Static HTML / CSS / JS UI (Carbon Web Components). No build step. |
| `src/api/` | z/OS Connect API project (Gradle / OpenAPI assets). Serves under `/api`. |
| `src/base/cics/` | CICS path: COBOL, BMS maps, copybooks |
| `src/base/ims/` | IMS path: COBOL, DBD/PSB, Java, PL/I, load data |
| `src/base/mq/` | MQ / Bank of Q Java samples |
| `src/base/batch/` | Batch COBOL / JCL / PL/I |

Bank of Z is a hybrid banking sample: the UI talks to z/OS Connect; customer IDs prefixed with **`C`** use CICS and **`I`** use IMS.

## Frontend overview (`src/frontend`)

- Multi-page static app; `index.html` redirects to `admin.html`
- ES modules + Carbon Web Components (CDN)
- API client: `js/api.js`; base URL and sort code: `config.js`
- Default sort code: `987654`

### Main pages

| Page | Purpose |
|------|---------|
| `admin.html` | Home — find/create customer, find account |
| `migration-dashboard.html` | Conversion dashboard: COBOL/DB2/JCL → Java/React/PostgreSQL (% converted, tests, performance) |
| `customer-create.html` / `customer-details.html` | Create customer / customer hub |
| `account-create.html` / `account-details.html` | Open / manage account |
| `account-deposit.html` | Credit / debit |
| `account-transactions.html` / `transaction-details.html` | History / detail |
| `account-transfer.html` | Transfer between accounts |

### Migration dashboard

Open http://localhost:8080/migration-dashboard.html while the frontend server is running.

Refresh inventory data from mainframe sources:

```bash
python3 scripts/migration-inventory.py --sample
```

This writes `src/frontend/data/migration-status.json` used by the dashboard KPIs.

### Customer ID rule

Enter one ID everywhere, with a system prefix:

- CICS: `C0000000042`
- IMS: `I000000015`

## Run the frontend locally

### Option A — quick static server (UI only)

From the repo root:

```bash
cd src/frontend
python3 -m http.server 8080
```

Open:

- http://localhost:8080/admin.html

`config.js` will call the API at `http://<host>:9080/api`. Start the API (Docker or z/OS Liberty) separately if you need live data.

### Option B — Docker (frontend + z/OS Connect)

From the repo root:

```bash
cd dev
# Set CICS_* / IMS_* in a .env file (see dev/README.md)
docker compose up
```

- Frontend: http://localhost:3001  
- z/OS Connect: http://localhost:9080  

On port **3001**, `config.js` uses relative `/api`, which nginx proxies to z/OS Connect.

### Option C — deployed on z/OS Liberty

After full Bank of Z setup:

- HTTP: `http://<zos-host>:9081/admin.html`
- HTTPS: `https://<zos-host>:9445/admin.html`
- API: ports **9080** (HTTP) / **9444** (HTTPS)

## API configuration (`config.js`)

| How you serve the UI | API base URL |
|----------------------|--------------|
| Port `3001` (Docker nginx) | `/api` (proxied) |
| HTTP other ports | `http://<hostname>:9080/api` |
| HTTPS | `https://<hostname>:9444/api` |

## Useful commands

```bash
# Frontend only
cd src/frontend && python3 -m http.server 8080

# Full local stack
cd dev && docker compose up

# Smoke-test frontend when Liberty is up
FRONTEND_URL=http://localhost:9081 ./tests/test_frontend.sh
```

## More documentation

- Root [README.md](../README.md)
- Local Docker: [dev/README.md](../dev/README.md)
- Full docs: https://ibm.github.io/Bank-of-Z/
