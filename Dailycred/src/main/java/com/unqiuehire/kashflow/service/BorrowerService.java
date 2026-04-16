package com.unqiuehire.kashflow.service;

import com.unqiuehire.kashflow.dto.requestdto.BorrowerRequestDto;
import com.unqiuehire.kashflow.dto.responsedto.ApiResponse;
import com.unqiuehire.kashflow.dto.responsedto.BorrowerResponseDto;
import com.unqiuehire.kashflow.dto.responsedto.LoanApplicationResponseDto;

import java.util.List;

public interface BorrowerService {

    ApiResponse<BorrowerResponseDto> createBorrower(BorrowerRequestDto borrowerRequestDto);

    ApiResponse<List<LoanApplicationResponseDto>> getApplications(Long borrowerId);

    ApiResponse<BorrowerResponseDto> getBorrowerById(Long BorrowerId);

    ApiResponse<List<BorrowerResponseDto>> getAllBorrowers();

    ApiResponse<BorrowerResponseDto> updateBorrower(Long borrowerId, BorrowerRequestDto borrowerRequestDto);

    ApiResponse<String> deleteBorrower(Long borrowerId);
}
