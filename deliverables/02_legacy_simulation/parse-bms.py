#!/usr/bin/env python3
"""Parse BMS mapsets into screens.json for the 3270-style simulator.

Joins continuation lines, then extracts DFHMDF fields:
name, POS=(row,col), LENGTH, INITIAL, ATTRB, COLOR, HILIGHT.
Usage: python3 parse-bms.py <bms_dir> <out_json>
"""
import json
import re
import sys
from pathlib import Path

START = re.compile(r'^\s*([A-Z0-9_]+)?\s+(DFH\w+)\s')
ATTR = re.compile(
    r"(POS=\((\d+),(\d+)\)|LENGTH=(\d+)|INITIAL='((?:''|[^'])*)'|ATTRB=\(([^)]*)\)|COLOR=(\w+)|HILIGHT=(\w+))")


def logical_records(text):
    """Yield (name, statement_text) per BMS macro statement, continuations joined."""
    name, buf = '', ''
    for raw in text.splitlines():
        line = raw.rstrip('\n')
        if line.lstrip().startswith('*'):
            continue
        m = START.match(line)
        if m:
            if buf:
                yield name, buf
            name, buf = m.group(1) or '', line
        elif buf:
            buf += ' ' + line.strip()
    if buf:
        yield name, buf


def parse_map(path):
    fields = []
    for name, stmt in logical_records(path.read_text(errors='replace')):
        if 'DFHMDF' not in stmt:
            continue
        f = {'name': name.strip(), 'row': None, 'col': None, 'len': 0,
             'initial': '', 'attrb': '', 'color': 'GREEN', 'hilight': ''}
        for whole, r, c, ln, init, attrb, color, hil in ATTR.findall(stmt):
            if r:
                f['row'], f['col'] = int(r), int(c)
            elif ln:
                f['len'] = int(ln)
            elif init:
                f['initial'] = init.replace('*', '').replace("''", "'")
            elif attrb:
                f['attrb'] = attrb
            elif color:
                f['color'] = color
            elif hil:
                f['hilight'] = hil
        if f['row'] is not None:
            fields.append(f)
    return fields


def main():
    src, out = Path(sys.argv[1]), Path(sys.argv[2])
    screens = {bms.stem.upper(): parse_map(bms) for bms in sorted(src.glob('*.bms'))}
    for k, v in screens.items():
        print(f'{k}: {len(v)} fields')
    out.write_text(json.dumps(screens, indent=1))
    print(f'wrote {out}')


if __name__ == '__main__':
    main()
