#!/usr/bin/env python3
"""
Build the measured conversion report shown on src/frontend/migration-dashboard.html.

Inputs (all measured, nothing sampled):
  - src/base                              legacy inventory (via scripts/migration-inventory.py)
  - conversion/input, conversion/output   legacy files used as input and the converted Spring Boot app
  - conversion/output/test-reports        Maven Surefire XML reports and JaCoCo jacoco.csv
  - http://localhost:8085                 running Spring Boot app, for response times (skipped if down)

Outputs:
  - src/frontend/data/conversion-report.json
  - src/frontend/data/migration-status.json (component status/targetLoc set from the mapping below,
    sample values removed)

Usage:
    python3 scripts/conversion-report.py [--api http://localhost:8085] [--samples 20] [--no-perf]
"""
import argparse
import csv
import datetime
import importlib.util
import json
import statistics
import time
import urllib.error
import urllib.request
import xml.etree.ElementTree as ET
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
BASE = ROOT / 'src' / 'base'
CONV = ROOT / 'conversion'
INPUT = CONV / 'input'
OUTPUT = CONV / 'output'
REPORTS = OUTPUT / 'test-reports'
DATA = ROOT / 'src' / 'frontend' / 'data'
J = 'src/main/java/com/bankz/'
T = 'src/test/java/com/bankz/'

# Each group: legacy files (relative to src/base, glob allowed) -> output files (relative to conversion/output).
# status: converted = Java implementation with automated tests; replaced = replaced by the web frontend;
# not-converted = no Java equivalent yet.
GROUPS = [
    dict(id='inqcust', wave='Customers', title='Customer inquiry', status='converted',
         legacy=['cics/cobol/INQCUST.cbl'],
         output=[J + 'customer/service/CustomerInquiryService.java', J + 'customer/service/CustomerNotFoundException.java',
                 J + 'customer/service/RandomConfig.java', J + 'customer/web/CustomerController.java',
                 J + 'customer/web/CustomerResponse.java', J + 'customer/web/InvalidCustomerIdException.java'],
         tests=['CustomerInquiryServiceTest', 'CustomerControllerIntegrationTest', 'CustomerResponseTest']),
    dict(id='crecust', wave='Customers', title='Customer create + credit agencies', status='converted',
         legacy=['cics/cobol/CRECUST.cbl', 'cics/cobol/CRDTAGY*.cbl'],
         output=[J + 'customer/service/CustomerCreateService.java', J + 'customer/service/CreditAgencyService.java',
                 J + 'customer/service/NewCustomer.java', J + 'customer/service/Titles.java'],
         tests=['BankFlowIntegrationTest']),
    dict(id='updcust', wave='Customers', title='Customer update', status='converted',
         legacy=['cics/cobol/UPDCUST.cbl'],
         output=[J + 'customer/service/CustomerUpdateService.java', J + 'customer/service/CustomerChanges.java'],
         tests=['BankFlowIntegrationTest']),
    dict(id='delcus', wave='Customers', title='Customer delete', status='converted',
         legacy=['cics/cobol/DELCUS.cbl'],
         output=[J + 'customer/service/CustomerDeleteService.java'],
         tests=['BankFlowIntegrationTest']),
    dict(id='accounts', wave='Accounts', title='Account create, inquire, list, update, delete', status='converted',
         legacy=['cics/cobol/CREACC.cbl', 'cics/cobol/INQACC.cbl', 'cics/cobol/INQACCS.cbl',
                 'cics/cobol/INQACCCU.cbl', 'cics/cobol/UPDACC.cbl', 'cics/cobol/DELACC.cbl'],
         output=[J + 'account/service/AccountService.java', J + 'account/service/NewAccount.java',
                 J + 'account/web/AccountController.java', J + 'account/web/AccountResponse.java',
                 J + 'account/domain/Account.java', J + 'account/domain/AccountKey.java',
                 J + 'account/domain/AccountRepository.java'],
         tests=['BankFlowIntegrationTest']),
    dict(id='payments', wave='Payments', title='Debit/credit and transfer', status='converted',
         legacy=['cics/cobol/DBCRFUN.cbl', 'cics/cobol/XFRFUN.cbl'],
         output=[J + 'payment/service/PaymentService.java', J + 'payment/web/PaymentController.java'],
         tests=['BankFlowIntegrationTest']),
    dict(id='transactions', wave='Payments', title='Transaction inquiry', status='converted',
         legacy=['cics/cobol/INQTRAND.cbl', 'cics/cobol/INQTRANL.cbl'],
         output=[J + 'payment/service/TransactionInquiryService.java'],
         tests=['BankFlowIntegrationTest']),
    dict(id='company', wave='Payments', title='Company name and sort code', status='converted',
         legacy=['cics/cobol/GETCOMPY.cbl', 'cics/cobol/GETSCODE.cbl'],
         output=[J + 'payment/service/CompanyService.java', J + 'payment/web/CompanyController.java'],
         tests=['BankFlowIntegrationTest']),
    dict(id='abend', wave='Payments', title='Abend handling', status='converted',
         legacy=['cics/cobol/ABNDPROC.cbl'],
         output=[J + 'common/AbendService.java', J + 'common/AbendRecord.java',
                 J + 'common/AbendRecordRepository.java', J + 'common/AbendException.java'],
         tests=['BankFlowIntegrationTest']),
    dict(id='data-layouts', wave='Shared', title='CICS copybooks and DB2 tables', status='converted',
         legacy=['cics/copy/*.cpy'],
         exclude=['cics/copy/BANKMAP.cpy', 'cics/copy/CUSTMAP.cpy', 'cics/copy/BNK1DDM.cpy'],
         extraInput=['db2/cics-Db2-create.j2'],
         output=[J + 'customer/domain/Customer.java', J + 'customer/domain/CustomerKey.java',
                 J + 'customer/domain/CustomerRepository.java', J + 'common/Proctran.java',
                 J + 'common/ProctranRepository.java', J + 'common/ProctranWriter.java',
                 J + 'common/ControlRecord.java', J + 'common/ControlRepository.java',
                 J + 'common/LegacyFormat.java', J + 'common/BusinessFailureException.java',
                 J + 'common/InvalidRequestException.java', J + 'common/ClockConfig.java',
                 J + 'common/web/GlobalExceptionHandler.java', J + 'common/web/Ids.java',
                 J + 'common/web/WebConfig.java', J + 'BankzApplication.java',
                 'src/main/resources/db/migration/V1__create_customer.sql',
                 'src/main/resources/db/migration/V2__create_account_proctran_control.sql',
                 'src/main/resources/application.yml'],
         tests=['BankFlowIntegrationTest', 'CustomerInquiryServiceTest']),
    dict(id='bankdata', wave='Batch', title='BANKDATA test-data generator', status='converted',
         legacy=['cics/cobol/BANKDATA.cbl'],
         output=[J + 'batch/BankDataGenerator.java'],
         tests=['BatchIntegrationTest']),
    dict(id='statements', wave='Batch', title='Monthly statements', status='converted',
         legacy=['batch/cobol/BNKSTMTC.cbl', 'batch/pli/BNKSTMTP.pli'],
         extraInput=['batch/MonthlyStatementSpecification.md'],
         output=[J + 'batch/StatementService.java', J + 'batch/StatementResult.java', J + 'batch/BatchController.java'],
         tests=['BatchIntegrationTest']),
    dict(id='jcl', wave='Batch', title='Statement JCL jobs', status='converted',
         legacy=['batch/jcl/*.jcl'],
         output=[J + 'batch/StatementJobRunner.java'],
         tests=['BatchIntegrationTest']),
    dict(id='ims-tran', wave='IMS', title='IMS transactions (login, customer, summary, deposit/withdraw)', status='converted',
         legacy=['ims/cobol/IB*.cbl', 'ims/pli/IBLOGIN.pli', 'ims/copy/*.cpy', 'ims/PSB/*.asm'],
         output=[J + 'ims/ImsService.java', J + 'ims/ImsController.java'],
         tests=['ImsIntegrationTest']),
    dict(id='ims-load', wave='IMS', title='IMS database load programs', status='converted',
         legacy=['ims/cobol/LOAD*.cbl'],
         extraInput=['ims/LoadData/*.data'],
         output=[J + 'ims/ImsDataLoader.java', 'src/main/resources/ims-data/*.data'],
         tests=['ImsIntegrationTest']),
    dict(id='ims-dbd', wave='IMS', title='IMS database definitions', status='converted',
         legacy=['ims/DBD/*.asm'],
         output=['src/main/resources/db/migration/V3__create_ims_tables.sql'],
         tests=['ImsIntegrationTest']),
    dict(id='screens', wave='Screens', title='3270 screens (BMS maps + screen programs)', status='replaced',
         legacy=['cics/cobol/BNK1*.cbl', 'cics/cobol/BNKMENU.cbl', 'cics/bms/*.bms',
                 'cics/copy/BANKMAP.cpy', 'cics/copy/CUSTMAP.cpy', 'cics/copy/BNK1DDM.cpy'],
         output=[], frontend=['src/frontend/*.html', 'src/frontend/js/api.js', 'src/frontend/config.js'],
         note='Not translated to Java. The web pages in src/frontend replace the screens and call the Java API.',
         tests=[]),
    dict(id='ims-java', wave='IMS', title='IMS Java history programs (nazare.jmp)', status='not-converted',
         legacy=['ims/java/**/*.java'], output=[],
         note='Not converted. IBTRAN history inserts are done directly in ImsService; history query programs have no Java equivalent yet.',
         tests=[]),
    dict(id='mq', wave='Integration', title='MQ Bank of Q transfer clients', status='not-converted',
         legacy=['mq/java/**/*.java'], output=[],
         note='Not converted. Needs a Spring JMS integration and an MQ broker.',
         tests=[]),
]

TEST_WAVES = {
    'CustomerInquiryServiceTest': 'Customers', 'CustomerControllerIntegrationTest': 'Customers',
    'CustomerResponseTest': 'Customers', 'BankFlowIntegrationTest': 'Customers, accounts, payments',
    'BatchIntegrationTest': 'Batch', 'ImsIntegrationTest': 'IMS',
}

ENDPOINTS = [
    ('GET', '/api/customers/1', 'INQCUST'),
    ('GET', '/api/customers/1/accounts', 'INQACCCU'),
    ('GET', '/api/accounts/1', 'INQACC'),
    ('GET', '/api/accounts', 'INQACCS'),
    ('GET', '/api/accounts/987654-00000001/transactions', 'INQTRANL'),
    ('GET', '/api/company', 'GETCOMPY'),
    ('GET', '/api/ims/customers/1', 'IBGCUDAT'),
    ('GET', '/api/ims/accounts/1', 'IBACSUM'),
    ('GET', '/api/batch/statements?customerId=987654-0000000001', 'BNKSTMTC'),
]

OUTSTANDING = [
    ('Mainframe parity run', 'Run the same inputs on z/OS and on the Java app and compare balances, records and reports. Needs z/OS CICS/IMS/DB2 access.'),
    ('Mainframe performance baseline', 'Response and batch times on the mainframe are not measured, so no before/after comparison yet.'),
    ('3270 screens', 'BNK1* / BNKMENU / BMS maps are replaced by the web frontend, not translated to Java.'),
    ('IMS Java and MQ clients', 'nazare.jmp history programs and Bank of Q MQ clients are not converted.'),
    ('Production data migration', 'Real DB2/IMS data (EBCDIC, packed decimal) has not been migrated; demo data and IMS LoadData files are used.'),
    ('OpenAPI contract check', 'REST routes follow src/api openapi.yaml, but no automated contract diff has been run.'),
    ('End-to-end UI test', 'Not every frontend flow has been browser-tested against the Java backend.'),
    ('Load testing', 'Response times are single-user samples on a laptop, not load tests on production-size PostgreSQL.'),
]


def load_inventory():
    spec = importlib.util.spec_from_file_location('inventory', ROOT / 'scripts' / 'migration-inventory.py')
    module = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(module)
    return module, module.scan()


def expand(root, patterns):
    files = []
    for pattern in patterns:
        matches = sorted(p for p in root.glob(pattern) if p.is_file())
        if not matches:
            raise SystemExit(f'No file matches {root.relative_to(ROOT)}/{pattern}')
        files.extend(matches)
    return files


def nonblank(path):
    return sum(1 for line in path.read_text(encoding='utf-8', errors='replace').splitlines() if line.strip())


def kind(path):
    s = path.suffix.lower()
    return {'.java': 'Java', '.sql': 'SQL', '.yml': 'Config', '.data': 'Data', '.html': 'HTML', '.js': 'JavaScript'}.get(s, s[1:])


def build_groups(components):
    by_id = {c['id']: c for c in components}
    assigned = {}
    groups = []
    for g in GROUPS:
        excluded = set(g.get('exclude', []))
        legacy = [p.relative_to(BASE).as_posix() for p in expand(BASE, g['legacy'])]
        legacy = [p for p in legacy if p not in excluded and p in by_id]
        for rel in legacy:
            if rel in assigned:
                raise SystemExit(f'{rel} is in both {assigned[rel]} and {g["id"]}')
            assigned[rel] = g['id']
            if not (INPUT / rel).exists() and g['status'] == 'converted':
                raise SystemExit(f'conversion/input/{rel} is missing')
        outputs = [{'path': p.relative_to(OUTPUT).as_posix(), 'kind': kind(p), 'lines': nonblank(p)}
                   for p in expand(OUTPUT, g['output'])]
        frontend = [{'path': p.relative_to(ROOT).as_posix(), 'kind': kind(p), 'lines': nonblank(p)}
                    for p in expand(ROOT, g.get('frontend', []))]
        extra = [p.relative_to(INPUT).as_posix() for p in expand(INPUT, g.get('extraInput', []))]
        groups.append({
            'id': g['id'], 'wave': g['wave'], 'title': g['title'], 'status': g['status'],
            'legacy': [{'path': rel, 'language': by_id[rel]['language'], 'lines': by_id[rel]['loc']} for rel in legacy],
            'extraInput': extra,
            'output': outputs,
            'frontend': frontend,
            'legacyLines': sum(by_id[rel]['loc'] for rel in legacy),
            'outputLines': sum(o['lines'] for o in outputs),
            'tests': g['tests'],
            'note': g.get('note', ''),
        })
    unmapped = [c['id'] for c in components if c['id'] not in assigned]
    if unmapped:
        raise SystemExit('Legacy files not in any group: ' + ', '.join(unmapped))
    return groups


def read_tests():
    suites = []
    for xml in sorted(REPORTS.glob('TEST-*.xml')):
        root = ET.parse(xml).getroot()
        name = root.get('name').rsplit('.', 1)[-1]
        cases = []
        for case in root.findall('testcase'):
            status = 'passed'
            if case.find('failure') is not None or case.find('error') is not None:
                status = 'failed'
            elif case.find('skipped') is not None:
                status = 'skipped'
            cases.append({'name': case.get('name'), 'time': float(case.get('time') or 0), 'status': status})
        failed = int(root.get('failures')) + int(root.get('errors'))
        skipped = int(root.get('skipped'))
        total = int(root.get('tests'))
        suites.append({'name': name, 'class': root.get('name'), 'covers': TEST_WAVES.get(name, ''),
                       'tests': total, 'passed': total - failed - skipped, 'failed': failed, 'skipped': skipped,
                       'time': float(root.get('time')), 'cases': cases})
    if not suites:
        return None
    return {
        'source': 'Maven Surefire (mvn test)',
        'reportedAt': datetime.datetime.fromtimestamp(max(p.stat().st_mtime for p in REPORTS.glob('TEST-*.xml')))
                       .astimezone().isoformat(timespec='seconds'),
        'suites': suites,
        'total': sum(s['tests'] for s in suites),
        'passed': sum(s['passed'] for s in suites),
        'failed': sum(s['failed'] for s in suites),
        'skipped': sum(s['skipped'] for s in suites),
        'time': round(sum(s['time'] for s in suites), 3),
    }


def read_coverage():
    path = REPORTS / 'jacoco.csv'
    if not path.exists():
        return None
    packages = {}
    for row in csv.DictReader(path.open()):
        p = packages.setdefault(row['PACKAGE'], {'name': row['PACKAGE'], 'classes': 0, 'lineCovered': 0,
                                                 'lineMissed': 0, 'branchCovered': 0, 'branchMissed': 0})
        p['classes'] += 1
        p['lineCovered'] += int(row['LINE_COVERED'])
        p['lineMissed'] += int(row['LINE_MISSED'])
        p['branchCovered'] += int(row['BRANCH_COVERED'])
        p['branchMissed'] += int(row['BRANCH_MISSED'])
    pk = sorted(packages.values(), key=lambda p: p['name'])
    lc, lm = sum(p['lineCovered'] for p in pk), sum(p['lineMissed'] for p in pk)
    bc, bm = sum(p['branchCovered'] for p in pk), sum(p['branchMissed'] for p in pk)
    return {'source': 'JaCoCo 0.8.12', 'packages': pk,
            'linePct': round(100 * lc / (lc + lm), 1) if lc + lm else 0,
            'branchPct': round(100 * bc / (bc + bm), 1) if bc + bm else 0,
            'lineCovered': lc, 'lineTotal': lc + lm}


def measure(api, samples):
    try:
        urllib.request.urlopen(api + '/api/company', timeout=3).read()
    except (urllib.error.URLError, OSError):
        print(f'{api} is not reachable: performance not measured')
        return None
    results = []
    for method, path, program in ENDPOINTS:
        times, status = [], None
        for i in range(samples + 3):
            start = time.perf_counter()
            try:
                with urllib.request.urlopen(urllib.request.Request(api + path, method=method), timeout=10) as r:
                    r.read()
                    status = r.status
            except urllib.error.HTTPError as e:
                status = e.code
            elapsed = (time.perf_counter() - start) * 1000
            if i >= 3:
                times.append(elapsed)
        times.sort()
        results.append({'method': method, 'path': path, 'legacy': program, 'status': status,
                        'medianMs': round(statistics.median(times), 1),
                        'p95Ms': round(times[min(len(times) - 1, int(len(times) * 0.95))], 1),
                        'minMs': round(times[0], 1)})
    return {'target': api, 'samples': samples, 'warmup': 3,
            'measuredAt': datetime.datetime.now().astimezone().isoformat(timespec='seconds'),
            'endpoints': results, 'mainframeBaseline': None}


def summarize(groups, components):
    def legacy_of(status):
        return [l for g in groups if g['status'] == status for l in g['legacy']]
    conv, repl, notc = legacy_of('converted'), legacy_of('replaced'), legacy_of('not-converted')
    out = [p for p in OUTPUT.rglob('*') if p.is_file() and 'test-reports' not in p.parts]
    java_main = [p for p in out if p.suffix == '.java' and '/src/main/' in p.as_posix()]
    java_test = [p for p in out if p.suffix == '.java' and '/src/test/' in p.as_posix()]
    sql = [p for p in out if p.suffix == '.sql']
    return {
        'legacyFiles': len(components), 'legacyLines': sum(c['loc'] for c in components),
        'convertedFiles': len(conv), 'convertedLines': sum(l['lines'] for l in conv),
        'replacedFiles': len(repl), 'replacedLines': sum(l['lines'] for l in repl),
        'notConvertedFiles': len(notc), 'notConvertedLines': sum(l['lines'] for l in notc),
        'javaFiles': len(java_main), 'javaLines': sum(nonblank(p) for p in java_main),
        'testFiles': len(java_test), 'testLines': sum(nonblank(p) for p in java_test),
        'sqlFiles': len(sql), 'sqlLines': sum(nonblank(p) for p in sql),
        'inputFiles': sum(1 for p in INPUT.rglob('*') if p.is_file()),
        'outputFiles': len(out),
    }


def java_by_package():
    rows = {}
    for p in (OUTPUT / 'src/main/java').rglob('*.java'):
        pkg = '.'.join(p.relative_to(OUTPUT / 'src/main/java').parts[:-1])
        r = rows.setdefault(pkg, {'name': pkg, 'files': 0, 'lines': 0})
        r['files'] += 1
        r['lines'] += nonblank(p)
    return sorted(rows.values(), key=lambda r: r['name'])


def update_status(groups, components, inventory):
    """Write measured status into migration-status.json and drop the sample values."""
    path = DATA / 'migration-status.json'
    data = json.loads(path.read_text())
    status_by_file, target = {}, {}
    for g in groups:
        st = {'converted': 'tested', 'replaced': 'replaced', 'not-converted': 'not-started'}[g['status']]
        for l in g['legacy']:
            status_by_file[l['path']] = (st, g['title'], g['note'])
            target[l['path']] = round(g['outputLines'] * l['lines'] / g['legacyLines']) if g['legacyLines'] else 0
    for c in components:
        st, title, note = status_by_file[c['id']]
        c.update({'status': st, 'targetLoc': target[c['id']], 'tests': None, 'performance': None,
                  'notes': note or title})
    data.update({
        'generatedAt': datetime.datetime.now().astimezone().isoformat(timespec='seconds'),
        'sample': False,
        'sourceRevision': inventory.source_revision(),
        'parity': inventory.default_parity(False),
        'components': components,
    })
    path.write_text(json.dumps(data, indent=2) + '\n')


def main():
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument('--api', default='http://localhost:8085')
    parser.add_argument('--samples', type=int, default=20)
    parser.add_argument('--no-perf', action='store_true')
    args = parser.parse_args()

    inventory, components = load_inventory()
    groups = build_groups(components)
    report = {
        'generatedAt': datetime.datetime.now().astimezone().isoformat(timespec='seconds'),
        'measured': True,
        'sourceRevision': inventory.source_revision(),
        'summary': summarize(groups, components),
        'javaPackages': java_by_package(),
        'groups': groups,
        'tests': read_tests(),
        'coverage': read_coverage(),
        'performance': None if args.no_perf else measure(args.api.rstrip('/'), args.samples),
        'outstanding': [{'item': i, 'detail': d} for i, d in OUTSTANDING],
    }
    if args.no_perf:
        old = DATA / 'conversion-report.json'
        if old.exists():
            report['performance'] = json.loads(old.read_text()).get('performance')
    (DATA / 'conversion-report.json').write_text(json.dumps(report, indent=2) + '\n')
    update_status(groups, components, inventory)
    s = report['summary']
    print(f"Converted {s['convertedFiles']}/{s['legacyFiles']} legacy files "
          f"({s['convertedLines']}/{s['legacyLines']} lines); wrote src/frontend/data/conversion-report.json")


if __name__ == '__main__':
    main()
