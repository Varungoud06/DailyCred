package com.unqiuehire.kashflow.service;

import com.unqiuehire.kashflow.dto.requestdto.LoanRequestDto;
import com.unqiuehire.kashflow.dto.responsedto.ApiResponse;
import com.unqiuehire.kashflow.dto.responsedto.LoanResponseDto;
import com.unqiuehire.kashflow.entity.Loan;
import com.unqiuehire.kashflow.entity.LoanApplication;

import java.util.List;

public interface LoanService {

    Loan createLoanFromApplication(LoanApplication app);


    ApiResponse<LoanResponseDto> getLoanById(Long loanId);

    ApiResponse<List<LoanResponseDto>> getLoansByBorrower(Long borrowerId);

    ApiResponse<List<LoanResponseDto>> getLoansByLender(Long lenderId);

    ApiResponse<String> closeLoan(Long loanId);
}