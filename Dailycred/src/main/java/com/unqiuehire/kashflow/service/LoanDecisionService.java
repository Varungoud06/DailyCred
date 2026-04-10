package com.unqiuehire.kashflow.service;

import com.unqiuehire.kashflow.dto.requestdto.LoanDecisionRequestDTO;
import com.unqiuehire.kashflow.dto.responsedto.ApiResponse;
import com.unqiuehire.kashflow.dto.responsedto.LoanDecisionResponseDTO;

public interface LoanDecisionService {
    ApiResponse<LoanDecisionResponseDTO> approveLoan(Long applicationId, LoanDecisionRequestDTO requestDTO);
    ApiResponse<LoanDecisionResponseDTO> rejectLoan(Long applicationId, LoanDecisionRequestDTO requestDTO);
    ApiResponse<LoanDecisionResponseDTO> getLoanDecisionByApplicationId(Long applicationId);
    ApiResponse<LoanDecisionResponseDTO> updateLoanDecision(Long applicationId, LoanDecisionRequestDTO requestDTO);
}
