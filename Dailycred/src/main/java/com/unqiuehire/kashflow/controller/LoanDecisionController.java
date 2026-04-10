package com.unqiuehire.kashflow.controller;


import com.unqiuehire.kashflow.dto.requestdto.LoanDecisionRequestDTO;
import com.unqiuehire.kashflow.dto.responsedto.ApiResponse;
import com.unqiuehire.kashflow.dto.responsedto.LoanDecisionResponseDTO;
import com.unqiuehire.kashflow.service.LoanDecisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/loan-decision")
@CrossOrigin("*")
public class LoanDecisionController {

    @Autowired
    private LoanDecisionService loanDecisionService;

    @PostMapping("/approve/{applicationId}")
    public ApiResponse<LoanDecisionResponseDTO> approveLoan(
            @PathVariable Long applicationId,
            @RequestBody LoanDecisionRequestDTO requestDTO)
    {
        return loanDecisionService.approveLoan(applicationId, requestDTO);
    }

    @PostMapping("/reject/{applicationId}")
    public ApiResponse<LoanDecisionResponseDTO> rejectLoan(
            @PathVariable Long applicationId,
            @RequestBody LoanDecisionRequestDTO requestDTO)
    {
        return loanDecisionService.rejectLoan(applicationId, requestDTO);
    }

    @GetMapping("/{applicationId}")
    public  ApiResponse<LoanDecisionResponseDTO> getLoanDecisionByApplicationId(
            @PathVariable Long applicationId)
    {
        return loanDecisionService.getLoanDecisionByApplicationId(applicationId);
    }
    @PutMapping("/{applicationId}")
    public ApiResponse<LoanDecisionResponseDTO> updateLoanDecision(
            @PathVariable Long applicationId,
            @RequestBody LoanDecisionRequestDTO requestDTO)
    {
        return loanDecisionService.updateLoanDecision(applicationId, requestDTO);
    }
}
