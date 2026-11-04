/* 3270-style simulator for the CICS Banking Sample Application.
 * Screens rendered from BMS maps (screens.js). Simulated datastore —
 * same demo data as the modern app. Not connected to a mainframe.
 */
const term = document.getElementById('term');
const SORTCODE = '987654';

// ---- simulated datastore (mirrors demo data in the modern app) ----
const db = {
    nextCust: 3, nextAcc: 4, nextTran: 6,
    customers: {
        1: { title: 'Mr', first: 'John', last: 'Smith', initials: 'P', dob: '01051980', addr1: '14 Hill Street', addr2: '', city: 'LONDON', postcode: 'EC1 4AB', country: 'UK', credit: 720, review: '01062025' },
        2: { title: 'Mrs', first: 'Mary', last: 'Jones', initials: 'A', dob: '22031975', addr1: '5 Bank Lane', addr2: '', city: 'LEEDS', postcode: 'LS1 2CD', country: 'UK', credit: 650, review: '22092025' }
    },
    accounts: {
        '00000001': { cust: 1, type: 'ISA', intrt: '0001.50', overdr: 0, open: '01012023', avbal: 1250.00, actbal: 1250.00 },
        '00000002': { cust: 1, type: 'CURRENT', intrt: '0000.25', overdr: 500, open: '01012023', avbal: 2310.55, actbal: 2310.55 },
        '00000003': { cust: 2, type: 'MORTGAGE', intrt: '0004.10', overdr: 0, open: '14062024', avbal: -150000.00, actbal: -150000.00 }
    },
    transactions: [
        { n: '0000000001', acc: '00000002', type: 'CREDIT', date: '01112026', amt: 500.00, desc: 'Salary' },
        { n: '0000000002', acc: '00000002', type: 'DEBIT', date: '01112026', amt: 89.45, desc: 'Utilities' },
        { n: '0000000003', acc: '00000001', type: 'CREDIT', date: '01112026', amt: 1000.00, desc: 'Deposit' }
    ]
};

// ---- screen routing (menu option -> map) ----
const ROUTES = { '1': 'BNK1DCM', '2': 'BNK1DAM', '3': 'BNK1CCM', '4': 'BNK1CAM', '5': 'BNK1UAM', '6': 'BNK1CDM', '7': 'BNK1TFM', 'A': 'BNK1ACC' };

let current = 'BNK1MAI';
const fmtAmt = (n) => n.toFixed(2).padStart(14, ' ');
const fmtDate = (d) => ({ dd: d.slice(0, 2), mm: d.slice(2, 4), yy: d.slice(4, 8) });
const num = (s) => (s || '').replace(/[^0-9.\-]/g, '');

function isInput(f) {
    if (!f.name) return false;
    const attrs = f.attrb.split(',');
    return !attrs.includes('PROT') && !attrs.includes('ASKIP');
}

function render(mapId, values = {}, message = '') {
    current = mapId;
    term.innerHTML = '';
    for (const f of SCREENS[mapId]) {
        const el = isInput(f)
            ? Object.assign(document.createElement('input'), { maxLength: f.len, size: Math.max(1, f.len), value: values[f.name] !== undefined ? values[f.name] : f.initial.slice(0, f.len) })
            : document.createElement('span');
        if (!isInput(f)) el.textContent = (values[f.name] !== undefined ? values[f.name] : f.initial).slice(0, f.len);
        if (f.name === 'MESSAGE' && message) el.textContent = message.slice(0, f.len);
        el.dataset.name = f.name;
        el.className = `f c-${f.color}`;
        el.style.top = `calc(${(f.row - 1)} * 1.55em)`;
        el.style.left = `calc(${f.col - 1}ch)`;
        term.appendChild(el);
    }
    const first = term.querySelector('input');
    if (first) { first.focus(); first.select(); }
    else term.focus();
}

function inputs() {
    const v = {};
    term.querySelectorAll('input.f').forEach((i) => { v[i.dataset.name] = i.value.trim(); });
    return v;
}

function msg(mapId, values, message) { render(mapId, values, message); }

function findCust(id) { return db.customers[Number(num(id))]; }
function custScreenValues(c, id) {
    const dob = fmtDate(c.dob), rev = fmtDate(c.review);
    return { CUSTNO2: String(id).padStart(10, '0'), SORTC: SORTCODE, CUSTTITL: c.title, CUSTFNAM: c.first, CUSTLNAM: c.last, CUSTTIT: c.title, CHRISTN: c.first, CUSTINS: c.initials, CUSTSN: c.last, CUSTAD1: c.addr1, CUSTAD2: c.addr2, CITY: c.city, CUSTCITY: c.city, POSTCODE: c.postcode, CUSTPOST: c.postcode, COUNTRY: c.country, CUSTCTRY: c.country, DOBDD: dob.dd, DOBMM: dob.mm, DOBYY: dob.yy, CREDSC: String(c.credit), SCRDTDD: rev.dd, SCRDTMM: rev.mm, SCRDTYY: rev.yy };
}
function accValues(a, id) {
    const op = fmtDate(a.open);
    return { ACCNO2: id, CUSTNO: String(a.cust).padStart(10, '0'), SORTC: SORTCODE, ACTYPE: a.type, INTRT: a.intrt, OVERDR: String(a.overdr), OPENDD: op.dd, OPENMM: op.mm, OPENYY: op.yy, AVBAL: fmtAmt(a.avbal), ACTBAL: fmtAmt(a.actbal), AVAIL: fmtAmt(a.avbal), FACCNO2: id, FACTBAL: fmtAmt(a.actbal), FAVBAL: fmtAmt(a.avbal), FSORTC: SORTCODE };
}

// ---- per-map Enter handlers (business simulation) ----
const HANDLERS = {
    BNK1MAI(v) {
        const opt = v.ACTION.toUpperCase();
        if (ROUTES[opt]) render(ROUTES[opt]);
        else msg('BNK1MAI', v, 'INVALID OPTION - ENTER 1-7 OR A');
    },
    BNK1DCM(v) { // display customer by number
        const id = num(v.CUSTNO);
        const c = findCust(id);
        if (c) render('BNK1DCM', { ...custScreenValues(c, id), CUSTNO: v.CUSTNO }, 'CUSTOMER FOUND');
        else msg('BNK1DCM', v, 'CUSTOMER NOT FOUND - ENTER A VALID CUSTOMER NUMBER');
    },
    BNK1CCM(v) { // create/change customer
        const id = num(v.CUSTNO2 || v.CUSTNO) || db.nextCust;
        if (!v.CUSTTIT && !v.CUSTTITL && !v.CUSTNO2 && !v.CUSTSN && !v.CUSTFNAM) { render('BNK1CCM', {}, 'ENTER CUSTOMER DETAILS'); return; }
        const t = v.CUSTTIT || v.CUSTTITL || 'Mr';
        const fn = v.CHRISTN || v.CUSTFNAM || '';
        const sn = v.CUSTSN || v.CUSTLNAM || '';
        if (!fn || !sn) { msg('BNK1CCM', v, 'FIRST AND FAMILY NAME ARE REQUIRED'); return; }
        if (db.customers[id]) { // update
            db.customers[id] = { ...db.customers[id], title: t, first: fn, last: sn, initials: v.CUSTINS || db.customers[id].initials, dob: `${v.DOBDD}${v.DOBMM}${v.DOBYY}`, addr1: v.CUSTAD1, addr2: v.CUSTAD2, city: v.CITY, postcode: v.POSTCODE, country: v.COUNTRY };
            render('BNK1CCM', { ...custScreenValues(db.customers[id], id), CUSTNO2: String(id).padStart(10, '0') }, 'CUSTOMER UPDATED');
        } else { // credit check + create
            const dob = `${v.DOBDD || '01'}${v.DOBMM || '01'}${v.DOBYY || '2000'}`;
            db.customers[id] = { title: t, first: fn, last: sn, initials: v.CUSTINS || '', dob, addr1: v.CUSTAD1 || '', addr2: v.CUSTAD2 || '', city: v.CITY || '', postcode: v.POSTCODE || '', country: v.COUNTRY || '', credit: 650, review: '01112027' };
            db.nextCust = id + 1;
            render('BNK1CCM', { ...custScreenValues(db.customers[id], id), CUSTNO2: String(id).padStart(10, '0') }, `CREDIT CHECKS PASSED - CUSTOMER ${String(id).padStart(10, '0')} CREATED`);
        }
    },
    BNK1DAM(v) { // display account
        const id = num(v.ACCNO).padStart(8, '0');
        const a = db.accounts[id];
        if (a) render('BNK1DAM', { ...accValues(a, id), ACCNO: v.ACCNO }, 'ACCOUNT FOUND');
        else msg('BNK1DAM', v, 'ACCOUNT NOT FOUND - ENTER A VALID ACCOUNT NUMBER');
    },
    BNK1CAM(v) { // create account for customer
        const cid = num(v.CUSTNO);
        if (!db.customers[cid]) { msg('BNK1CAM', v, 'CUSTOMER NOT FOUND - ENTER A VALID CUSTOMER NUMBER'); return; }
        const id = String(db.nextAcc++).padStart(8, '0');
        const a = { cust: Number(cid), type: (v.ACTYP || 'CURRENT').toUpperCase(), intrt: v.INTRT || '0000.00', overdr: Number(num(v.OVERDR)) || 0, open: '04112026', avbal: 0, actbal: 0 };
        db.accounts[id] = a;
        render('BNK1CAM', { ...accValues(a, id), CUSTNO: v.CUSTNO, ACCNO: id }, `ACCOUNT ${id} CREATED`);
    },
    BNK1UAM(v) { // update account
        const id = num(v.ACCNO).padStart(8, '0');
        const a = db.accounts[id];
        if (!a) { msg('BNK1UAM', v, 'ACCOUNT NOT FOUND - ENTER A VALID ACCOUNT NUMBER'); return; }
        if (!v.ACTYPE && !v.INTRT && !v.OVERDR) { render('BNK1UAM', { ...accValues(a, id), ACCNO: v.ACCNO }, 'ACCOUNT FOUND - CHANGE FIELDS THEN ENTER'); return; }
        a.type = (v.ACTYPE || a.type).toUpperCase();
        a.intrt = v.INTRT || a.intrt;
        a.overdr = v.OVERDR !== '' && v.OVERDR !== undefined ? Number(num(v.OVERDR)) : a.overdr;
        render('BNK1UAM', { ...accValues(a, id), ACCNO: v.ACCNO }, 'ACCOUNT UPDATED');
    },
    BNK1CDM(v) { // credit/debit
        const id = num(v.ACCNO).padStart(8, '0');
        const a = db.accounts[id];
        if (!a) { msg('BNK1CDM', v, 'ACCOUNT NOT FOUND'); return; }
        const amt = parseFloat(num(v.AMT)) || 0;
        if (amt <= 0) { msg('BNK1CDM', v, 'AMOUNT MUST BE GREATER THAN ZERO'); return; }
        const sign = (v.SIGN || '+').startsWith('-') ? '-' : '+';
        if (sign === '-' && a.avbal + a.overdr < amt) { render('BNK1CDM', { ...accValues(a, id), ACCNO: v.ACCNO, AMT: v.AMT, SIGN: v.SIGN }, 'INSUFFICIENT FUNDS - TRANSACTION REJECTED'); return; }
        a.actbal += sign === '+' ? amt : -amt;
        a.avbal += sign === '+' ? amt : -amt;
        db.transactions.push({ n: String(db.nextTran++).padStart(10, '0'), acc: id, type: sign === '+' ? 'CREDIT' : 'DEBIT', date: '04112026', amt, desc: 'Counter transaction' });
        render('BNK1CDM', { ...accValues(a, id), ACCNO: v.ACCNO, AMT: v.AMT, SIGN: v.SIGN }, `${sign === '+' ? 'CREDIT' : 'DEBIT'} ${amt.toFixed(2)} APPLIED TO ACCOUNT ${id}`);
    },
    BNK1TFM(v) { // transfer
        const from = num(v.FACCNO).padStart(8, '0'), to = num(v.TACCNO).padStart(8, '0');
        const fa = db.accounts[from], ta = db.accounts[to];
        const amt = parseFloat(num(v.AMT)) || 0;
        if (!fa || !ta) { msg('BNK1TFM', v, 'FROM/TO ACCOUNT NOT FOUND'); return; }
        if (from === to) { msg('BNK1TFM', v, 'FROM AND TO ACCOUNTS MUST DIFFER'); return; }
        if (amt <= 0) { msg('BNK1TFM', v, 'AMOUNT MUST BE GREATER THAN ZERO'); return; }
        if (fa.avbal + fa.overdr < amt) { render('BNK1TFM', { ...accValues(fa, from), TACCNO2: to, TSORTC: SORTCODE, TACTBAL: fmtAmt(ta.actbal), TAVBAL: fmtAmt(ta.avbal), FACCNO: v.FACCNO, TACCNO: v.TACCNO, AMT: v.AMT }, 'INSUFFICIENT FUNDS - TRANSFER ROLLED BACK'); return; }
        fa.actbal -= amt; fa.avbal -= amt; ta.actbal += amt; ta.avbal += amt;
        db.transactions.push({ n: String(db.nextTran++).padStart(10, '0'), acc: from, type: 'TRANSFER', date: '04112026', amt, desc: `To ${to}` });
        render('BNK1TFM', { ...accValues(fa, from), TACCNO2: to, TSORTC: SORTCODE, TACTBAL: fmtAmt(ta.actbal), TAVBAL: fmtAmt(ta.avbal), FACCNO: v.FACCNO, TACCNO: v.TACCNO, AMT: v.AMT }, `TRANSFERRED ${amt.toFixed(2)} FROM ${from} TO ${to}`);
    },
    BNK1B2M(v) { HANDLERS.BNK1TFM(v); },
    BNK1ACC(v) { // accounts for customer
        const cid = num(v.CUSTNO);
        if (!db.customers[cid]) { msg('BNK1ACC', v, 'CUSTOMER NOT FOUND - ENTER A VALID CUSTOMER NUMBER'); return; }
        const list = Object.entries(db.accounts).filter(([, a]) => a.cust === Number(cid)).map(([id, a]) => `${id}  ${a.type.padEnd(10)} ${fmtAmt(a.actbal)}`).join('   ');
        render('BNK1ACC', { CUSTNO: v.CUSTNO, ACCOUNT: list || 'NO ACCOUNTS FOR THIS CUSTOMER' }, list ? `${list.split('  ').length} ACCOUNT(S) FOUND` : 'NO ACCOUNTS FOR THIS CUSTOMER');
    }
};

term.addEventListener('keydown', (e) => {
    if (e.key === 'Enter') {
        e.preventDefault();
        (HANDLERS[current] || (() => render('BNK1MAI')))(inputs());
    } else if (e.key === 'F3' || e.key === 'Escape') {
        e.preventDefault();
        render('BNK1MAI');
    }
});

render('BNK1MAI');
