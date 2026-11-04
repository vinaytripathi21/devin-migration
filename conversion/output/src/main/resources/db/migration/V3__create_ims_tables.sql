-- IMS databases (src/base/ims/DBD/*.asm) as PostgreSQL tables. Each DBD has a single root segment.
CREATE TABLE ims_customer (
    custid    INTEGER      NOT NULL PRIMARY KEY,
    lastname  VARCHAR(50),
    firstnm   VARCHAR(50),
    address   VARCHAR(80),
    city      VARCHAR(25),
    state     CHAR(2),
    zipcode   VARCHAR(15),
    phone     VARCHAR(12),
    status    CHAR(1),
    password  VARCHAR(16),
    custtype  CHAR(1),
    lastlogi  VARCHAR(26)
);

CREATE TABLE ims_account (
    accid     BIGINT        NOT NULL PRIMARY KEY,
    acctype   CHAR(1),
    balance   NUMERIC(15, 2) NOT NULL,
    lasttxid  BIGINT        NOT NULL
);

CREATE TABLE ims_custaccs (
    accid     BIGINT  NOT NULL PRIMARY KEY,
    custid    INTEGER NOT NULL,
    accnum    INTEGER
);
CREATE INDEX ims_custaccs_custid ON ims_custaccs (custid);

CREATE TABLE ims_history (
    txid      BIGINT        NOT NULL PRIMARY KEY,
    time      VARCHAR(26),
    transtyp  CHAR(1),
    amount    NUMERIC(15, 2),
    reftxid   BIGINT,
    accid     BIGINT
);
CREATE INDEX ims_history_accid ON ims_history (accid);

CREATE TABLE ims_tstat (
    txid      BIGINT  NOT NULL PRIMARY KEY,
    status    CHAR(1),
    starttim  VARCHAR(26),
    stoptime  VARCHAR(26),
    custid    INTEGER
);

-- ACCTYPE, CUSTTYPE, TSTATTYP and TTYPE code tables.
CREATE TABLE ims_code (
    dbd       VARCHAR(8)  NOT NULL,
    code      CHAR(1)     NOT NULL,
    descript  VARCHAR(40),
    PRIMARY KEY (dbd, code)
);
