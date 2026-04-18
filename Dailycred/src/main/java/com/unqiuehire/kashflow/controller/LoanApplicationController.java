package com.unqiuehire.kashflow.controller;

import com.unqiuehire.kashflow.dto.requestdto.LoanApplicationRequestDto;
import com.unqiuehire.kashflow.dto.responsedto.ApiResponse;
import com.unqiuehire.kashflow.dto.responsedto.LoanApplicationResponseDto;
import com.unqiuehire.kashflow.service.LoanApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loan-applications")
@RequiredArgsConstructor
public class LoanApplicationController {

    private final LoanApplicationService service;

    @PostMapping
    public ApiResponse<LoanApplicationResponseDto> create(@RequestBody LoanApplicationRequestDto dto) {
        return service.createApplication(dto);
    }

    @GetMapping("/{id}")
    public ApiResponse<LoanApplicationResponseDto> getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping("/borrower/{borrowerId}")
    public ApiResponse<List<LoanApplicationResponseDto>> getByBorrower(@PathVariable Long borrowerId) {
        return service.getByBorrower(borrowerId);
    }

    @GetMapping("/lender/{lenderId}")
    public ApiResponse<List<LoanApplicationResponseDto>> getByLender(@PathVariable Long lenderId) {
        return service.getByLender(lenderId);
    }

    @PutMapping("/cancel/{id}")
    public ApiResponse<String> cancel(@PathVariable Long id) {
        return service.cancelApplication(id);
    }
}