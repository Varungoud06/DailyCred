package com.unqiuehire.kashflow.service;

import java.math.BigDecimal;

public interface LoanCalculationService {

    BigDecimal getInterestDeductionPercent(BigDecimal sanctionedAmount);

    BigDecimal calculateUpfrontInterest(BigDecimal sanctionedAmount);

    BigDecimal calculateDisbursedAmount(BigDecimal sanctionedAmount);

    BigDecimal calculateDailyDue(BigDecimal totalRepayableAmount, Integer tenureDays);
}