package com.bankz.common;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Row of BANKZ.CONTROL: named counters for customer and account numbers. */
@Entity
@Table(name = "control")
public class ControlRecord {

    @Id
    @Column(name = "control_name")
    private String name;
    @Column(name = "control_value_num")
    private Integer valueNum;
    @Column(name = "control_value_str")
    private String valueStr;

    protected ControlRecord() {
    }

    public ControlRecord(String name, int valueNum) {
        this.name = name;
        this.valueNum = valueNum;
        this.valueStr = "";
    }

    public String getName() { return name; }
    public int getValueNum() { return valueNum == null ? 0 : valueNum; }
    public void setValueNum(int valueNum) { this.valueNum = valueNum; }
}
