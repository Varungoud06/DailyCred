package com.unqiuehire.kashflow.serviceimpl;

import com.unqiuehire.kashflow.constant.ApiStatus;
import com.unqiuehire.kashflow.constant.LoanDecisionStatus;
import com.unqiuehire.kashflow.dto.requestdto.LoanDecisionRequestDTO;
import com.unqiuehire.kashflow.dto.responsedto.ApiResponse;
import com.unqiuehire.kashflow.dto.responsedto.LoanDecisionResponseDTO;
import com.unqiuehire.kashflow.entity.LoanDecision;
import com.unqiuehire.kashflow.repository.LenderRepository;
import com.unqiuehire.kashflow.repository.LoanApplicationRepository;
import com.unqiuehire.kashflow.repository.LoanDecisionRepository;
import com.unqiuehire.kashflow.repository.LoanRepository;
import com.unqiuehire.kashflow.service.LoanDecisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class LoanDecisionServiceImpl implements LoanDecisionService
{

    @Autowired
    private LoanDecisionRepository loanDecisionRepository;

//    @Autowired
//    private LoanApplicationRepository loanApplicationRepository;
//
//    @Autowired
//    private LoanRepository loanRepository;
//
//    @Autowired
//    private LenderRepository lenderRepository;

    @Override
    public ApiResponse<LoanDecisionResponseDTO> approveLoan(Long applicationId, LoanDecisionRequestDTO requestDTO)
    {
        if (loanDecisionRepository.existsByLoanApplicationId(applicationId))
        {
            return new ApiResponse<>(
                    ApiStatus.FAILURE,
                    "Decision already exists for application id: " + applicationId,
                    null
            );
        }
        LoanDecision loanDecision = new LoanDecision();
        loanDecision.setLoanApplicationId(applicationId);
        loanDecision.setLoanId(requestDTO.getLoanId());
        loanDecision.setLenderId(requestDTO.getLenderId());
        loanDecision.setDecision(LoanDecisionStatus.APPROVED);
        loanDecision.setReason(requestDTO.getReason());
        loanDecision.setDecidedAt(LocalDateTime.now());

        LoanDecision savedDecision = loanDecisionRepository.save(loanDecision);

        return new ApiResponse<>(
                ApiStatus.SUCCESS,
                "Loan approved successfully",
                mapToResponseDTO(savedDecision)
        );
    }

    @Override
    public ApiResponse<LoanDecisionResponseDTO> rejectLoan(Long applicationId, LoanDecisionRequestDTO requestDTO)
    {
        if (loanDecisionRepository.existsByLoanApplicationId(applicationId))
        {
            return new ApiResponse<>(
                    ApiStatus.FAILURE,
                    "Decision already exists for application id: " + applicationId,
                    null
            );
        }
        LoanDecision loanDecision = new LoanDecision();
        loanDecision.setLoanApplicationId(applicationId);
        loanDecision.setLoanId(requestDTO.getLoanId());
        loanDecision.setLenderId(requestDTO.getLenderId());
        loanDecision.setDecision(LoanDecisionStatus.REJECTED);
        loanDecision.setReason(requestDTO.getReason());
        loanDecision.setDecidedAt(LocalDateTime.now());

        LoanDecision savedDecision = loanDecisionRepository.save(loanDecision);

        return new ApiResponse<>(
                ApiStatus.SUCCESS,
                "Loan rejected successfully",
                mapToResponseDTO(savedDecision)
        );
    }
    @Override
    public ApiResponse<LoanDecisionResponseDTO> getLoanDecisionByApplicationId(Long applicationId)
    {
        Optional<LoanDecision> optionalLoanDecision = loanDecisionRepository.findByLoanApplicationId(applicationId);

        if (optionalLoanDecision.isPresent())
        {
            return new ApiResponse<>(
                    ApiStatus.SUCCESS,
                    "Loan decision found",
                    mapToResponseDTO(optionalLoanDecision.get())
            );
        }
        return new ApiResponse<>(
                ApiStatus.FAILURE,
                "Loan decision not found application id: " + applicationId,
                null
        );
    }
    @Override
    public ApiResponse<LoanDecisionResponseDTO> updateLoanDecision(Long applicationId,LoanDecisionRequestDTO requestDTO)
    {
        Optional<LoanDecision> optionalLoanDecision = loanDecisionRepository.findByLoanApplicationId(applicationId);

        if(optionalLoanDecision.isPresent())
        {
            LoanDecision existingDecision = optionalLoanDecision.get();

            existingDecision.setLoanId(requestDTO.getLoanId());
            existingDecision.setLenderId(requestDTO.getLenderId());
            existingDecision.setDecision(requestDTO.getDecision());
            existingDecision.setReason(requestDTO.getReason());
            existingDecision.setDecidedAt(LocalDateTime.now());

            LoanDecision updatedDecision = loanDecisionRepository.save(existingDecision);

            return new ApiResponse<>(
                    ApiStatus.SUCCESS,
                    "Loan decision updated successfully",
                    mapToResponseDTO(updatedDecision)
            );
        }

        return new ApiResponse<>(
                ApiStatus.FAILURE,
                "Loan decision not found application id: " + applicationId,
                null
        );
    }

    private LoanDecisionResponseDTO mapToResponseDTO(LoanDecision loanDecision)
    {
        LoanDecisionResponseDTO responseDTO = new LoanDecisionResponseDTO();
        responseDTO.setDecisionId(loanDecision.getDecisionId());
        responseDTO.setLoanApplicationId(loanDecision.getLoanApplicationId());
        responseDTO.setLoanId(loanDecision.getLoanId());
        responseDTO.setLenderId(loanDecision.getLenderId());
        responseDTO.setDecision(loanDecision.getDecision());
        responseDTO.setReason(loanDecision.getReason());
        responseDTO.setDecidedAt(loanDecision.getDecidedAt());
        return responseDTO;
    }
}
