#!/usr/bin/env python3
"""
Scan the legacy mainframe sources in src/base and write the data file used by
src/frontend/migration-dashboard.html (src/frontend/data/migration-status.json).

The legacy inventory (files, lines, SQL / CICS / DL/I usage) is always re-scanned.
Per-component migration fields (status, targetLoc, tests, performance, notes) are kept
from the existing JSON, so they can be updated by hand or by a conversion/test pipeline.

Usage:
    python3 scripts/migration-inventory.py            # refresh inventory, keep tracked fields
    python3 scripts/migration-inventory.py --sample   # also fill missing fields with sample values
"""
import argparse
import datetime
import hashlib
import json
import re
import subprocess
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
BASE = ROOT / 'src' / 'base'
OUT = ROOT / 'src' / 'frontend' / 'data' / 'migration-status.json'

LANGUAGES = {
    '.cbl': 'COBOL', '.cob': 'COBOL', '.cobol': 'COBOL', '.pli': 'PL/I', '.jcl': 'JCL', '.bms': 'BMS',
    '.cpy': 'Copybook', '.asm': 'Assembler', '.java': 'Java',
}

TRACKED_FIELDS = ('status', 'targetLoc', 'tests', 'performance', 'notes')

STATUSES = ('not-started', 'in-progress', 'converted', 'tested', 'validated', 'replaced')

# Parity test areas and execution sequence from
# "Mainframe COBOL Applications for Conversion Testing".
PARITY_AREAS = (
    ('access-navigation', 'Access and navigation',
     'Valid/invalid sign-on, user roles, menu routing, return/cancel behavior and session context'),
    ('inquiry-update', 'Inquiry and update',
     'Existing/missing keys, displayed fields, validation order, committed changes and unchanged records after rejection'),
    ('payments', 'Payments',
     'Confirmation and cancellation, payment transaction creation, resulting balances and related record consistency'),
    ('numeric', 'Numeric behavior',
     'Decimal precision, signed fields, truncation/rounding, boundary values and leading-zero identifiers'),
    ('batch', 'Batch',
     'Identical input files and job sequence, accepted/rejected records, account deltas, statements and report totals'),
    ('failure-handling', 'Failure handling',
     'Missing records, duplicate keys, interrupted updates, locking and rollback/commit outcomes'),
    ('data-conversion', 'Data conversion',
     'Record layouts, EBCDIC conversion, packed decimals, padding and cross-record relationships'),
)

PARITY_STEPS = (
    ('pin', 'Pin source and fixtures',
     'Record commit IDs, input-data hashes, compiler/runtime settings and the business date.'),
    ('capture', 'Capture original behavior first',
     'Save inputs, screen outputs, messages, persisted-state changes, return codes and reports.'),
    ('replay', 'Reset and replay',
     'Start both systems from equivalent data and run the same ordered actions.'),
    ('compare', 'Compare business outcomes',
     'Reconcile balances, transaction amounts, relationships and reporting totals, not merely screen appearance.'),
    ('independent', 'Keep expectations independent',
     'Do not use the converted code to generate its own expected results.'),
    ('separate', 'Separate parity from corrections',
     'Preserve observed source behavior for the benchmark; record approved defect fixes as separate acceptance criteria.'),
)
STEP_STATUSES = ('not-started', 'in-progress', 'done')

TARGET_STACK = {
    'Business logic': 'Java 21 / Spring Boot',
    'User interface': 'React',
    'Data': 'PostgreSQL / Spring Data JPA',
    'Batch': 'Spring Batch',
    'Integration': 'Spring Boot JMS',
}


def classify(rel: Path):
    """Return (layer, platform, target, targetLanguage) for a legacy file."""
    parts = rel.parts
    name = rel.stem.upper()
    area, kind = parts[0], parts[1]

    if area == 'batch':
        if kind == 'jcl':
            return 'Batch', 'z/OS JCL', 'Spring Batch job definition', 'Java'
        return 'Batch', 'z/OS batch + DB2', 'Spring Batch job', 'Java'

    if area == 'cics':
        if kind == 'bms':
            return 'User interface', 'CICS BMS', 'React component', 'React'
        if kind == 'copy':
            if 'DB2' in name:
                return 'Data', 'DB2', 'PostgreSQL table + JPA entity', 'SQL'
            return 'Data', 'CICS copybook', 'Java record / DTO', 'Java'
        if name.startswith('BNK1') or name == 'BNKMENU':
            return 'User interface', 'CICS 3270', 'React page + REST controller', 'React'
        return 'Business logic', 'CICS + DB2', 'Spring Boot service', 'Java'

    if area == 'ims':
        if kind == 'DBD':
            return 'Data', 'IMS DB', 'PostgreSQL table', 'SQL'
        if kind == 'PSB':
            return 'Data', 'IMS PSB', 'Spring Data repository', 'Java'
        if kind == 'LoadData':
            return 'Data', 'IMS seed data', 'PostgreSQL seed data', 'SQL'
        if kind == 'copy':
            return 'Data', 'IMS copybook', 'Java record / DTO', 'Java'
        if kind == 'cobol' and name.startswith('LOAD'):
            return 'Data', 'IMS load program', 'Flyway data migration', 'SQL'
        if kind == 'java':
            return 'Business logic', 'IMS JMP (Java)', 'Spring Boot service (refactor)', 'Java'
        return 'Business logic', 'IMS TM', 'Spring Boot service', 'Java'

    if area == 'mq':
        return 'Integration', 'IBM MQ', 'Spring Boot JMS listener', 'Java'

    return 'Other', area, 'Review', 'Java'


def count(pattern, text):
    return len(re.findall(pattern, text, flags=re.IGNORECASE))


def is_cobol_comment(line):
    """Fixed-format comment ('*' or '/' in column 7) or free-format '*>' comment."""
    return (len(line) > 6 and line[6] in '*/') or line.lstrip().startswith('*>')


def source_revision():
    try:
        return subprocess.run(['git', 'rev-parse', 'HEAD'], cwd=ROOT, capture_output=True,
                              text=True, check=True).stdout.strip() or None
    except (OSError, subprocess.CalledProcessError):
        return None


def scan():
    components = []
    for path in sorted(BASE.rglob('*')):
        if not path.is_file() or path.suffix.lower() not in LANGUAGES:
            continue
        rel = path.relative_to(BASE)
        text = path.read_text(encoding='utf-8', errors='replace')
        lines = text.splitlines()
        language = LANGUAGES[path.suffix.lower()]
        layer, platform, target, target_language = classify(rel)
        components.append({
            'id': rel.as_posix(),
            'name': path.stem,
            'language': language,
            'platform': platform,
            'layer': layer,
            'target': target,
            'targetLanguage': target_language,
            'loc': sum(1 for line in lines if line.strip()),
            'physicalLines': len(lines),
            'codeLines': sum(1 for line in lines
                             if line.strip() and not (language == 'COBOL' and is_cobol_comment(line))),
            'sqlStatements': count(r'EXEC\s+SQL', text),
            'cicsCommands': count(r'EXEC\s+CICS', text),
            'dliCalls': count(r'CBLTDLI|PLITDLI|AIBTDLI', text),
        })
    return components


# ---------------------------------------------------------------------------
# Sample values (only used with --sample, for fields that are not tracked yet)
# ---------------------------------------------------------------------------
LAYER_PROGRESS = {
    'Data': 0.9, 'Business logic': 0.7, 'User interface': 0.5,
    'Batch': 0.35, 'Integration': 0.75, 'Other': 0.2,
}
LOC_RATIO = {'Java': 0.45, 'React': 0.7, 'SQL': 0.6}


def rand(seed: str, salt: str) -> float:
    digest = hashlib.md5(f'{seed}:{salt}'.encode()).hexdigest()
    return int(digest[:8], 16) / 0xFFFFFFFF


def sample_fields(c):
    r = rand(c['id'], 'status')
    w = LAYER_PROGRESS.get(c['layer'], 0.3)
    if r < w * 0.55:
        status = 'validated'
    elif r < w * 0.8:
        status = 'tested'
    elif r < w:
        status = 'converted'
    elif r < w + 0.15:
        status = 'in-progress'
    else:
        status = 'not-started'

    fields = {'status': status, 'targetLoc': 0, 'tests': None, 'performance': None}
    if status == 'not-started':
        return fields

    ratio = LOC_RATIO.get(c['targetLanguage'], 0.5)
    done = 0.4 if status == 'in-progress' else 1.0
    fields['targetLoc'] = int(c['loc'] * ratio * done * (0.85 + 0.3 * rand(c['id'], 'loc')))

    if status in ('tested', 'validated'):
        total = max(3, c['loc'] // 60)
        failed = 0 if status == 'validated' else int(rand(c['id'], 'fail') * 3)
        fields['tests'] = {
            'total': total,
            'passed': total - failed,
            'failed': failed,
            'coverage': round(70 + 25 * rand(c['id'], 'cov'), 1),
        }
        if c['layer'] in ('Business logic', 'User interface', 'Integration') and c['language'] != 'BMS':
            legacy = round(35 + 90 * rand(c['id'], 'legacy'), 1)
            fields['performance'] = {
                'unit': 'ms',
                'legacy': legacy,
                'modern': round(legacy * (0.45 + 0.7 * rand(c['id'], 'modern')), 1),
            }
        elif c['layer'] == 'Batch' and c['language'] != 'JCL':
            legacy = round(240 + 600 * rand(c['id'], 'legacy'))
            fields['performance'] = {
                'unit': 's',
                'legacy': legacy,
                'modern': round(legacy * (0.5 + 0.5 * rand(c['id'], 'modern'))),
            }
    return fields


def default_parity(sample):
    areas = []
    for area_id, area, checks in PARITY_AREAS:
        scenarios = None
        if sample and rand(area_id, 'parity') > 0.15:
            total = 10 + int(rand(area_id, 'total') * 30)
            executed = int(total * (0.4 + 0.6 * rand(area_id, 'executed')))
            failed = int(executed * 0.15 * rand(area_id, 'failed'))
            scenarios = {'total': total, 'executed': executed, 'passed': executed - failed, 'failed': failed}
        areas.append({'id': area_id, 'area': area, 'checks': checks, 'scenarios': scenarios})
    sample_steps = ('done', 'done', 'done', 'in-progress', 'in-progress', 'not-started')
    steps = [{'id': step_id, 'title': title, 'detail': detail,
              'status': sample_steps[i] if sample else 'not-started'}
             for i, (step_id, title, detail) in enumerate(PARITY_STEPS)]
    return {'areas': areas, 'steps': steps}


def merge_parity(old, sample):
    """Keep recorded parity results; add any area or step that is missing."""
    parity = default_parity(sample)
    if not old:
        return parity
    for key in ('areas', 'steps'):
        recorded = {item['id']: item for item in old.get(key, [])}
        parity[key] = [recorded.get(item['id'], item) for item in parity[key]] + \
            [item for item_id, item in recorded.items() if item_id not in {i['id'] for i in parity[key]}]
    for step in parity['steps']:
        if step.get('status') not in STEP_STATUSES:
            raise SystemExit(f"parity step {step['id']}: unknown status '{step.get('status')}'")
    return parity


def main():
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument('--sample', action='store_true',
                        help='fill untracked migration fields with sample values')
    args = parser.parse_args()

    previous = {}
    was_sample = False
    old_parity = None
    if OUT.exists():
        data = json.loads(OUT.read_text())
        previous = {c['id']: c for c in data.get('components', [])}
        was_sample = data.get('sample', False)
        old_parity = data.get('parity')

    components = scan()
    for c in components:
        old = previous.get(c['id'], {})
        if 'status' in old:
            for field in TRACKED_FIELDS:
                if field in old:
                    c[field] = old[field]
        elif args.sample:
            c.update(sample_fields(c))
        else:
            c.update({'status': 'not-started', 'targetLoc': 0, 'tests': None, 'performance': None})
        if c['status'] not in STATUSES:
            raise SystemExit(f"{c['id']}: unknown status '{c['status']}'")

    OUT.parent.mkdir(parents=True, exist_ok=True)
    OUT.write_text(json.dumps({
        'generatedAt': datetime.datetime.now().astimezone().isoformat(timespec='seconds'),
        'sample': args.sample or was_sample,
        'sourceRevision': source_revision(),
        'targetStack': TARGET_STACK,
        'parity': merge_parity(old_parity, args.sample),
        'components': components,
    }, indent=2) + '\n')
    print(f'Wrote {len(components)} components to {OUT.relative_to(ROOT)}')


if __name__ == '__main__':
    main()
