package com.unqiuehire.kashflow.dto.responsedto;

import com.unqiuehire.kashflow.constant.LoanDecisionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class LoanDecisionResponseDTO {

    private Long decisionId;
    private Long loanApplicationId;
    private Long loanId;
    private LoanDecisionStatus decision;
    private String reason;
    private LocalDateTime decidedAt;
}
