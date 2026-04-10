package com.unqiuehire.kashflow.controller;

import com.unqiuehire.kashflow.dto.requestdto.LoanPlanRequest;
import com.unqiuehire.kashflow.dto.responsedto.ApiResponse;
import com.unqiuehire.kashflow.dto.responsedto.LoanPlanResponse;
import com.unqiuehire.kashflow.service.LoanPlanService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/loan-plans")
@RequiredArgsConstructor
public class LoanPlanController {

    private final LoanPlanService service;

    @PostMapping
    public ApiResponse<LoanPlanResponse> create(@RequestBody LoanPlanRequest request) {
        return service.createLoanPlan(request);
    }

    @GetMapping("/{id}")
    public ApiResponse<LoanPlanResponse> getById(@PathVariable Long id) {
        return service.getLoanPlanById(id);
    }

    @GetMapping
    public ApiResponse<List<LoanPlanResponse>> getAll() {
        return service.getAllLoanPlans();
    }

    @PutMapping("/{id}")
    public ApiResponse<LoanPlanResponse> update(
            @PathVariable Long id,
            @RequestBody LoanPlanRequest request) {
        return service.updateLoanPlan(id, request);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable Long id) {
        return service.deleteLoanPlan(id);
    }
}