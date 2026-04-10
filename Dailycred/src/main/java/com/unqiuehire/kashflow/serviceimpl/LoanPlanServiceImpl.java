package com.unqiuehire.kashflow.serviceimpl;

import com.unqiuehire.kashflow.constant.ApiStatus;
import com.unqiuehire.kashflow.constant.LoanPlanStatus;
import com.unqiuehire.kashflow.dto.requestdto.LoanPlanRequest;
import com.unqiuehire.kashflow.dto.responsedto.ApiResponse;
import com.unqiuehire.kashflow.dto.responsedto.LoanPlanResponse;
import com.unqiuehire.kashflow.entity.LoanPlan;
import com.unqiuehire.kashflow.exception.ResourceNotFoundException;
import com.unqiuehire.kashflow.repository.LoanPlanRepository;
import com.unqiuehire.kashflow.service.LoanPlanService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoanPlanServiceImpl implements LoanPlanService {

    private final LoanPlanRepository repository;

    // CREATE
    @Override
    public ApiResponse<LoanPlanResponse> createLoanPlan(LoanPlanRequest request) {

        LoanPlan loanPlan = LoanPlan.builder()
                .planName(request.getPlanName())
                .lenderId(request.getLenderId())
                .amount(request.getAmount())
                .interestPerDay(request.getInterestPerDay())
                .penaltyAmount(request.getPenaltyAmount())
                .planDuration(request.getPlanDuration())
                .maxRadius(request.getMaxRadius())
                .minCibil(request.getMinCibil())
                .status(LoanPlanStatus.valueOf(request.getStatus().toUpperCase()))
                .build();

        LoanPlan saved = repository.save(loanPlan);

        return new ApiResponse<>(
                ApiStatus.SUCCESS,
                "Loan Plan Created",
                mapToResponse(saved)
        );
    }

    //  GET BY ID
    @Override
    public ApiResponse<LoanPlanResponse> getLoanPlanById(Long id) {

        LoanPlan loanPlan = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Loan Plan not found: " + id)
                );

        return new ApiResponse<>(
                ApiStatus.SUCCESS,
                "Loan Plan Fetched",
                mapToResponse(loanPlan)
        );
    }

    //  GET ALL
    @Override
    public ApiResponse<List<LoanPlanResponse>> getAllLoanPlans() {

        List<LoanPlanResponse> list = repository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return new ApiResponse<>(
                ApiStatus.SUCCESS,
                "All Loan Plans",
                list
        );
    }

    //  UPDATE
    @Override
    public ApiResponse<LoanPlanResponse> updateLoanPlan(Long id, LoanPlanRequest request) {

        LoanPlan plan = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Loan Plan not found: " + id)
                );

        plan.setPlanName(request.getPlanName());
        plan.setLenderId(request.getLenderId());
        plan.setAmount(request.getAmount());
        plan.setInterestPerDay(request.getInterestPerDay());
        plan.setPenaltyAmount(request.getPenaltyAmount());
        plan.setPlanDuration(request.getPlanDuration());
        plan.setMaxRadius(request.getMaxRadius());
        plan.setMinCibil(request.getMinCibil());
        plan.setStatus(LoanPlanStatus.valueOf(request.getStatus().toUpperCase()));

        LoanPlan updated = repository.save(plan);

        return new ApiResponse<>(
                ApiStatus.SUCCESS,
                "Loan Plan Updated",
                mapToResponse(updated)
        );
    }

    // DELETE
    @Override
    public ApiResponse<String> deleteLoanPlan(Long id) {

        LoanPlan plan = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Loan Plan not found: " + id)
                );

        repository.delete(plan);

        return new ApiResponse<>(
                ApiStatus.SUCCESS,
                "Loan Plan Deleted",
                "Deleted successfully"
        );
    }

    // MAPPER (No utils as per your requirement)
    private LoanPlanResponse mapToResponse(LoanPlan loanPlan) {
        return LoanPlanResponse.builder()
                .id(loanPlan.getId())
                .planName(loanPlan.getPlanName())
                .lenderId(loanPlan.getLenderId())
                .amount(loanPlan.getAmount())
                .interestPerDay(loanPlan.getInterestPerDay())
                .penaltyAmount(loanPlan.getPenaltyAmount())
                .planDuration(loanPlan.getPlanDuration())
                .maxRadius(loanPlan.getMaxRadius())
                .minCibil(loanPlan.getMinCibil())
                .status(loanPlan.getStatus().name())
                .build();
    }
}