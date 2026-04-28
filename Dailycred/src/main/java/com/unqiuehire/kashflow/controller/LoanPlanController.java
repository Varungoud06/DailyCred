package com.unqiuehire.kashflow.controller;

import com.unqiuehire.kashflow.dto.requestdto.LoanPlanRequest;
import com.unqiuehire.kashflow.dto.responsedto.ApiResponse;
import com.unqiuehire.kashflow.dto.responsedto.LoanPlanResponseDto;
import com.unqiuehire.kashflow.service.LoanPlanService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/loan-plans")
@RequiredArgsConstructor
public class LoanPlanController {

    private final LoanPlanService service;

    //ADD LOAN PLAN
    @PostMapping("/lender/{lenderId}")
    public ApiResponse<LoanPlanResponseDto> create(
            @PathVariable Long lenderId,
            @RequestBody LoanPlanRequest request) {
        return service.createLoanPlan(lenderId, request);
    }

    // GET
    @GetMapping("/lender/{lenderId}")
    public ApiResponse<List<LoanPlanResponseDto>> getByLender(@PathVariable Long lenderId) {
        return service.getLoanPlansByLenderId(lenderId);
    }

    // UPDATE
    @PutMapping("/lender/{lenderId}/plan/{planId}")
    public ApiResponse<LoanPlanResponseDto> update(
            @PathVariable Long lenderId,
            @PathVariable Long planId,
            @RequestBody LoanPlanRequest request) {
        return service.updateLoanPlanByLenderId(lenderId, planId, request);
    }

    // GET ALL
    @GetMapping
    public ApiResponse<List<LoanPlanResponseDto>> getAll() {
        return service.getAllLoanPlans();
    }

    // DELETE LOAN PLAN
    @DeleteMapping("/lender/{lenderId}/plan/{planId}")
    public ApiResponse<String> delete(
            @PathVariable Long lenderId,
            @PathVariable Long planId) {
        return service.deleteLoanPlanByLenderId(lenderId, planId);
    }


//    @PostMapping
//    public ApiResponse<LoanPlanResponseDto> create(@RequestBody LoanPlanRequest request) {
//        return service.createLoanPlan(request);
//    }
//
//    @GetMapping("/{id}")
//    public ApiResponse<LoanPlanResponseDto> getById(@PathVariable Long id) {
//        return service.getLoanPlanById(id);
//    }
//
//    @GetMapping
//    public ApiResponse<List<LoanPlanResponseDto>> getAll() {
//        return service.getAllLoanPlans();
//    }
//
//    @PutMapping("/{id}")
//    public ApiResponse<LoanPlanResponseDto> update(
//            @PathVariable Long id,
//            @RequestBody LoanPlanRequest request) {
//        return service.updateLoanPlan(id, request);
//    }
//
//    @DeleteMapping("/{id}")
//    public ApiResponse<String> delete(@PathVariable Long id) {
//        return service.deleteLoanPlan(id);
//    }
}