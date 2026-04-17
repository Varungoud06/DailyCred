package com.unqiuehire.kashflow.dto.requestdto;

import com.unqiuehire.kashflow.constant.LoanDecisionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class LoanDecisionRequestDTO {
    private Long loanApplicationId;
    private Long loanId;
    private Long lenderId;
    private LoanDecisionStatus decision;
    private String reason;
}
