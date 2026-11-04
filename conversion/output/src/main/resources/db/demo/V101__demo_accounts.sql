-- Fictional sample accounts and transactions, loaded only with the "demo" profile.
INSERT INTO account VALUES
 ('ACCT', '0000000001', '987654', '00000001', 'CURRENT',  0.00, DATE '2020-01-15',  500, DATE '2026-10-01', DATE '2026-11-01', 1250.75, 1250.75),
 ('ACCT', '0000000001', '987654', '00000002', 'SAVING',   2.50, DATE '2020-02-01',    0, DATE '2026-10-01', DATE '2026-11-01', 8400.00, 8400.00),
 ('ACCT', '0000000002', '987654', '00000003', 'ISA',      3.10, DATE '2021-07-20',    0, DATE '2026-10-01', DATE '2026-11-01', 15000.00, 15000.00),
 ('ACCT', '0000000042', '987654', '00000004', 'MORTGAGE', 4.25, DATE '2019-03-02',    0, DATE '2026-10-01', DATE '2026-11-01', -185000.00, -185000.00);

INSERT INTO proctran (proctran_eyecatcher, proctran_sortcode, proctran_number, proctran_date, proctran_time, proctran_ref, proctran_type, proctran_desc, proctran_amount) VALUES
 ('PRTR', '987654', '00000001', DATE '2026-10-28', '093015', '000000000901', 'CRE', 'COUNTER RECVED', 250.00),
 ('PRTR', '987654', '00000001', DATE '2026-10-29', '141200', '000000000902', 'DEB', 'COUNTER WTHDRW', -40.00),
 ('PRTR', '987654', '00000002', DATE '2026-10-30', '101500', '000000000903', 'PCR', 'SALARY', 1200.00);

UPDATE control SET control_value_num = 42 WHERE control_name = 'BANKZCUST987654';
UPDATE control SET control_value_num = 4  WHERE control_name IN ('987654-ACCOUNT-LAST', '987654-ACCOUNT-COUNT');
