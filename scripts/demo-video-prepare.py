#!/usr/bin/env python3
"""Demo video preparation script.

Performs the actions described in doc/demo-video-structure.md:
  1. Starts the legacy 3270 simulator server (deliverables/02_legacy_simulation/simulator).
  2. Verifies the modern stack is reachable (bankz-app :8085, frontend :3001).
  3. Runs the identical-scenario API calls and saves the evidence JSON.
  4. Copies the available screenshots into the demo asset folder.
  5. Emits a scene manifest (JSON) + combined narration script for recording.

Usage (from repo root, e.g. ~/Desktop/Bank-of-Z):
    python3 scripts/demo-video-prepare.py
Optional: --repo <path>  --sim-port 3222  --api http://localhost:8085
"""
import argparse, json, os, shutil, signal, subprocess, sys, time, urllib.request

def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--repo", default=os.getcwd())
    ap.add_argument("--sim-port", type=int, default=3222)
    ap.add_argument("--api", default="http://localhost:8085")
    args = ap.parse_args()
    repo = os.path.abspath(args.repo)
    demo = os.path.join(repo, "deliverables", "06_demo")
    assets = os.path.join(demo, "assets")
    evidence = os.path.join(demo, "evidence")
    os.makedirs(assets, exist_ok=True); os.makedirs(evidence, exist_ok=True)

    print("[1/5] Simulator server")
    sim_dir = os.path.join(repo, "deliverables", "02_legacy_simulation", "simulator")
    sim_proc = None
    if get(f"http://localhost:{args.sim_port}/index.html", parse_json=False) is not None:
        print(f"  already running on :{args.sim_port}")
    elif os.path.isdir(sim_dir):
        sim_proc = subprocess.Popen(
            [sys.executable, "-m", "http.server", str(args.sim_port), "--directory", sim_dir],
            stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
        ok = False
        for _ in range(20):
            time.sleep(0.5)
            if get(f"http://localhost:{args.sim_port}/index.html", parse_json=False) is not None:
                ok = True; break
        print(f"  started on :{args.sim_port} -> {'OK' if ok else 'FAILED'}")
    else:
        print(f"  simulator dir not found: {sim_dir}")

    print("[2/5] Modern stack checks")
    for name, url in [("API", f"{args.api}/api/company"),
                      ("frontend", "http://localhost:3001/")]:
        print(f"  {name}: {'OK' if get(url, parse_json=False) is not None else 'DOWN'}  {url}")

    print("[3/5] Identical-scenario evidence")
    calls = [
        ("s1_customer", "GET", f"{args.api}/api/customers/1", None),
        ("s2_account", "GET", f"{args.api}/api/accounts/2", None),
        ("s3_transfer", "POST", f"{args.api}/api/accounts/2/transfer",
         {"toAccountId": "1", "amount": 100.00, "fromSortCode": "987654", "toSortCode": "987654"}),
        ("s3_balances_from", "GET", f"{args.api}/api/accounts/2/balances", None),
        ("s3_balances_to", "GET", f"{args.api}/api/accounts/1/balances", None),
        ("s3_transactions", "GET", f"{args.api}/api/accounts/2/transactions", None),
        ("s4_insufficient", "POST", f"{args.api}/api/accounts/1/transfer",
         {"toAccountId": "2", "amount": 9999999.99, "fromSortCode": "987654", "toSortCode": "987654"}),
    ]
    results = {}
    for key, method, url, body in calls:
        results[key] = {"method": method, "url": url, "body": body, "response": get(url, method, body)}
        print(f"  {key}: {'captured' if results[key]['response'] is not None else 'FAILED'}")
    with open(os.path.join(evidence, "scenario-results.json"), "w") as f:
        json.dump({"captured_at": time.strftime("%Y-%m-%d %H:%M:%S"), "scenarios": results}, f, indent=2)

    print("[4/5] Screenshot assets")
    n = 0
    for src_dir in [os.path.join(repo, "deliverables", "02_legacy_simulation"),
                    os.path.expanduser("~/Downloads")]:
        if not os.path.isdir(src_dir):
            continue
        for fn in os.listdir(src_dir):
            if fn.startswith("screen-") and fn.endswith(".png") or fn == "bank-of-z-screens.zip":
                shutil.copy2(os.path.join(src_dir, fn), os.path.join(assets, fn)); n += 1
    print(f"  {n} assets copied to {assets}")

    print("[5/5] Scene manifest + narration")
    manifest = [
        {"scene": 0, "name": "Cold open", "record": "split frame: sim menu vs web app", "assets": ["screen-menu.png"]},
        {"scene": 1, "name": "Legacy estate", "record": "run scripts/migration-inventory.py in Devin session", "assets": []},
        {"scene": 2, "name": "Requirements", "record": "open doc/reverse-engineering.md", "assets": []},
        {"scene": 3, "name": "Legacy simulation", "record": f"http://localhost:{args.sim_port} menu->1->customer 1, ->7->transfer", "assets": ["screen-customer.png", "screen-transfer.png"]},
        {"scene": 4, "name": "Migration", "record": "INQCUST.cbl vs CustomerInquiryService.java + mvn test", "assets": []},
        {"scene": 5, "name": "Validation", "record": "evidence/scenario-results.json + dashboard", "assets": ["bank-of-z-screens.zip"]},
        {"scene": 6, "name": "Honest gaps", "record": "doc/comparison-validation.md section 7", "assets": []},
        {"scene": 7, "name": "Close", "record": "deliverables/ tree + dashboard Still-to-do", "assets": []},
    ]
    with open(os.path.join(demo, "scene-manifest.json"), "w") as f:
        json.dump(manifest, f, indent=2)
    print(f"  wrote {demo}/scene-manifest.json and evidence/scenario-results.json")
    if sim_proc:
        print(f"  simulator left running (pid {sim_proc.pid}); kill it when done recording")
    print("Done.")

def get(url, method="GET", body=None, parse_json=True):
    try:
        data = json.dumps(body).encode() if body is not None else None
        req = urllib.request.Request(url, data=data, method=method,
                                     headers={"Content-Type": "application/json"})
        with urllib.request.urlopen(req, timeout=5) as r:
            raw = r.read().decode()
            return json.loads(raw) if parse_json else raw
    except Exception as e:
        return {"_error": str(e)} if method != "GET" else None

if __name__ == "__main__":
    main()
