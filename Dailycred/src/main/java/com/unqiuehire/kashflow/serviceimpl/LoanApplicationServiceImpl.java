//package com.unqiuehire.kashflow.serviceimpl;
//
//import com.unqiuehire.kashflow.constant.ApiStatus;
//import com.unqiuehire.kashflow.constant.ApplicationStatus;
//import com.unqiuehire.kashflow.dto.requestdto.LoanApplicationRequestDto;
//import com.unqiuehire.kashflow.dto.responsedto.ApiResponse;
//import com.unqiuehire.kashflow.dto.responsedto.LoanApplicationResponseDto;
//import com.unqiuehire.kashflow.entity.LoanApplication;
//import com.unqiuehire.kashflow.entity.LoanPlan;
//import com.unqiuehire.kashflow.exception.ResourceNotFoundException;
//import com.unqiuehire.kashflow.repository.LoanApplicationRepository;
//import com.unqiuehire.kashflow.repository.LoanPlanRepository;
//import com.unqiuehire.kashflow.service.LoanApplicationService;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class LoanApplicationServiceImpl implements LoanApplicationService {
//
//    private final LoanApplicationRepository repository;
//    private final LoanPlanRepository loanPlanRepository;
//
//    @Override
//    public ApiResponse<LoanApplicationResponseDto> createApplication(LoanApplicationRequestDto dto) {
//
//        LoanPlan plan = loanPlanRepository.findById(dto.getPlanId())
//                .orElseThrow(() -> new ResourceNotFoundException("Loan Plan not found"));
//
//        LoanApplication app = new LoanApplication();
//
//        app.setBorrowerId(dto.getBorrowerId());
//        app.setLoanPlan(plan); //  RELATION
//        app.setLoanAmount(dto.getLoanAmount());
//
//        LoanApplication saved = repository.save(app);
//
//        return new ApiResponse<>(
//                ApiStatus.SUCCESS,
//                "Loan application created",
//                mapToDto(saved)
//        );
//    }
//
//    @Override
//    public ApiResponse<LoanApplicationResponseDto> getById(Long id) {
//
//        LoanApplication app = repository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
//
//        return new ApiResponse<>(ApiStatus.SUCCESS, "Fetched", mapToDto(app));
//    }
//
//    @Override
//    public ApiResponse<List<LoanApplicationResponseDto>> getByBorrower(Long borrowerId) {
//
//        List<LoanApplicationResponseDto> list = repository.findByBorrowerId(borrowerId)
//                .stream()
//                .map(this::mapToDto)
//                .toList();
//
//        return new ApiResponse<>(ApiStatus.SUCCESS, "Fetched", list);
//    }
//
//    @Override
//    public ApiResponse<List<LoanApplicationResponseDto>> getByLender(Long lenderId) {
//
//        List<LoanApplicationResponseDto> list =
//                repository.findByLoanPlan_Lender_LenderId(lenderId)
//                        .stream()
//                        .map(this::mapToDto)
//                        .toList();
//
//        return new ApiResponse<>(ApiStatus.SUCCESS, "Fetched", list);
//    }
//
//    @Override
//    public ApiResponse<String> cancelApplication(Long id) {
//
//        LoanApplication app = repository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
//
//        app.setStatus(ApplicationStatus.CANCELLED);
//        repository.save(app);
//
//        return new ApiResponse<>(ApiStatus.SUCCESS, "Cancelled", "ID: " + id);
//    }
//
//    private LoanApplicationResponseDto mapToDto(LoanApplication app) {
//
//        LoanApplicationResponseDto dto = new LoanApplicationResponseDto();
//
//        dto.setApplicationId(app.getApplicationId());
//        dto.setBorrowerId(app.getBorrowerId());
//        dto.setPlanId(app.getLoanPlan().getId());
//
//        //  FROM RELATION
//        dto.setLenderId(app.getLoanPlan().getLender().getLenderId());
//
//        dto.setLoanAmount(app.getLoanAmount());
//        dto.setStatus(app.getStatus().name());
//        dto.setApplicationDate(app.getApplicationDate().toString());
//
//        return dto;
//    }
//}

package com.unqiuehire.kashflow.serviceimpl;

import com.unqiuehire.kashflow.constant.ApiStatus;
import com.unqiuehire.kashflow.constant.ApplicationStatus;
import com.unqiuehire.kashflow.constant.LoanPlanStatus;
import com.unqiuehire.kashflow.dto.requestdto.LoanApplicationRequestDto;
import com.unqiuehire.kashflow.dto.responsedto.ApiResponse;
import com.unqiuehire.kashflow.dto.responsedto.LoanApplicationResponseDto;
import com.unqiuehire.kashflow.entity.Borrower;
import com.unqiuehire.kashflow.entity.LoanApplication;
import com.unqiuehire.kashflow.entity.LoanPlan;
import com.unqiuehire.kashflow.exception.ResourceNotFoundException;
import com.unqiuehire.kashflow.repository.BorrowerRepository;
import com.unqiuehire.kashflow.repository.LoanApplicationRepository;
import com.unqiuehire.kashflow.repository.LoanPlanRepository;
import com.unqiuehire.kashflow.service.LoanApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanApplicationServiceImpl implements LoanApplicationService {

    private final LoanApplicationRepository repository;
    private final LoanPlanRepository loanPlanRepository;
    private final BorrowerRepository borrowerRepository;

    @Override
    public ApiResponse<LoanApplicationResponseDto> createApplication(LoanApplicationRequestDto dto) {

        if (dto == null) {
            return new ApiResponse<>(ApiStatus.FAILURE, "Loan application request cannot be null", null);
        }

        if (dto.getBorrowerId() == null) {
            return new ApiResponse<>(ApiStatus.FAILURE, "Borrower id is required", null);
        }

        if (dto.getPlanId() == null) {
            return new ApiResponse<>(ApiStatus.FAILURE, "Plan id is required", null);
        }

        if (dto.getLoanAmount() == null || dto.getLoanAmount() <= 0) {
            return new ApiResponse<>(ApiStatus.FAILURE, "Loan amount must be greater than 0", null);
        }

        Borrower borrower = borrowerRepository.findById(dto.getBorrowerId())
                .orElseThrow(() -> new ResourceNotFoundException("Borrower not found"));

        LoanPlan plan = loanPlanRepository.findById(dto.getPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Loan Plan not found"));

        if (Boolean.FALSE.equals(borrower.getIsActive())) {
            return new ApiResponse<>(ApiStatus.FAILURE, "Inactive borrower cannot apply for loan", null);
        }

        if (plan.getStatus() != LoanPlanStatus.ACTIVE) {
            return new ApiResponse<>(ApiStatus.FAILURE, "This loan plan is not active", null);
        }

        if (dto.getLoanAmount() > plan.getAmount()) {
            return new ApiResponse<>(ApiStatus.FAILURE, "Requested loan amount exceeds loan plan amount", null);
        }

        if (borrower.getCibil() == null) {
            return new ApiResponse<>(ApiStatus.FAILURE, "Borrower CIBIL is missing", null);
        }

        if (borrower.getCibil() < plan.getMinCibil()) {
            return new ApiResponse<>(ApiStatus.FAILURE, "Borrower does not meet minimum CIBIL for this plan", null);
        }

        boolean alreadyPending = repository.existsByBorrower_BorrowerIdAndLoanPlan_IdAndStatus(
                borrower.getBorrowerId(),
                plan.getId(),
                ApplicationStatus.PENDING
        );

        if (alreadyPending) {
            return new ApiResponse<>(ApiStatus.FAILURE, "Borrower already has a pending application for this plan", null);
        }

        LoanApplication app = new LoanApplication();
        app.setBorrower(borrower);
        app.setLoanPlan(plan);
        app.setLoanAmount(dto.getLoanAmount());
        app.setStatus(ApplicationStatus.PENDING);
        app.setIsLoanCreated(false);

        LoanApplication saved = repository.save(app);

        return new ApiResponse<>(
                ApiStatus.SUCCESS,
                "Loan application created successfully",
                mapToDto(saved)
        );
    }

    @Override
    public ApiResponse<LoanApplicationResponseDto> getById(Long id) {
        LoanApplication app = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        return new ApiResponse<>(ApiStatus.SUCCESS, "Loan application fetched successfully", mapToDto(app));
    }

    @Override
    public ApiResponse<List<LoanApplicationResponseDto>> getByBorrower(Long borrowerId) {
        List<LoanApplicationResponseDto> list = repository.findByBorrower_BorrowerId(borrowerId)
                .stream()
                .map(this::mapToDto)
                .toList();

        return new ApiResponse<>(ApiStatus.SUCCESS, "Borrower loan applications fetched successfully", list);
    }

    @Override
    public ApiResponse<List<LoanApplicationResponseDto>> getByLender(Long lenderId) {
        List<LoanApplicationResponseDto> list = repository.findByLoanPlan_Lender_LenderId(lenderId)
                .stream()
                .map(this::mapToDto)
                .toList();

        return new ApiResponse<>(ApiStatus.SUCCESS, "Lender loan applications fetched successfully", list);
    }

    @Override
    public ApiResponse<String> cancelApplication(Long id) {
        LoanApplication app = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        if (app.getStatus() == ApplicationStatus.APPROVED) {
            return new ApiResponse<>(ApiStatus.FAILURE, "Approved application cannot be cancelled", null);
        }

        if (app.getStatus() == ApplicationStatus.REJECTED) {
            return new ApiResponse<>(ApiStatus.FAILURE, "Rejected application cannot be cancelled", null);
        }

        if (app.getStatus() == ApplicationStatus.CANCELLED) {
            return new ApiResponse<>(ApiStatus.FAILURE, "Application is already cancelled", null);
        }

        app.setStatus(ApplicationStatus.CANCELLED);
        repository.save(app);

        return new ApiResponse<>(ApiStatus.SUCCESS, "Loan application cancelled successfully", "ID: " + id);
    }

    private LoanApplicationResponseDto mapToDto(LoanApplication app) {
        LoanApplicationResponseDto dto = new LoanApplicationResponseDto();

        dto.setApplicationId(app.getApplicationId());
        dto.setBorrowerId(app.getBorrower().getBorrowerId());
        dto.setPlanId(app.getLoanPlan().getId());
        dto.setLenderId(app.getLoanPlan().getLender().getLenderId());
        dto.setLoanAmount(app.getLoanAmount());
        dto.setStatus(app.getStatus().name());
        dto.setApplicationDate(app.getApplicationDate() != null ? app.getApplicationDate().toString() : null);
        dto.setRejectionReason(app.getRejectionReason());
        dto.setIsLoanCreated(app.getIsLoanCreated());

        return dto;
    }
}