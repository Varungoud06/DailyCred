package com.unqiuehire.kashflow.serviceimpl;
import com.unqiuehire.kashflow.constant.ApiStatus;
import com.unqiuehire.kashflow.constant.ApplicationStatus;
import com.unqiuehire.kashflow.dto.requestdto.LoanApplicationApprovalRequestDto;
import com.unqiuehire.kashflow.dto.requestdto.LoanApplicationRequestDto;
import com.unqiuehire.kashflow.dto.requestdto.LoanRequestDto;
import com.unqiuehire.kashflow.dto.responsedto.ApiResponse;
import com.unqiuehire.kashflow.dto.responsedto.LoanApplicationResponseDto;
import com.unqiuehire.kashflow.entity.Borrower;
import com.unqiuehire.kashflow.entity.Lender;
import com.unqiuehire.kashflow.entity.LoanApplication;
import com.unqiuehire.kashflow.entity.LoanPlan;
import com.unqiuehire.kashflow.repository.BorrowerRepository;
import com.unqiuehire.kashflow.repository.LenderRepository;
import com.unqiuehire.kashflow.repository.LoanApplicationRepository;
import com.unqiuehire.kashflow.repository.LoanPlanRepository;
import com.unqiuehire.kashflow.service.LoanApplicationService;
import com.unqiuehire.kashflow.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LoanApplicationServiceImpl implements LoanApplicationService {

    @Autowired
    private LoanApplicationRepository loanApplicationRepository;

    @Autowired
    private LoanPlanRepository loanPlanRepository;

    @Autowired
    private BorrowerRepository borrowerRepository;
    @Autowired
    private LenderRepository lenderRepository;
    @Autowired
    private LoanService loanService;

    @Override
    public ApiResponse<LoanApplicationResponseDto> applyLoan(Long borrowerId, Long lenderId,Long planId, LoanApplicationRequestDto requestDto) {

        Optional<Borrower> borrowerOptional = borrowerRepository.findById(borrowerId);
        if (borrowerOptional.isEmpty()) {
            return new ApiResponse<>(ApiStatus.FAILURE, "Borrower not found", null);
        }

        Optional<Lender> lenderOptional = lenderRepository.findById(lenderId);
        if (lenderOptional.isEmpty()) {
            return new ApiResponse<>(ApiStatus.FAILURE, "Lender not found", null);
        }

        Optional<LoanPlan> planOptional = loanPlanRepository.findById(planId);
        if (planOptional.isEmpty()) {
            return new ApiResponse<>(ApiStatus.FAILURE, "Loan plan not found", null);
        }

        Borrower borrower = borrowerOptional.get();
        Lender lender = lenderOptional.get();
        LoanPlan loanPlan = planOptional.get();

        // Lender validation
        if (!loanPlan.getLender().getLenderId().equals(lenderId)) {
            return new ApiResponse<>(ApiStatus.FAILURE, "Lender mismatch with loan plan", null);
        }

        // Collateral required
        if (requestDto.getCollateral() == null || requestDto.getCollateral().trim().isEmpty()) {
            return new ApiResponse<>(ApiStatus.FAILURE, "Collateral is required", null);
        }

        //If educated → certificates required
        if (Boolean.TRUE.equals(requestDto.getIsEducated())) {
            if (requestDto.getCertificates() == null || requestDto.getCertificates().trim().isEmpty()) {
                return new ApiResponse<>(ApiStatus.FAILURE, "Certificates required for educated borrower", null);
            }
        }

        // Age validation
        if (loanPlan.getMinAge() == null || loanPlan.getMaxAge() == null) {
            return new ApiResponse<>(ApiStatus.FAILURE, "Loan plan age not configured", null);
        }

        if (requestDto.getAge() < loanPlan.getMinAge() || requestDto.getAge() > loanPlan.getMaxAge()) {
            return new ApiResponse<>(ApiStatus.FAILURE, "Age not eligible", null);
        }

        // Income validation
        if (requestDto.getMonthlyIncome() < loanPlan.getMinMonthlyIncome()) {
            return new ApiResponse<>(ApiStatus.FAILURE, "Income too low", null);
        }

        // CIBIL
        if (borrower.getCibil() < loanPlan.getMinCibil()) {
            return new ApiResponse<>(ApiStatus.FAILURE, "CIBIL not eligible", null);
        }

        //PinCode validation
        if (!requestDto.getPinCode().equals(loanPlan.getServicePinCode())) {
            return new ApiResponse<>(ApiStatus.FAILURE, "Service area mismatch", null);
        }

        // Create Application
        LoanApplication application = new LoanApplication();
        application.setBorrower(borrower);
        application.setLender(lender);
        application.setLoanPlan(loanPlan);
        application.setLoanAmount(requestDto.getLoanAmount());
        application.setAge(requestDto.getAge());
        application.setMonthlyIncome(requestDto.getMonthlyIncome());
        application.setEmploymentType(requestDto.getEmployeeType());
        application.setPinCode(requestDto.getPinCode());
        application.setIsEducated(requestDto.getIsEducated());
        application.setCertificates(requestDto.getCertificates());
        application.setCollateral(requestDto.getCollateral());
        application.setStatus(ApplicationStatus.PENDING);
        application.setAppliedAt(LocalDateTime.now());

        LoanApplication saved = loanApplicationRepository.save(application);

        return new ApiResponse<>(
                ApiStatus.SUCCESS,
                "Loan applied successfully",
                mapToResponse(saved)
        );
    }

    @Override
    public ApiResponse<LoanApplicationResponseDto> updateLoanDecision(
            Long applicationId,
            Long lenderId,
            LoanApplicationApprovalRequestDto requestDto) {

        Optional<LoanApplication> optional = loanApplicationRepository.findById(applicationId);

        if (optional.isEmpty()) {
            return new ApiResponse<>(ApiStatus.FAILURE, "Loan application not found", null);
        }

        LoanApplication application = optional.get();

        // LENDER VALIDATION
        if (!application.getLender().getLenderId().equals(lenderId)) {
            return new ApiResponse<>(ApiStatus.FAILURE, "Unauthorized: Lender mismatch", null);
        }

        // STATUS VALIDATION
        if (requestDto.getApplicationStatus() == null) {
            return new ApiResponse<>(ApiStatus.FAILURE, "Application status is required", null);
        }

        // ALREADY PROCESSED CHECK
        if (application.getStatus() != ApplicationStatus.PENDING) {
            return new ApiResponse<>(ApiStatus.FAILURE, "Application already processed", null);
        }

        if (requestDto.getApplicationStatus() == ApplicationStatus.PENDING) {
            return new ApiResponse<>(ApiStatus.FAILURE, "Cannot set status back to PENDING", null);
        }

        // UPDATE STATUS
        application.setStatus(requestDto.getApplicationStatus());

        // SAVE REMARKS
        if (requestDto.getRemarks() != null) {
            application.setRejectionReason(requestDto.getRemarks());
        }

        application.setUpdatedAt(java.time.LocalDateTime.now());

        if (requestDto.getApplicationStatus() == ApplicationStatus.APPROVED) {

            if (!application.getIsLoanCreated()) {

                LoanRequestDto loanRequest = new LoanRequestDto();

                loanRequest.setLoanApplicationId(application.getApplicationId());
                loanRequest.setBorrowerId(application.getBorrower().getBorrowerId());
                loanRequest.setLenderId(application.getLender().getLenderId());
                loanRequest.setPlanId(application.getLoanPlan().getId());

                loanRequest.setSanctionedAmount(application.getLoanAmount());
                loanRequest.setTotalAmount(application.getLoanAmount());

                loanRequest.setTenureDays(application.getLoanPlan().getPlanDuration());
                loanRequest.setInterestPerDay(application.getLoanPlan().getInterestPerDay());
                loanRequest.setPenaltyAmount(application.getLoanPlan().getPenaltyAmount());

                loanRequest.setStartDate(LocalDate.now()); // 🔥 repayment starts here

                loanService.createLoan(loanRequest); // 🔥 creates loan + wallet flow

                application.setIsLoanCreated(true);
            }
        }

        LoanApplication updated = loanApplicationRepository.save(application);

        return new ApiResponse<>(
                ApiStatus.SUCCESS,
                "Loan application decision updated successfully",
                mapToResponse(updated)
        );
    }

    @Override
    public ApiResponse<LoanApplicationResponseDto> getApplicationById(Long applicationId) {

        Optional<LoanApplication> optional = loanApplicationRepository.findById(applicationId);

        if (optional.isEmpty()) {
            return new ApiResponse<>(
                    ApiStatus.FAILURE,
                    "Loan application not found",
                    null
            );
        }

        LoanApplication application = optional.get();

        return new ApiResponse<>(
                ApiStatus.SUCCESS,
                "Loan application fetched successfully",
                mapToResponse(application)
        );
    }
    @Override
    public ApiResponse<List<LoanApplicationResponseDto>> getApplicationsByLenderId(Long lenderId) {

        List<LoanApplicationResponseDto> list = loanApplicationRepository
                .findByLender_LenderId(lenderId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return new ApiResponse<>(
                ApiStatus.SUCCESS,
                "Lender applications fetched successfully",
                list
        );
    }
    @Override
    public ApiResponse<List<LoanApplicationResponseDto>> getApplicationsByBorrowerId(Long borrowerId) {

        List<LoanApplicationResponseDto> list = loanApplicationRepository
                .findByBorrower_BorrowerId(borrowerId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return new ApiResponse<>(
                ApiStatus.SUCCESS,
                "Borrower applications fetched successfully",
                list
        );
    }
//
    private LoanApplicationResponseDto mapToResponse(LoanApplication application) {
        LoanApplicationResponseDto dto = new LoanApplicationResponseDto();
        dto.setApplicationId(application.getApplicationId());
        dto.setBorrowerId(application.getBorrower().getBorrowerId());
        dto.setLenderId(application.getLender().getLenderId());
        dto.setPlanId(application.getLoanPlan().getId());
        dto.setLoanAmount(application.getLoanAmount());
        dto.setAge(application.getAge());
        dto.setMonthlyIncome(application.getMonthlyIncome());
        dto.setEmployeeType(application.getEmploymentType());
        dto.setPinCode(application.getPinCode());
        dto.setIsEducated(application.getIsEducated());
        dto.setCertificates(application.getCertificates());
        dto.setCollateral(application.getCollateral());
        dto.setApplicationStatus(application.getStatus());
//        dto.setRemarks(application.get);
        dto.setAppliedAt(application.getAppliedAt());
        return dto;
    }
}