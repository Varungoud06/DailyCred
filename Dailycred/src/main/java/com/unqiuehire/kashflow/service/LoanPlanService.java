package com.unqiuehire.kashflow.service;

import com.unqiuehire.kashflow.dto.requestdto.LoanPlanRequest;
import com.unqiuehire.kashflow.dto.responsedto.ApiResponse;
import com.unqiuehire.kashflow.dto.responsedto.LoanPlanResponse;

import java.util.List;

public interface LoanPlanService {

    ApiResponse<LoanPlanResponse> createLoanPlan(LoanPlanRequest request);

    ApiResponse<LoanPlanResponse> getLoanPlanById(Long id);

    ApiResponse<List<LoanPlanResponse>> getAllLoanPlans();

    ApiResponse<LoanPlanResponse> updateLoanPlan(Long id, LoanPlanRequest request);

    ApiResponse<String> deleteLoanPlan(Long id);
}