/*
 *
 *    Copyright IBM Corp. 2023
 *
 */

import { mountPage, notify, errorMessage } from './components/layout.js';
import { escapeHtml } from './utils.js';

const DATA_URL = 'data/migration-status.json';
const REPORT_URL = 'data/conversion-report.json';

const STATUS = {
    'not-started': { label: 'Not started', color: '#c6c6c6', tag: 'gray' },
    'in-progress': { label: 'In progress', color: '#a56eff', tag: 'purple' },
    'converted':   { label: 'Converted',   color: '#78a9ff', tag: 'blue' },
    'tested':      { label: 'Tested',      color: '#1192e8', tag: 'cyan' },
    'validated':   { label: 'Validated',   color: '#198038', tag: 'green' },
    'replaced':    { label: 'Replaced by web UI', color: '#ff832b', tag: 'warm-gray' }
};
const STATUS_KEYS = Object.keys(STATUS);
const DONE = new Set(['converted', 'tested', 'validated']);

const SUBSYSTEMS = {
    cics: { label: 'CICS online', path: 'src/base/cics/cobol/' },
    ims: { label: 'IMS', path: 'src/base/ims/cobol/' },
    batch: { label: 'Batch', path: 'src/base/batch/cobol/' }
};

const PARITY_STATUS = {
    'not-started': { label: 'Not started', tag: 'gray' },
    'in-progress': { label: 'In progress', tag: 'purple' },
    'passing':     { label: 'Passing', tag: 'green' },
    'failing':     { label: 'Failing', tag: 'red' }
};

const STEP_STATUS = {
    'not-started': { label: 'Not started', tag: 'gray' },
    'in-progress': { label: 'In progress', tag: 'purple' },
    'done':        { label: 'Done', tag: 'green' }
};

const fmt = (n) => Number(n || 0).toLocaleString();
const pct = (part, whole) => (whole ? Math.round((part / whole) * 1000) / 10 : 0);
const sum = (items, fn) => items.reduce((total, item) => total + (fn(item) || 0), 0);
const avg = (items, fn) => (items.length ? sum(items, fn) / items.length : 0);

function groupBy(items, fn) {
    const groups = new Map();
    items.forEach((item) => {
        const key = fn(item);
        if (!groups.has(key)) groups.set(key, []);
        groups.get(key).push(item);
    });
    return groups;
}

function changeHtml(legacy, modern) {
    if (!legacy) return '—';
    const change = Math.round(((modern - legacy) / legacy) * 1000) / 10;
    const cls = change <= 0 ? 'credit' : 'debit';
    const word = change <= 0 ? 'faster' : 'slower';
    return `<span class="${cls}">${change > 0 ? '+' : ''}${change}% ${word}</span>`;
}

function statusTag(status) {
    const s = STATUS[status] || STATUS['not-started'];
    return `<cds-tag type="${s.tag}" size="sm">${s.label}</cds-tag>`;
}

/** Horizontal bar made of segments: [{value, color, title}] */
function stackedBar(segments, total) {
    if (!total) return '<div class="mig-bar"></div>';
    const parts = segments
        .filter((seg) => seg.value > 0)
        .map((seg) => `<span style="width:${(seg.value / total) * 100}%;background:${seg.color}" title="${escapeHtml(seg.title)}"></span>`)
        .join('');
    return `<div class="mig-bar">${parts}</div>`;
}

function statusBar(components, weight = (c) => c.loc) {
    const total = sum(components, weight);
    return stackedBar(STATUS_KEYS.map((key) => {
        const value = sum(components.filter((c) => c.status === key), weight);
        return { value, color: STATUS[key].color, title: `${STATUS[key].label}: ${fmt(value)}` };
    }), total);
}

function donut(segments, centerValue, centerLabel) {
    const total = sum(segments, (s) => s.value);
    let offset = 25;
    const arcs = segments.filter((s) => s.value > 0).map((s) => {
        const length = (s.value / total) * 100;
        const arc = `<circle cx="21" cy="21" r="15.915" fill="none" stroke="${s.color}" stroke-width="6"
            stroke-dasharray="${length} ${100 - length}" stroke-dashoffset="${offset}"><title>${escapeHtml(s.title)}</title></circle>`;
        offset -= length;
        return arc;
    }).join('');
    return `
        <svg viewBox="0 0 42 42" class="mig-donut" role="img" aria-label="${escapeHtml(centerLabel)}">
            <circle cx="21" cy="21" r="15.915" fill="none" stroke="#e0e0e0" stroke-width="6"></circle>
            ${arcs}
            <text x="21" y="21" class="mig-donut__value">${escapeHtml(centerValue)}</text>
            <text x="21" y="26.5" class="mig-donut__label">${escapeHtml(centerLabel)}</text>
        </svg>`;
}

function legend() {
    return `<ul class="mig-legend">${STATUS_KEYS.map((key) =>
        `<li><span class="mig-dot" style="background:${STATUS[key].color}"></span>${STATUS[key].label}</li>`).join('')}</ul>`;
}

function kpi(label, value, sub) {
    return `
        <div class="card mig-kpi">
            <p class="mig-kpi__label">${escapeHtml(label)}</p>
            <p class="mig-kpi__value">${value}</p>
            <p class="mig-kpi__sub">${sub}</p>
        </div>`;
}

function summarize(components) {
    const done = components.filter((c) => DONE.has(c.status));
    const tested = components.filter((c) => c.tests);
    const online = components.filter((c) => c.performance && c.performance.unit === 'ms');
    const batch = components.filter((c) => c.performance && c.performance.unit === 's');
    const testsTotal = sum(tested, (c) => c.tests.total);
    return {
        totalLoc: sum(components, (c) => c.loc),
        doneLoc: sum(done, (c) => c.loc),
        done,
        inProgress: components.filter((c) => c.status === 'in-progress').length,
        validated: components.filter((c) => c.status === 'validated').length,
        targetLoc: sum(components, (c) => c.targetLoc),
        targetByLanguage: [...groupBy(components, (c) => c.targetLanguage)]
            .map(([lang, items]) => ({ lang, loc: sum(items, (c) => c.targetLoc) })),
        tested,
        testsTotal,
        testsPassed: sum(tested, (c) => c.tests.passed),
        testsFailed: sum(tested, (c) => c.tests.failed),
        coverage: testsTotal ? sum(tested, (c) => c.tests.coverage * c.tests.total) / testsTotal : 0,
        online,
        batch,
        legacyMs: avg(online, (c) => c.performance.legacy),
        modernMs: avg(online, (c) => c.performance.modern),
        legacyBatch: sum(batch, (c) => c.performance.legacy),
        modernBatch: sum(batch, (c) => c.performance.modern)
    };
}

function renderKpis(components, s) {
    const fasterPct = s.legacyMs ? Math.round((1 - s.modernMs / s.legacyMs) * 1000) / 10 : 0;
    const batchPct = s.legacyBatch ? Math.round((1 - s.modernBatch / s.legacyBatch) * 1000) / 10 : 0;
    return `
        <div class="mig-kpis">
            ${kpi('Code converted', `${pct(s.doneLoc, s.totalLoc)}%`, `${fmt(s.doneLoc)} of ${fmt(s.totalLoc)} legacy lines`)}
            ${kpi('Components converted', `${s.done.length} / ${components.length}`, `${s.inProgress} in progress`)}
            ${kpi('Modern code generated', fmt(s.targetLoc), s.targetByLanguage.map((t) => `${escapeHtml(t.lang)} ${fmt(t.loc)}`).join(' · ') + ' lines')}
            ${kpi('Functional parity', `${pct(s.validated, s.done.length)}%`, `${s.validated} of ${s.done.length} converted components validated against legacy`)}
            ${kpi('Tests passed', `${pct(s.testsPassed, s.testsTotal)}%`, `${fmt(s.testsPassed)} of ${fmt(s.testsTotal)} tests, ${fmt(s.testsFailed)} failed`)}
            ${kpi('Code coverage', `${Math.round(s.coverage * 10) / 10}%`, `Weighted over ${s.tested.length} tested components`)}
            ${s.online.length
                ? kpi('Avg response time', `${Math.round(s.modernMs)} ms`, `was ${Math.round(s.legacyMs)} ms on the mainframe (${fasterPct}% faster)`)
                : kpi('Avg response time', '—', 'No online transactions measured yet')}
            ${s.batch.length
                ? kpi('Batch run time', `${fmt(s.modernBatch)} s`, `was ${fmt(s.legacyBatch)} s (${batchPct}% faster)`)
                : kpi('Batch run time', '—', 'No batch jobs measured yet')}
        </div>`;
}

function renderLayers(components, targetStack) {
    const rows = [...groupBy(components, (c) => c.layer)].map(([layer, items]) => {
        const loc = sum(items, (c) => c.loc);
        const doneLoc = sum(items.filter((c) => DONE.has(c.status)), (c) => c.loc);
        return `
            <div class="mig-layer">
                <div class="mig-layer__head">
                    <strong>${escapeHtml(layer)}</strong>
                    <span class="muted">→ ${escapeHtml(targetStack[layer] || '')}</span>
                    <span class="mig-layer__pct">${pct(doneLoc, loc)}%</span>
                </div>
                ${statusBar(items)}
                <p class="hint">${items.filter((c) => DONE.has(c.status)).length} of ${items.length} components · ${fmt(doneLoc)} of ${fmt(loc)} lines</p>
            </div>`;
    }).join('');

    const counts = STATUS_KEYS.map((key) => {
        const value = components.filter((c) => c.status === key).length;
        return { value, color: STATUS[key].color, title: `${STATUS[key].label}: ${value}` };
    });
    const done = components.filter((c) => DONE.has(c.status)).length;

    return `
        <div class="mig-split">
            <div class="card">
                <h2 class="card__title">Conversion by layer</h2>
                <p class="card__intro">Share of legacy lines converted to the target stack, coloured by status.</p>
                ${legend()}
                <div class="mig-layers">${rows}</div>
            </div>
            <div class="card">
                <h2 class="card__title">Component status</h2>
                <p class="card__intro">Number of legacy components in each stage.</p>
                <div class="mig-donut-wrap">
                    ${donut(counts, `${pct(done, components.length)}%`, 'converted')}
                    <ul class="mig-legend mig-legend--stack">${counts.map((c, i) =>
                        `<li><span class="mig-dot" style="background:${c.color}"></span>${STATUS[STATUS_KEYS[i]].label}<strong>${c.value}</strong></li>`).join('')}</ul>
                </div>
            </div>
        </div>`;
}

function renderLanguages(components) {
    const rows = [...groupBy(components, (c) => c.language)]
        .sort((a, b) => sum(b[1], (c) => c.loc) - sum(a[1], (c) => c.loc))
        .map(([language, items]) => {
            const loc = sum(items, (c) => c.loc);
            const doneLoc = sum(items.filter((c) => DONE.has(c.status)), (c) => c.loc);
            return `
                <tr>
                    <td>${escapeHtml(language)}</td>
                    <td class="num">${items.length}</td>
                    <td class="num">${fmt(loc)}</td>
                    <td class="num">${fmt(doneLoc)}</td>
                    <td class="mig-bar-cell">${statusBar(items)}</td>
                    <td class="num">${pct(doneLoc, loc)}%</td>
                </tr>`;
        }).join('');

    const remaining = components.filter((c) => !DONE.has(c.status));
    const deps = [
        ['DB2 SQL statements', 'PostgreSQL / JPA', 'sqlStatements'],
        ['CICS commands', 'Spring Boot', 'cicsCommands'],
        ['IMS DL/I calls', 'PostgreSQL / JPA', 'dliCalls']
    ].map(([label, target, field]) => {
        const total = sum(components, (c) => c[field]);
        const left = sum(remaining, (c) => c[field]);
        return `
            <tr>
                <td>${label}</td>
                <td class="muted">${target}</td>
                <td class="num">${fmt(total)}</td>
                <td class="num">${fmt(total - left)}</td>
                <td class="num">${pct(total - left, total)}%</td>
            </tr>`;
    }).join('');

    return `
        <div class="mig-split">
            <div class="card">
                <h2 class="card__title">Legacy source by language</h2>
                <div class="table-wrap">
                    <table class="bz-table">
                        <thead><tr><th>Language</th><th class="num">Files</th><th class="num">Lines</th><th class="num">Converted</th><th>Status</th><th class="num">%</th></tr></thead>
                        <tbody>${rows}</tbody>
                    </table>
                </div>
            </div>
            <div class="card">
                <h2 class="card__title">Mainframe dependencies replaced</h2>
                <div class="table-wrap">
                    <table class="bz-table">
                        <thead><tr><th>Dependency</th><th>Replaced by</th><th class="num">Total</th><th class="num">Replaced</th><th class="num">%</th></tr></thead>
                        <tbody>${deps}</tbody>
                    </table>
                </div>
            </div>
        </div>`;
}

function renderCobolSize(components, revision) {
    const cobol = components.filter((c) => c.language === 'COBOL');
    const row = (label, path, items, total = false) => {
        const physical = sum(items, (c) => c.physicalLines);
        const code = sum(items, (c) => c.codeLines);
        const doneCode = sum(items.filter((c) => DONE.has(c.status)), (c) => c.codeLines);
        const b = (v) => (total ? `<strong>${v}</strong>` : v);
        return `
            <tr>
                <td>${b(escapeHtml(label))}${path ? `<div class="hint mono">${escapeHtml(path)}</div>` : ''}</td>
                <td class="num">${b(items.length)}</td>
                <td class="num">${b(fmt(physical))}</td>
                <td class="num">${b(fmt(code))}</td>
                <td class="num">${b(fmt(doneCode))}</td>
                <td class="mig-bar-cell">${statusBar(items, (c) => c.codeLines)}</td>
                <td class="num">${b(`${pct(doneCode, code)}%`)}</td>
            </tr>`;
    };
    const rows = [...groupBy(cobol, (c) => c.id.split('/')[0])].map(([key, items]) => {
        const sub = SUBSYSTEMS[key] || { label: key, path: `src/base/${key}/` };
        return row(sub.label, sub.path, items);
    }).join('');

    return `
        <div class="card">
            <h2 class="card__title">COBOL source size</h2>
            <p class="card__intro">Counted like the <em>Mainframe COBOL Applications for Conversion Testing</em> benchmark:
                <span class="mono">.cbl</span>, <span class="mono">.cob</span> and <span class="mono">.cobol</span> program files, COPY statements not expanded.
                Physical lines include comments and blanks; code lines drop blank lines and COBOL comments.
                Copybooks, BMS maps and JCL are not counted as COBOL source.</p>
            <div class="table-wrap">
                <table class="bz-table">
                    <thead><tr><th>Subsystem</th><th class="num">COBOL source files</th><th class="num">Physical COBOL lines</th>
                        <th class="num">Approx. nonblank, noncomment lines</th><th class="num">Converted code lines</th><th>Status</th><th class="num">%</th></tr></thead>
                    <tbody>${rows}${row('Total', '', cobol, true)}</tbody>
                </table>
            </div>
            ${revision ? `<p class="hint">Source revision <span class="mono">${escapeHtml(revision.slice(0, 12))}</span></p>` : ''}
        </div>`;
}

function parityAreaStatus(s) {
    if (!s || !s.executed) return 'not-started';
    if (s.failed) return 'failing';
    return s.executed < s.total ? 'in-progress' : 'passing';
}

function renderParity(parity) {
    const areas = (parity && parity.areas) || [];
    const steps = (parity && parity.steps) || [];
    const sc = (a) => a.scenarios || {};
    const planned = sum(areas, (a) => sc(a).total);
    const executed = sum(areas, (a) => sc(a).executed);
    const passed = sum(areas, (a) => sc(a).passed);
    const failed = sum(areas, (a) => sc(a).failed);

    const rows = areas.map((a) => {
        const s = sc(a);
        const st = PARITY_STATUS[parityAreaStatus(a.scenarios)];
        return `
            <tr>
                <td><strong>${escapeHtml(a.area)}</strong></td>
                <td class="mig-checks">${escapeHtml(a.checks)}</td>
                <td class="num">${s.total ? fmt(s.total) : '—'}</td>
                <td class="num">${s.total ? fmt(s.executed) : '—'}</td>
                <td class="num credit">${s.executed ? fmt(s.passed) : '—'}</td>
                <td class="num ${s.failed ? 'debit' : ''}">${s.executed ? fmt(s.failed) : '—'}</td>
                <td class="mig-bar-cell">${stackedBar([
                    { value: s.passed || 0, color: STATUS.validated.color, title: `Passed: ${s.passed || 0}` },
                    { value: s.failed || 0, color: '#da1e28', title: `Failed: ${s.failed || 0}` }
                ], s.total || 0)}</td>
                <td><cds-tag type="${st.tag}" size="sm">${st.label}</cds-tag></td>
            </tr>`;
    }).join('');

    const stepsHtml = steps.map((step) => {
        const st = STEP_STATUS[step.status] || STEP_STATUS['not-started'];
        return `
            <li>
                <div><strong>${escapeHtml(step.title)}</strong><div class="muted">${escapeHtml(step.detail)}</div></div>
                <cds-tag type="${st.tag}" size="sm">${st.label}</cds-tag>
            </li>`;
    }).join('');
    const stepsDone = steps.filter((step) => step.status === 'done').length;

    return `
        <div class="card">
            <h2 class="card__title">Parity test matrix</h2>
            <p class="card__intro">Original mainframe behaviour vs converted application, by test area.
                ${fmt(executed)} of ${fmt(planned)} scenarios executed (${pct(executed, planned)}%):
                ${fmt(passed)} passed, ${fmt(failed)} failed, pass rate ${pct(passed, executed)}%.</p>
            <div class="table-wrap">
                <table class="bz-table">
                    <thead><tr><th>Test area</th><th>Proposed checks</th><th class="num">Planned</th><th class="num">Executed</th>
                        <th class="num">Passed</th><th class="num">Failed</th><th>Progress</th><th>Status</th></tr></thead>
                    <tbody>${rows || '<tr><td colspan="8" class="empty">No parity test areas defined.</td></tr>'}</tbody>
                </table>
            </div>
            <h3 class="form-section-title">Execution sequence · ${stepsDone} of ${steps.length} done</h3>
            <ol class="mig-steps">${stepsHtml}</ol>
        </div>`;
}

function renderTests(components, s) {
    const rows = [...groupBy(components, (c) => c.layer)].map(([layer, items]) => {
        const tested = items.filter((c) => c.tests);
        const total = sum(tested, (c) => c.tests.total);
        const passed = sum(tested, (c) => c.tests.passed);
        const failed = sum(tested, (c) => c.tests.failed);
        const coverage = total ? sum(tested, (c) => c.tests.coverage * c.tests.total) / total : 0;
        return `
            <tr>
                <td>${escapeHtml(layer)}</td>
                <td class="num">${tested.length} / ${items.length}</td>
                <td class="num">${fmt(total)}</td>
                <td class="num credit">${fmt(passed)}</td>
                <td class="num ${failed ? 'debit' : ''}">${fmt(failed)}</td>
                <td class="mig-bar-cell">${stackedBar([
                    { value: passed, color: STATUS.validated.color, title: `Passed: ${passed}` },
                    { value: failed, color: '#da1e28', title: `Failed: ${failed}` }
                ], total)}</td>
                <td class="num">${total ? `${Math.round(coverage * 10) / 10}%` : '—'}</td>
            </tr>`;
    }).join('');

    const failing = components.filter((c) => c.tests && c.tests.failed > 0)
        .sort((a, b) => b.tests.failed - a.tests.failed);
    const failingHtml = failing.length
        ? `<ul class="mig-failing mig-scroll">${failing.map((c) =>
            `<li><span class="mono">${escapeHtml(c.name)}</span><span class="muted">${escapeHtml(c.target)}</span><span class="debit">${c.tests.failed} failed</span></li>`).join('')}</ul>`
        : '<p class="empty">No failing tests.</p>';

    return `
        <div class="card">
            <h2 class="card__title">Test evaluation</h2>
            <p class="card__intro">${fmt(s.testsTotal)} tests across ${s.tested.length} components: ${fmt(s.testsPassed)} passed, ${fmt(s.testsFailed)} failed.</p>
            <div class="table-wrap">
                <table class="bz-table">
                    <thead><tr><th>Layer</th><th class="num">Components tested</th><th class="num">Tests</th><th class="num">Passed</th><th class="num">Failed</th><th>Pass rate</th><th class="num">Coverage</th></tr></thead>
                    <tbody>${rows}</tbody>
                </table>
            </div>
            <h3 class="form-section-title">Components with failing tests</h3>
            ${failingHtml}
        </div>`;
}

function perfTable(items, unit) {
    if (!items.length) return '<p class="empty">No measurements yet.</p>';
    const max = Math.max(...items.map((c) => Math.max(c.performance.legacy, c.performance.modern)));
    const rows = [...items].sort((a, b) => b.performance.legacy - a.performance.legacy).map((c) => `
        <tr>
            <td><span class="mono">${escapeHtml(c.name)}</span><div class="hint">${escapeHtml(c.target)}</div></td>
            <td class="num">${fmt(c.performance.legacy)} ${unit}</td>
            <td class="num">${fmt(c.performance.modern)} ${unit}</td>
            <td class="mig-bar-cell">
                <div class="mig-perf"><span class="mig-perf__legacy" style="width:${(c.performance.legacy / max) * 100}%"></span></div>
                <div class="mig-perf"><span class="mig-perf__modern" style="width:${(c.performance.modern / max) * 100}%"></span></div>
            </td>
            <td class="num">${changeHtml(c.performance.legacy, c.performance.modern)}</td>
        </tr>`).join('');
    return `
        <div class="table-wrap mig-scroll">
            <table class="bz-table">
                <thead><tr><th>Component</th><th class="num">Mainframe</th><th class="num">Modern</th><th>Comparison</th><th class="num">Change</th></tr></thead>
                <tbody>${rows}</tbody>
            </table>
        </div>`;
}

function renderPerformance(s) {
    return `
        <div class="card">
            <h2 class="card__title">Performance</h2>
            <p class="card__intro">Mainframe vs modern stack, measured on tested components.</p>
            <ul class="mig-legend">
                <li><span class="mig-dot mig-perf__legacy"></span>Mainframe (CICS / IMS / batch)</li>
                <li><span class="mig-dot mig-perf__modern"></span>Modern (Spring Boot / PostgreSQL)</li>
            </ul>
            <h3 class="form-section-title">Online transactions (avg response time)</h3>
            ${perfTable(s.online, 'ms')}
            <h3 class="form-section-title">Batch jobs (run time)</h3>
            ${perfTable(s.batch, 's')}
        </div>`;
}

function renderComponentsCard(components) {
    const option = (value, label) => `<option value="${escapeHtml(value)}">${escapeHtml(label)}</option>`;
    const layers = [...new Set(components.map((c) => c.layer))];
    return `
        <div class="card">
            <div class="card__header">
                <h2 class="card__title">Components</h2>
                <span class="muted" id="mig-count"></span>
            </div>
            <div class="mig-filters">
                <label>Search<input id="mig-search" class="mig-input" type="search" placeholder="Program or target"></label>
                <label>Layer<select id="mig-layer" class="mig-input">${option('', 'All layers')}${layers.map((l) => option(l, l)).join('')}</select></label>
                <label>Status<select id="mig-status" class="mig-input">${option('', 'All statuses')}${STATUS_KEYS.map((k) => option(k, STATUS[k].label)).join('')}</select></label>
            </div>
            <div class="table-wrap mig-scroll mig-scroll--tall">
                <table class="bz-table">
                    <thead><tr><th>Component</th><th>Legacy</th><th>Layer</th><th>Target</th><th class="num">Legacy lines</th><th class="num">Modern lines</th><th>Status</th><th class="num">Tests</th><th class="num">Performance</th></tr></thead>
                    <tbody id="mig-rows"></tbody>
                </table>
            </div>
        </div>`;
}

function bindComponentsTable(components) {
    const search = document.getElementById('mig-search');
    const layer = document.getElementById('mig-layer');
    const status = document.getElementById('mig-status');

    const render = () => {
        const q = search.value.trim().toLowerCase();
        const rows = components.filter((c) =>
            (!layer.value || c.layer === layer.value) &&
            (!status.value || c.status === status.value) &&
            (!q || `${c.name} ${c.id} ${c.target}`.toLowerCase().includes(q)));

        document.getElementById('mig-count').textContent = `${rows.length} of ${components.length}`;
        document.getElementById('mig-rows').innerHTML = rows.length ? rows.map((c) => `
            <tr>
                <td><span class="mono">${escapeHtml(c.name)}</span><div class="hint">${escapeHtml(c.id)}</div></td>
                <td>${escapeHtml(c.language)}<div class="hint">${escapeHtml(c.platform)}</div></td>
                <td>${escapeHtml(c.layer)}</td>
                <td>${escapeHtml(c.target)}</td>
                <td class="num">${fmt(c.loc)}</td>
                <td class="num">${c.targetLoc ? fmt(c.targetLoc) : '—'}</td>
                <td>${statusTag(c.status)}</td>
                <td class="num">${c.tests ? `<span class="${c.tests.failed ? 'debit' : 'credit'}">${c.tests.passed}/${c.tests.total}</span>` : '—'}</td>
                <td class="num">${c.performance ? changeHtml(c.performance.legacy, c.performance.modern) : '—'}</td>
            </tr>`).join('') : '<tr><td colspan="9" class="empty">No components match the filters.</td></tr>';
    };

    [search, layer, status].forEach((el) => el.addEventListener('input', render));
    render();
}

const GROUP_STATUS = {
    'converted':     { label: 'Converted', tag: 'green', color: '#198038' },
    'replaced':      { label: 'Replaced by web UI', tag: 'warm-gray', color: '#ff832b' },
    'not-converted': { label: 'Not converted', tag: 'gray', color: '#c6c6c6' }
};

const groupTag = (status) => {
    const s = GROUP_STATUS[status] || GROUP_STATUS['not-converted'];
    return `<cds-tag type="${s.tag}" size="sm">${s.label}</cds-tag>`;
};

const ms = (v) => `${Number(v).toLocaleString(undefined, { maximumFractionDigits: 1 })} ms`;

function renderReportKpis(r) {
    const s = r.summary;
    const t = r.tests;
    const c = r.coverage;
    const p = r.performance;
    const median = p ? statistics(p.endpoints.map((e) => e.medianMs)) : null;
    return `
        <div class="mig-kpis">
            ${kpi('Code converted to Java', `${pct(s.convertedLines, s.legacyLines)}%`, `${fmt(s.convertedLines)} of ${fmt(s.legacyLines)} legacy lines`)}
            ${kpi('Legacy files converted', `${s.convertedFiles} / ${s.legacyFiles}`, `${s.replacedFiles} replaced by web UI · ${s.notConvertedFiles} not converted`)}
            ${kpi('Java code generated', fmt(s.javaLines), `${s.javaFiles} Java files · ${fmt(s.sqlLines)} SQL lines · ${fmt(s.testLines)} test lines`)}
            ${kpi('Migration coverage', `${pct(s.convertedLines + s.replacedLines, s.legacyLines)}%`, 'Legacy lines converted or replaced by the web UI')}
            ${t ? kpi('Tests passed', `${pct(t.passed, t.total)}%`, `${t.passed} of ${t.total} tests, ${t.failed} failed · ${t.time} s`) : kpi('Tests passed', '—', 'No test reports found')}
            ${c ? kpi('Code coverage', `${c.linePct}%`, `Lines · branches ${c.branchPct}% (${escapeHtml(c.source)})`) : kpi('Code coverage', '—', 'No JaCoCo report found')}
            ${p ? kpi('API response time', ms(median), `Median over ${p.endpoints.length} endpoints · mainframe baseline not measured`) : kpi('API response time', '—', 'Spring Boot app was not running')}
            ${kpi('Outstanding items', r.outstanding.length, 'See "Still to do" below')}
        </div>`;
}

function statistics(values) {
    const sorted = [...values].sort((a, b) => a - b);
    const mid = Math.floor(sorted.length / 2);
    return sorted.length % 2 ? sorted[mid] : (sorted[mid - 1] + sorted[mid]) / 2;
}

function renderWaves(r) {
    const rows = [...groupBy(r.groups, (g) => g.wave)].map(([wave, items]) => {
        const legacy = sum(items, (g) => g.legacyLines);
        const segs = Object.keys(GROUP_STATUS).map((key) => {
            const value = sum(items.filter((g) => g.status === key), (g) => g.legacyLines);
            return { value, color: GROUP_STATUS[key].color, title: `${GROUP_STATUS[key].label}: ${fmt(value)} lines` };
        });
        const done = sum(items.filter((g) => g.status === 'converted'), (g) => g.legacyLines);
        return `
            <tr>
                <td><strong>${escapeHtml(wave)}</strong></td>
                <td class="num">${sum(items, (g) => g.legacy.length)}</td>
                <td class="num">${fmt(legacy)}</td>
                <td class="num">${fmt(sum(items, (g) => g.outputLines))}</td>
                <td class="mig-bar-cell">${stackedBar(segs, legacy)}</td>
                <td class="num">${pct(done, legacy)}%</td>
            </tr>`;
    }).join('');
    return `
        <div class="card">
            <h2 class="card__title">Conversion by wave</h2>
            <p class="card__intro">Measured from <span class="mono">conversion/input</span> and <span class="mono">conversion/output</span>.</p>
            <ul class="mig-legend">${Object.values(GROUP_STATUS).map((s) => `<li><span class="mig-dot" style="background:${s.color}"></span>${s.label}</li>`).join('')}</ul>
            <div class="table-wrap">
                <table class="bz-table">
                    <thead><tr><th>Wave</th><th class="num">Legacy files</th><th class="num">Legacy lines</th><th class="num">Java/SQL lines</th><th>Status</th><th class="num">Converted</th></tr></thead>
                    <tbody>${rows}</tbody>
                </table>
            </div>
        </div>`;
}

function fileList(files, prefix) {
    if (!files.length) return '<span class="muted">—</span>';
    return `<ul class="mig-files">${files.map((f) =>
        `<li><span class="mono" title="${escapeHtml(prefix + f.path)}">${escapeHtml(f.path.replace('src/main/java/com/bankz/', '').replace('src/main/resources/', 'resources/'))}</span>${f.lines !== undefined ? `<span class="muted">${fmt(f.lines)}</span>` : ''}</li>`).join('')}</ul>`;
}

function renderConversionReport(r) {
    const option = (value, label) => `<option value="${escapeHtml(value)}">${escapeHtml(label)}</option>`;
    const waves = [...new Set(r.groups.map((g) => g.wave))];
    return `
        <div class="card">
            <div class="card__header">
                <h2 class="card__title">Converted file report</h2>
                <span class="muted" id="conv-count"></span>
            </div>
            <p class="card__intro">Each legacy input file and the Java/SQL output it became. Line counts are nonblank lines.</p>
            <div class="mig-filters">
                <label>Search<input id="conv-search" class="mig-input" type="search" placeholder="Program or Java class"></label>
                <label>Wave<select id="conv-wave" class="mig-input">${option('', 'All waves')}${waves.map((w) => option(w, w)).join('')}</select></label>
                <label>Status<select id="conv-status" class="mig-input">${option('', 'All statuses')}${Object.keys(GROUP_STATUS).map((k) => option(k, GROUP_STATUS[k].label)).join('')}</select></label>
            </div>
            <div class="table-wrap mig-scroll mig-scroll--tall">
                <table class="bz-table mig-report">
                    <thead><tr><th>Function</th><th>Input <span class="hint mono">src/base/</span></th><th>Output <span class="hint mono">conversion/output/src/main/java/com/bankz/</span></th><th class="num">Lines in → out</th><th>Tests</th><th>Status</th></tr></thead>
                    <tbody id="conv-rows"></tbody>
                </table>
            </div>
        </div>`;
}

function bindConversionReport(r) {
    const search = document.getElementById('conv-search');
    const wave = document.getElementById('conv-wave');
    const status = document.getElementById('conv-status');
    const suites = new Map(((r.tests && r.tests.suites) || []).map((s) => [s.name, s]));
    const render = () => {
        const q = search.value.trim().toLowerCase();
        const rows = r.groups.filter((g) =>
            (!wave.value || g.wave === wave.value) &&
            (!status.value || g.status === status.value) &&
            (!q || JSON.stringify([g.title, g.legacy, g.output, g.frontend]).toLowerCase().includes(q)));
        document.getElementById('conv-count').textContent = `${rows.length} of ${r.groups.length} functions`;
        document.getElementById('conv-rows').innerHTML = rows.length ? rows.map((g) => {
            const tests = g.tests.map((name) => {
                const s = suites.get(name);
                return s ? `<li><span class="${s.failed ? 'debit' : 'credit'}">${s.passed}/${s.tests}</span> <span class="mono">${escapeHtml(name)}</span></li>` : '';
            }).join('');
            const output = g.output.length ? fileList(g.output, 'conversion/output/')
                : g.frontend.length ? `<div class="hint">Web replacement:</div>${fileList(g.frontend.slice(0, 6), '')}${g.frontend.length > 6 ? `<div class="hint">+ ${g.frontend.length - 6} more files</div>` : ''}`
                : '<span class="muted">—</span>';
            return `
                <tr>
                    <td><strong>${escapeHtml(g.title)}</strong><div class="hint">${escapeHtml(g.wave)}</div>${g.note ? `<div class="hint">${escapeHtml(g.note)}</div>` : ''}</td>
                    <td>${fileList(g.legacy, 'src/base/')}${g.extraInput.length ? `<div class="hint">Also: ${g.extraInput.map(escapeHtml).join(', ')}</div>` : ''}</td>
                    <td>${output}</td>
                    <td class="num">${fmt(g.legacyLines)} → ${fmt(g.outputLines)}</td>
                    <td>${tests ? `<ul class="mig-files">${tests}</ul>` : '<span class="muted">—</span>'}</td>
                    <td>${groupTag(g.status)}</td>
                </tr>`;
        }).join('') : '<tr><td colspan="6" class="empty">No functions match the filters.</td></tr>';
    };
    [search, wave, status].forEach((el) => el.addEventListener('input', render));
    render();
}

function renderOutputCode(r) {
    const cov = new Map(((r.coverage && r.coverage.packages) || []).map((p) => [p.name, p]));
    const rows = r.javaPackages.map((p) => {
        const c = cov.get(p.name);
        const lines = c ? c.lineCovered + c.lineMissed : 0;
        const branches = c ? c.branchCovered + c.branchMissed : 0;
        return `
            <tr>
                <td class="mono">${escapeHtml(p.name)}</td>
                <td class="num">${p.files}</td>
                <td class="num">${fmt(p.lines)}</td>
                <td class="mig-bar-cell">${c ? stackedBar([{ value: c.lineCovered, color: STATUS.validated.color, title: `Covered: ${c.lineCovered}` }], lines) : ''}</td>
                <td class="num">${c ? `${pct(c.lineCovered, lines)}%` : '—'}</td>
                <td class="num">${branches ? `${pct(c.branchCovered, branches)}%` : '—'}</td>
            </tr>`;
    }).join('');
    return `
        <div class="card">
            <h2 class="card__title">Generated Java code and coverage</h2>
            <p class="card__intro">Spring Boot packages in <span class="mono">conversion/output/src/main/java</span>, with line and branch coverage from the test run.</p>
            <div class="table-wrap">
                <table class="bz-table">
                    <thead><tr><th>Package</th><th class="num">Files</th><th class="num">Lines</th><th>Line coverage</th><th class="num">Line %</th><th class="num">Branch %</th></tr></thead>
                    <tbody>${rows}</tbody>
                </table>
            </div>
        </div>`;
}

function renderReportTests(r) {
    const t = r.tests;
    if (!t) return '<div class="card"><h2 class="card__title">Test evaluation</h2><p class="empty">No test reports in conversion/output/test-reports.</p></div>';
    const rows = t.suites.map((s) => `
        <tr>
            <td><details><summary class="mono">${escapeHtml(s.name)}</summary>
                <ul class="mig-files">${s.cases.map((c) => `<li><span class="${c.status === 'passed' ? 'credit' : 'debit'}">${c.status}</span> ${escapeHtml(c.name)}<span class="muted">${c.time.toFixed(3)} s</span></li>`).join('')}</ul>
            </details></td>
            <td>${escapeHtml(s.covers)}</td>
            <td class="num">${s.tests}</td>
            <td class="num credit">${s.passed}</td>
            <td class="num ${s.failed ? 'debit' : ''}">${s.failed}</td>
            <td class="mig-bar-cell">${stackedBar([
                { value: s.passed, color: STATUS.validated.color, title: `Passed: ${s.passed}` },
                { value: s.failed, color: '#da1e28', title: `Failed: ${s.failed}` }
            ], s.tests)}</td>
            <td class="num">${s.time.toFixed(2)} s</td>
        </tr>`).join('');
    return `
        <div class="card">
            <h2 class="card__title">Test evaluation</h2>
            <p class="card__intro">${escapeHtml(t.source)}, ${escapeHtml(new Date(t.reportedAt).toLocaleString())}: ${t.total} tests, ${t.passed} passed, ${t.failed} failed, ${t.skipped} skipped in ${t.time} s.
                Tests run against H2 in PostgreSQL mode. Click a suite to see its test cases.</p>
            <div class="table-wrap">
                <table class="bz-table">
                    <thead><tr><th>Test suite</th><th>Covers</th><th class="num">Tests</th><th class="num">Passed</th><th class="num">Failed</th><th>Pass rate</th><th class="num">Time</th></tr></thead>
                    <tbody>${rows}</tbody>
                </table>
            </div>
        </div>`;
}

function renderReportPerformance(r) {
    const p = r.performance;
    if (!p) return '<div class="card"><h2 class="card__title">Performance</h2><p class="empty">Not measured: start the Spring Boot app and run python3 scripts/conversion-report.py.</p></div>';
    const max = Math.max(...p.endpoints.map((e) => e.p95Ms));
    const rows = p.endpoints.map((e) => `
        <tr>
            <td><span class="mono">${escapeHtml(e.method)} ${escapeHtml(e.path)}</span></td>
            <td class="mono">${escapeHtml(e.legacy)}</td>
            <td class="num">${e.status}</td>
            <td class="num">${ms(e.medianMs)}</td>
            <td class="num">${ms(e.p95Ms)}</td>
            <td class="mig-bar-cell">
                <div class="mig-perf"><span class="mig-perf__modern" style="width:${(e.medianMs / max) * 100}%"></span></div>
                <div class="mig-perf"><span class="mig-perf__legacy" style="width:${(e.p95Ms / max) * 100}%"></span></div>
            </td>
            <td class="num muted">Not measured</td>
        </tr>`).join('');
    return `
        <div class="card">
            <h2 class="card__title">Performance</h2>
            <p class="card__intro">Response times of the running Spring Boot app (${escapeHtml(p.target)}), ${p.samples} requests per endpoint after ${p.warmup} warm-up requests,
                measured ${escapeHtml(new Date(p.measuredAt).toLocaleString())}. Single user on a laptop with PostgreSQL in Docker; not a load test.</p>
            <ul class="mig-legend">
                <li><span class="mig-dot mig-perf__modern"></span>Median</li>
                <li><span class="mig-dot mig-perf__legacy"></span>95th percentile</li>
            </ul>
            <div class="table-wrap">
                <table class="bz-table">
                    <thead><tr><th>Endpoint</th><th>Replaces</th><th class="num">HTTP</th><th class="num">Median</th><th class="num">p95</th><th>Comparison</th><th class="num">Mainframe</th></tr></thead>
                    <tbody>${rows}</tbody>
                </table>
            </div>
        </div>`;
}

function renderOutstanding(r) {
    return `
        <div class="card">
            <h2 class="card__title">Still to do</h2>
            <p class="card__intro">Work needed before the migration can be called complete.</p>
            <ol class="mig-steps">${r.outstanding.map((o) => `
                <li><div><strong>${escapeHtml(o.item)}</strong><div class="muted">${escapeHtml(o.detail)}</div></div>
                <cds-tag type="gray" size="sm">Open</cds-tag></li>`).join('')}</ol>
        </div>`;
}

async function loadReport() {
    try {
        const response = await fetch(REPORT_URL, { cache: 'no-store' });
        return response.ok ? await response.json() : null;
    } catch {
        return null;
    }
}

async function init() {
    mountPage({
        crumbs: [{ label: 'Home', href: 'admin.html' }, { label: 'Migration dashboard' }],
        title: 'Mainframe migration dashboard',
        subtitle: 'COBOL, PL/I, JCL, CICS, IMS and DB2 → Java Spring Boot, React and PostgreSQL'
    });

    const content = document.getElementById('content');
    content.innerHTML = '<cds-loading type="small"></cds-loading>';

    try {
        const response = await fetch(DATA_URL, { cache: 'no-store' });
        if (!response.ok) throw new Error(`Could not load ${DATA_URL} (HTTP ${response.status})`);
        const data = await response.json();
        const components = data.components || [];
        const s = summarize(components);
        const report = await loadReport();

        if (data.sample) {
            notify('warning', 'Sample data',
                'The legacy inventory is scanned from src/base. Status, test, parity and performance figures are sample values until real results are recorded in data/migration-status.json.');
        }

        content.innerHTML = `
            <p class="muted mig-updated">Data generated ${escapeHtml(new Date((report || data).generatedAt).toLocaleString())} · ${components.length} legacy components${report ? ' · measured' : ''}</p>
            ${report ? renderReportKpis(report) : renderKpis(components, s)}
            ${report ? renderWaves(report) : ''}
            ${renderLayers(components, data.targetStack || {})}
            ${report ? renderConversionReport(report) : ''}
            ${report ? renderOutputCode(report) : ''}
            ${renderCobolSize(components, data.sourceRevision)}
            ${renderLanguages(components)}
            ${report ? renderReportTests(report) : renderTests(components, s)}
            ${renderParity(data.parity)}
            ${report ? renderReportPerformance(report) : renderPerformance(s)}
            ${report ? renderOutstanding(report) : ''}
            ${renderComponentsCard(components)}
        `;
        if (report) bindConversionReport(report);
        bindComponentsTable(components);
    } catch (error) {
        content.innerHTML = '';
        notify('error', 'Dashboard data unavailable',
            `${errorMessage(error)}. Run: python3 scripts/migration-inventory.py`);
    }
}

init();

// Made with Bob
