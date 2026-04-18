package com.unqiuehire.kashflow.dto.requestdto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoanApplicationRequestDto {
    private Long borrowerId;
    private Long planId;
    private Double loanAmount;
}