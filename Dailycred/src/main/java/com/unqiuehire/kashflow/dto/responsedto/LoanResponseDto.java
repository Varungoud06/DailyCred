package com.unqiuehire.kashflow.dto.responsedto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoanResponseDto {

    private Long loanId;
    private Long loanApplicationId;
    private Long borrowerId;
    private Long lenderId;
    private Long planId;

    private Double sanctionedAmount;
    private Double remainingAmount;
    private Double totalAmount;
    private Double interestPerDay;
    private Integer tenureDays;
    private Double dailyEmi;
    private Double penaltyAmount;

    private String loanStatus;
    private String startDate;
    private String endDate;
}