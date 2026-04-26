package com.unqiuehire.kashflow.service;

import com.unqiuehire.kashflow.dto.requestdto.LoanApplicationRequestDto;
import com.unqiuehire.kashflow.dto.responsedto.ApiResponse;
import com.unqiuehire.kashflow.dto.responsedto.LoanApplicationResponseDto;

import java.util.List;

public interface LoanApplicationService {

    ApiResponse<LoanApplicationResponseDto> createApplication(LoanApplicationRequestDto dto);

    ApiResponse<LoanApplicationResponseDto> getById(Long id);

    ApiResponse<List<LoanApplicationResponseDto>> getByBorrower(Long borrowerId);

    ApiResponse<List<LoanApplicationResponseDto>> getByLender(Long lenderId);

    ApiResponse<String> cancelApplication(Long id);
}