package com.unqiuehire.kashflow.dto.requestdto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoanRequestDto {

    private Long loanApplicationId;
    private Long borrowerId;
    private Long lenderId;
    private Long planId;
    private Integer tenureDays;
    private Double sanctionedAmount;
    private Double interestPerDay;
    private Double penaltyAmount;
}
