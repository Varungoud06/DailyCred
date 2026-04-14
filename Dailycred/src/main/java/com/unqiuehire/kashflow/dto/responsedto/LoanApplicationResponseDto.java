package com.unqiuehire.kashflow.dto.responsedto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoanApplicationResponseDto {

    private Long applicationId;
    private Long borrowerId;
    private Long lenderId;
    private Long planId;
    private Double loanAmount;
    private String status;
    private String applicationDate;
}