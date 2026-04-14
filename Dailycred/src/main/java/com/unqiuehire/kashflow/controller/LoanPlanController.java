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

    @PostMapping
    public ApiResponse<LoanPlanResponseDto> create(@RequestBody LoanPlanRequest request) {
        return service.createLoanPlan(request);
    }

    @GetMapping("/{id}")
    public ApiResponse<LoanPlanResponseDto> getById(@PathVariable Long id) {
        return service.getLoanPlanById(id);
    }

    @GetMapping
    public ApiResponse<List<LoanPlanResponseDto>> getAll() {
        return service.getAllLoanPlans();
    }

    @PutMapping("/{id}")
    public ApiResponse<LoanPlanResponseDto> update(
            @PathVariable Long id,
            @RequestBody LoanPlanRequest request) {
        return service.updateLoanPlan(id, request);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable Long id) {
        return service.deleteLoanPlan(id);
    }
}