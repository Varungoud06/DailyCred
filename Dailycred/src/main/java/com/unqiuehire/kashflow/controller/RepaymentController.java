package com.unqiuehire.kashflow.controller;

import com.unqiuehire.kashflow.dto.requestdto.RepaymentRequestDTO;
import com.unqiuehire.kashflow.dto.responsedto.RepaymentResponseDTO;
import com.unqiuehire.kashflow.service.RepaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/repayments")
@RequiredArgsConstructor
public class RepaymentController {

    private final RepaymentService repaymentService;

    @PostMapping
    public RepaymentResponseDTO makePayment(@RequestBody RepaymentRequestDTO request) {
        return repaymentService.makePayment(request);
    }

    @GetMapping("/loan/{loanId}")
    public List<RepaymentResponseDTO> getByLoan(@PathVariable Long loanId) {
        return repaymentService.getByLoan(loanId);
    }

    @GetMapping("/application/{loanApplicationId}")
    public List<RepaymentResponseDTO> getByLoanApplication(@PathVariable Long loanApplicationId) {
        return repaymentService.getByLoanApplication(loanApplicationId);
    }
}