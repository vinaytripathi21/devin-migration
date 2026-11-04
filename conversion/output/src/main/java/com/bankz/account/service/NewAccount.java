package com.bankz.account.service;

import java.math.BigDecimal;

/** CREACC commarea input. */
public record NewAccount(long customerNumber, String type, BigDecimal interestRate, int overdraftLimit,
                         BigDecimal availableBalance, BigDecimal actualBalance) {
}
