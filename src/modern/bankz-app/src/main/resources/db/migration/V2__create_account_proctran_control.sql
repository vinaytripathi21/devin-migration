-- PostgreSQL versions of BANKZ.ACCOUNT, BANKZ.PROCTRAN and BANKZ.CONTROL from .setup/jcl/cics/Db2-create.j2
CREATE TABLE account (
    account_eyecatcher        CHAR(4),
    account_customer_number   CHAR(10),
    account_sortcode          CHAR(6)  NOT NULL,
    account_number            CHAR(8)  NOT NULL,
    account_type              CHAR(8),
    account_interest_rate     DECIMAL(6, 2),
    account_opened            DATE,
    account_overdraft_limit   INTEGER,
    account_last_statement    DATE,
    account_next_statement    DATE,
    account_available_balance DECIMAL(12, 2),
    account_actual_balance    DECIMAL(12, 2),
    CONSTRAINT accindx PRIMARY KEY (account_sortcode, account_number)
);

CREATE INDEX acccust ON account (account_sortcode, account_customer_number);

-- DB2 PROCTRAN has no key; a surrogate id is added so JPA can address rows.
CREATE TABLE proctran (
    proctran_id         BIGSERIAL PRIMARY KEY,
    proctran_eyecatcher CHAR(4),
    proctran_sortcode   CHAR(6)  NOT NULL,
    proctran_number     CHAR(8)  NOT NULL,
    proctran_date       DATE,
    proctran_time       CHAR(6),
    proctran_ref        CHAR(12),
    proctran_type       CHAR(3),
    proctran_desc       CHAR(40),
    proctran_amount     DECIMAL(12, 2)
);

CREATE INDEX proctran_account ON proctran (proctran_sortcode, proctran_number, proctran_date);

-- Replaces CICS EIBTASKN, which the COBOL programs used as the 12-digit PROCTRAN reference.
CREATE SEQUENCE proctran_ref_seq START WITH 1;

-- control_name is VARCHAR (DB2: CHAR(32)) so lookups do not depend on blank padding.
CREATE TABLE control (
    control_name      VARCHAR(32) PRIMARY KEY,
    control_value_num INTEGER,
    control_value_str CHAR(40)
);

INSERT INTO control (control_name, control_value_num, control_value_str) VALUES
 ('BANKZCUST987654',       0, ''),
 ('987654-ACCOUNT-LAST',   0, ''),
 ('987654-ACCOUNT-COUNT',  0, '');

-- Replaces the ABNDFILE VSAM file written by ABNDPROC.
CREATE TABLE abend_record (
    abend_id     BIGSERIAL PRIMARY KEY,
    program      VARCHAR(8),
    abend_code   VARCHAR(4),
    occurred_at  TIMESTAMP,
    message      VARCHAR(600)
);
