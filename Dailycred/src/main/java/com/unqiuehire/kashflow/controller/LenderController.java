package com.unqiuehire.kashflow.controller;

import com.unqiuehire.kashflow.dto.requestdto.LenderRequestDto;
import com.unqiuehire.kashflow.dto.requestdto.LoanPlanRequest;
import com.unqiuehire.kashflow.dto.responsedto.ApiResponse;
import com.unqiuehire.kashflow.dto.responsedto.LenderResponseDto;
import com.unqiuehire.kashflow.dto.responsedto.LoanPlanResponseDto;
import com.unqiuehire.kashflow.service.LenderService;
import com.unqiuehire.kashflow.service.LoanPlanService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lenders")
@RequiredArgsConstructor
public class LenderController {

    private final LenderService lenderService;
    private final LoanPlanService loanPlanService;

    //  CREATE LOAN PLAN UNDER LENDER
    @PostMapping("/{lenderId}/loan-plans")
    public ApiResponse<LoanPlanResponseDto> createLoanPlan(
            @PathVariable Long lenderId,
            @RequestBody LoanPlanRequest request) {

        return loanPlanService.createLoanPlan(lenderId, request);
    }

    // CREATE LENDER
    @PostMapping
    public ApiResponse<LenderResponseDto> createLender(@RequestBody LenderRequestDto lenderRequestDto) {
        return lenderService.createLender(lenderRequestDto);
    }

    // GET BY ID
    @GetMapping("/{lenderId}")
    public ApiResponse<LenderResponseDto> getLenderById(@PathVariable Long lenderId) {
        return lenderService.getLenderById(lenderId);
    }

    //  GET ALL
    @GetMapping
    public ApiResponse<List<LenderResponseDto>> getAllLenders() {
        return lenderService.getAllLenders();
    }

    //  UPDATE
    @PutMapping("/{lenderId}")
    public ApiResponse<LenderResponseDto> updateLender(
            @PathVariable Long lenderId,
            @RequestBody LenderRequestDto lenderRequestDto) {

        return lenderService.updateLender(lenderId, lenderRequestDto);
    }

    //  DELETE
    @DeleteMapping("/{lenderId}")
    public ApiResponse<String> deleteLender(@PathVariable Long lenderId) {
        return lenderService.deleteLender(lenderId);
    }
}