-- PostgreSQL version of BANKZ.CUSTOMER from .setup/jcl/cics/Db2-create.j2
CREATE TABLE customer (
    customer_eyecatcher     CHAR(4),
    customer_sortcode       CHAR(6)  NOT NULL,
    customer_number         CHAR(10) NOT NULL,
    customer_title          CHAR(10),
    customer_first_name     CHAR(50),
    customer_last_name      CHAR(50),
    customer_date_of_birth  INTEGER,
    customer_phone          CHAR(20),
    customer_addr_line1     CHAR(50),
    customer_addr_line2     CHAR(50),
    customer_city           CHAR(50),
    customer_postcode       CHAR(10),
    customer_country        CHAR(50),
    customer_status         CHAR(10),
    customer_created_date   INTEGER,
    customer_credit_score   SMALLINT,
    customer_cs_review_date INTEGER,
    CONSTRAINT custindx PRIMARY KEY (customer_sortcode, customer_number)
);

CREATE INDEX custname ON customer (customer_last_name, customer_first_name);
