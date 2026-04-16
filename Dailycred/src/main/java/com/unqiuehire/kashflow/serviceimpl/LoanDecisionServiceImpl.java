package com.unqiuehire.kashflow.serviceImpl;

import com.unqiuehire.kashflow.constant.ApiStatus;
import com.unqiuehire.kashflow.constant.ApplicationStatus;
import com.unqiuehire.kashflow.constant.LoanDecisionStatus;
import com.unqiuehire.kashflow.dto.requestdto.LoanDecisionRequestDTO;
import com.unqiuehire.kashflow.dto.responsedto.ApiResponse;
import com.unqiuehire.kashflow.dto.responsedto.LoanDecisionResponseDTO;
import com.unqiuehire.kashflow.entity.LoanApplication;
import com.unqiuehire.kashflow.entity.LoanDecision;
import com.unqiuehire.kashflow.exception.ResourceNotFoundException;
import com.unqiuehire.kashflow.repository.LenderRepository;
import com.unqiuehire.kashflow.repository.LoanApplicationRepository;
import com.unqiuehire.kashflow.repository.LoanDecisionRepository;
import com.unqiuehire.kashflow.repository.LoanRepository;
import com.unqiuehire.kashflow.service.LoanDecisionService;
import com.unqiuehire.kashflow.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoanDecisionServiceImpl implements LoanDecisionService {

    private final LoanDecisionRepository loanDecisionRepository;
    private final LoanApplicationRepository appRepo;
    private final LoanService loanService;
    private final LenderRepository lenderRepository;

    @Override
    public ApiResponse<LoanDecisionResponseDTO> approveLoan(Long appId, LoanDecisionRequestDTO dto) {

        LoanApplication app = appRepo.findById(appId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if (Boolean.TRUE.equals(app.getIsLoanCreated())) {
            return new ApiResponse<>(ApiStatus.FAILURE, "Loan already created", null);
        }

        LoanDecision decision = new LoanDecision();

        decision.setLoanApplication(app);

        decision.setLender(
                lenderRepository.findById(dto.getLenderId())
                        .orElseThrow(() -> new RuntimeException("Lender not found"))
        );

        decision.setDecision(LoanDecisionStatus.APPROVED);
        decision.setReason(dto.getReason());
        decision.setDecidedAt(LocalDateTime.now());

        loanDecisionRepository.save(decision);

        app.setStatus(ApplicationStatus.APPROVED);

        // AUTO LOAN CREATION
        loanService.createLoanFromApplication(app);

        System.out.println("Loan created for app: " + app.getApplicationId());

        app.setIsLoanCreated(true);

        appRepo.save(app);

        return new ApiResponse<>(ApiStatus.SUCCESS, "Approved", map(decision));
    }
    @Override
    public ApiResponse<LoanDecisionResponseDTO> rejectLoan(Long appId, LoanDecisionRequestDTO dto) {

        LoanApplication app = appRepo.findById(appId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        LoanDecision decision = new LoanDecision();
        decision.setLoanApplication(app);
        decision.setDecision(LoanDecisionStatus.REJECTED);
        decision.setReason(dto.getReason());
        decision.setDecidedAt(LocalDateTime.now());

        loanDecisionRepository.save(decision);

        app.setStatus(ApplicationStatus.REJECTED);
        app.setRejectionReason(dto.getReason());

        appRepo.save(app);

        return new ApiResponse<>(ApiStatus.SUCCESS, "Rejected", map(decision));
    }

    @Override
    public ApiResponse<LoanDecisionResponseDTO> getLoanDecisionByApplicationId(Long applicationId) {

        Optional<LoanDecision> optional = loanDecisionRepository.findByLoanApplication_ApplicationId(applicationId);

        if (optional.isPresent()) {
            return new ApiResponse<>(ApiStatus.SUCCESS, "Loan decision found", map(optional.get()));
        }

        return new ApiResponse<>(ApiStatus.FAILURE,
                "Loan decision not found application id: " + applicationId, null);
    }

    @Override
    public ApiResponse<LoanDecisionResponseDTO> updateLoanDecision(Long applicationId,
                                                                   LoanDecisionRequestDTO requestDTO) {

        Optional<LoanDecision> optional = loanDecisionRepository.findByLoanApplication_ApplicationId(applicationId);

        if (optional.isPresent()) {

            LoanDecision existing = optional.get();

            existing.setDecision(requestDTO.getDecision());
            existing.setReason(requestDTO.getReason());
            existing.setDecidedAt(LocalDateTime.now());

            LoanDecision updated = loanDecisionRepository.save(existing);

            return new ApiResponse<>(ApiStatus.SUCCESS,
                    "Loan decision updated successfully", map(updated));
        }

        return new ApiResponse<>(ApiStatus.FAILURE,
                "Loan decision not found application id: " + applicationId, null);
    }

    private LoanDecisionResponseDTO map(LoanDecision d) {

        LoanDecisionResponseDTO dto = new LoanDecisionResponseDTO();

        dto.setDecisionId(d.getDecisionId());
        dto.setLoanApplicationId(d.getLoanApplication().getApplicationId());
        dto.setDecision(d.getDecision());
        dto.setReason(d.getReason());
        dto.setDecidedAt(d.getDecidedAt());

        return dto;
    }
}