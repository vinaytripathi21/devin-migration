#!/usr/bin/env python3
"""Automatic demo screen-recording script (macOS).

Performs the recording actions from doc/demo-video-structure.md:
  * Opens the Cmd+Shift+5 screenshot/recording toolbar once (for manual capture).
  * Automatically walks through every scene — simulator, converted Java code,
    dashboard and all app screens — recording each with macOS `screencapture`.

Video capture uses `screencapture -V <seconds>` (records the whole screen for
N seconds). If the local macOS build does not support -V, each scene is saved
as a full-screen PNG instead so nothing is lost.

Requires: System Settings > Privacy & Security > Screen Recording permission
for Terminal (or the app running this script). The Cmd+Shift+5 toolbar needs
the same permission when it first opens.

Usage (from repo root):
    python3 scripts/demo-video-record.py [--secs 6] [--repo .]
"""
import argparse, json, os, subprocess, sys, time, urllib.request

SCENES = [
    ("scene0_legacy_sim_menu",      "browser", "http://localhost:3222/index.html"),
    ("scene1_legacy_customer",      "browser", "http://localhost:3222/index.html"),
    ("scene2_codebase_scan",        "terminal", "ls src/base/cics/cobol | head -20 && python3 scripts/migration-inventory.py"),
    ("scene3_converted_java",       "browser", "file://{repo}/src/modern/bankz-app/src/main/java/com/bankz/customer/service/CustomerInquiryService.java"),
    ("scene4_mvn_test",             "terminal", "cd src/modern/bankz-app && mvn -q test 2>&1 | tail -15"),
    ("scene5_modern_app",           "browser", "http://localhost:3001/?backend=spring"),
    ("scene6_api_evidence",         "browser", "http://localhost:8085/api/customers/1"),
    ("scene7_dashboard",            "browser", "http://localhost:3001/migration-dashboard.html"),
    ("scene8_gaps_doc",             "browser", "file://{repo}/doc/comparison-validation.md"),
]

def up(url):
    try:
        urllib.request.urlopen(url, timeout=3)
        return True
    except Exception:
        return False

def sh(cmd):
    return subprocess.run(cmd, shell=True, capture_output=True, text=True).stdout

def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--secs", type=int, default=6, help="seconds to record per scene")
    ap.add_argument("--repo", default=os.getcwd())
    args = ap.parse_args()
    repo = os.path.abspath(args.repo)
    out = os.path.join(repo, "deliverables", "06_demo", "recording")
    os.makedirs(out, exist_ok=True)
    manifest = []

    # 1) Open the Cmd+Shift+5 toolbar once (Screenshot app) for manual control.
    subprocess.Popen(["osascript", "-e",
        'tell application "System Events" to keystroke "5" using {command down, shift down}'],
        stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
    print("Cmd+Shift+5 toolbar triggered (Screenshot app).")
    time.sleep(2)

    # 2) Detect video-capable screencapture.
    video_ok = "-V" in sh("screencapture --help 2>&1; man screencapture 2>/dev/null | col -b | grep -o '\\-V ' | head -1")
    video_ok = True  # -V works on macOS 12+; fall back per-scene if it fails

    for i, (name, kind, target) in enumerate(SCENES):
        target = target.format(repo=repo)
        if target.startswith("http://localhost:3222") and not up("http://localhost:3222/index.html"):
            print(f"[{name}] simulator down — starting")
            subprocess.Popen([sys.executable, "-m", "http.server", "3222",
                "--directory", os.path.join(repo, "deliverables", "02_legacy_simulation", "simulator")],
                stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
            time.sleep(1.5)
        if kind == "browser":
            subprocess.run(["open", target])
            if i == 1:  # customer scene: type option 1 + customer number via AppleScript
                subprocess.run(["osascript", "-e",
                    'tell application "System Events" to keystroke "1"'])
        else:
            subprocess.Popen(["osascript", "-e",
                f'tell application "Terminal" to do script "{target.replace(chr(34), chr(92)+chr(34))}"'])
        time.sleep(2.5)

        mov = os.path.join(out, f"{name}.mov")
        r = subprocess.run(["screencapture", "-V", str(args.secs), mov],
                           capture_output=True, text=True)
        if r.returncode == 0 and os.path.exists(mov):
            manifest.append({"scene": name, "file": mov, "kind": "video", "secs": args.secs})
            print(f"[{name}] recorded {args.secs}s -> {mov}")
        else:  # fallback: still image
            png = os.path.join(out, f"{name}.png")
            subprocess.run(["screencapture", "-x", png])
            manifest.append({"scene": name, "file": png, "kind": "image"})
            print(f"[{name}] video unsupported -> screenshot {png}")

    with open(os.path.join(out, "recording-manifest.json"), "w") as f:
        json.dump(manifest, f, indent=2)
    print(f"Done. {len(manifest)} scenes -> {out}")
    print("NOTE: if scenes are blank, grant Screen Recording permission to your terminal and re-run.")

if __name__ == "__main__":
    main()
