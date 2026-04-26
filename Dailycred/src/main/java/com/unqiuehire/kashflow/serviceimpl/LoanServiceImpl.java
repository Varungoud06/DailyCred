package com.unqiuehire.kashflow.serviceimpl;

import com.unqiuehire.kashflow.constant.ApiStatus;
import com.unqiuehire.kashflow.constant.WalletOwnerType;
import com.unqiuehire.kashflow.constant.WalletTransactionType;
import com.unqiuehire.kashflow.dto.requestdto.LoanRequestDto;
import com.unqiuehire.kashflow.dto.responsedto.ApiResponse;
import com.unqiuehire.kashflow.dto.responsedto.LoanResponseDto;
import com.unqiuehire.kashflow.entity.Borrower;
import com.unqiuehire.kashflow.entity.Loan;
import com.unqiuehire.kashflow.entity.LoanPlan;
import com.unqiuehire.kashflow.repository.BorrowerRepository;
import com.unqiuehire.kashflow.repository.LoanPlanRepository;
import com.unqiuehire.kashflow.repository.LoanRepository;
import com.unqiuehire.kashflow.service.LoanCalculationService;
import com.unqiuehire.kashflow.service.LoanService;
import com.unqiuehire.kashflow.service.WalletService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;
    private final BorrowerRepository borrowerRepository;
    private final LoanCalculationService loanCalculationService;
    private final WalletService walletService;
    private final LoanPlanRepository loanPlanRepository;


    public LoanServiceImpl(
            LoanRepository loanRepository,
            BorrowerRepository borrowerRepository,
            LoanCalculationService loanCalculationService,
            WalletService walletService, LoanPlanRepository loanPlanRepository
    ) {
        this.loanRepository = loanRepository;
        this.borrowerRepository = borrowerRepository;
        this.loanCalculationService = loanCalculationService;
        this.walletService = walletService;
        this.loanPlanRepository = loanPlanRepository;
    }

    @Override
    public ApiResponse<LoanResponseDto> createLoan(LoanRequestDto dto) {

        if (loanRepository.existsByLoanApplicationId(dto.getLoanApplicationId())) {
            return new ApiResponse<>(ApiStatus.FAILURE, "Loan already exists for this loan application", null);
        }

        if (dto.getSanctionedAmount() == null || dto.getTenureDays() == null || dto.getTenureDays() <= 0) {
            return new ApiResponse<>(ApiStatus.FAILURE, "Sanctioned amount and valid tenure days are required", null);
        }

        BigDecimal sanctionedAmount = BigDecimal.valueOf(dto.getSanctionedAmount());
        //BigDecimal interestPercent = loanCalculationService.getInterestDeductionPercent(sanctionedAmount);
        BigDecimal interestPercent;
        int tenureDays;

        if (Boolean.TRUE.equals(dto.getIsCustomLoan())) {


            interestPercent = BigDecimal.valueOf(dto.getCustomInterestPercent());
            tenureDays = dto.getCustomTenureDays();

        } else if (dto.getPlanId() != null) {


            LoanPlan plan = loanPlanRepository.findById(dto.getPlanId())
                    .orElseThrow(() -> new RuntimeException("Loan Plan not found"));

            interestPercent = BigDecimal.valueOf(plan.getInterestPerDay());
            tenureDays = plan.getPlanDuration();

        } else {

            interestPercent = loanCalculationService.getInterestDeductionPercent(
                    BigDecimal.valueOf(dto.getSanctionedAmount())
            );

            tenureDays = dto.getTenureDays();
        }
        BigDecimal upfrontInterest = loanCalculationService.calculateUpfrontInterest(sanctionedAmount);
        BigDecimal disbursedAmount = loanCalculationService.calculateDisbursedAmount(sanctionedAmount);
        BigDecimal totalRepayableAmount = sanctionedAmount;
        BigDecimal dailyDue = loanCalculationService.calculateDailyDue(totalRepayableAmount, dto.getTenureDays());

        Loan loan = new Loan();
        loan.setLoanApplicationId(dto.getLoanApplicationId());
        loan.setBorrowerId(dto.getBorrowerId());
        loan.setLenderId(dto.getLenderId());
        loan.setPlanId(dto.getPlanId());
        loan.setTotalAmount(dto.getTotalAmount());
        loan.setSanctionedAmount(dto.getSanctionedAmount());
        loan.setInterestPerDay(dto.getInterestPerDay() == null ? 0.0 : dto.getInterestPerDay());
        loan.setPenaltyAmount(dto.getPenaltyAmount() == null ? 0.0 : dto.getPenaltyAmount());
        loan.setTenureDays(dto.getTenureDays());
        loan.setStartDate(dto.getStartDate() == null ? LocalDate.now() : dto.getStartDate());
        loan.setEndDate(loan.getStartDate().plusDays(dto.getTenureDays()));
        loan.setDailyEmi(dailyDue.doubleValue());
        loan.setRemainingAmount(totalRepayableAmount.doubleValue());
        loan.setIsClosed(false);

        loan.setDisbursedAmount(disbursedAmount);
        loan.setInterestDeductionAmount(upfrontInterest);
        loan.setInterestDeductionPercent(interestPercent);
        loan.setTotalRepayableAmount(totalRepayableAmount);
        loan.setPrincipalOutstanding(totalRepayableAmount);
        loan.setOverdueAmount(BigDecimal.ZERO);
        loan.setTotalPenaltyAmount(BigDecimal.ZERO);
        loan.setTotalPaidAmount(BigDecimal.ZERO);
        loan.setMissedDaysCount(0);
        loan.setPartialDaysCount(0);
        loan.setAdvancePaidDaysCount(0);
        loan.setConsecutiveMissedDays(0);
        loan.setConsecutivePartialDays(0);
        loan.setNextDueDate(loan.getStartDate());
        loan.setClosedEarly(false);
        loan.setClosedLate(false);

        Loan savedLoan = loanRepository.save(loan);

        walletService.debitWallet(
                WalletOwnerType.LENDER,
                savedLoan.getLenderId(),
                disbursedAmount,
                WalletTransactionType.LOAN_DISBURSEMENT_DEBIT,
                "Loan disbursed to borrower",
                savedLoan.getLoanId(),
                null
        );

        walletService.creditWallet(
                WalletOwnerType.BORROWER,
                savedLoan.getBorrowerId(),
                disbursedAmount,
                WalletTransactionType.LOAN_DISBURSEMENT_CREDIT,
                "Loan amount received from lender",
                savedLoan.getLoanId(),
                null
        );

        Borrower borrower = borrowerRepository.findById(savedLoan.getBorrowerId()).orElse(null);
        if (borrower != null) {
            borrower.setTotalLoansTaken((borrower.getTotalLoansTaken() == null ? 0 : borrower.getTotalLoansTaken()) + 1);
            borrowerRepository.save(borrower);
        }

        return new ApiResponse<>(ApiStatus.SUCCESS, "Loan created successfully", mapToResponseDto(savedLoan));
    }

    @Override
    public ApiResponse<LoanResponseDto> getLoanById(Long loanId) {
        Optional<Loan> optionalLoan = loanRepository.findById(loanId);

        if (optionalLoan.isEmpty()) {
            return new ApiResponse<>(ApiStatus.FAILURE, "Loan not found", null);
        }

        return new ApiResponse<>(ApiStatus.SUCCESS, "Loan fetched successfully", mapToResponseDto(optionalLoan.get()));
    }

    @Override
    public ApiResponse<List<LoanResponseDto>> getLoansByBorrower(Long borrowerId) {
        List<Loan> loans = loanRepository.findByBorrowerId(borrowerId);
        List<LoanResponseDto> responseList = new ArrayList<>();

        for (Loan loan : loans) {
            responseList.add(mapToResponseDto(loan));
        }

        return new ApiResponse<>(ApiStatus.SUCCESS, "Borrower loans fetched successfully", responseList);
    }

    @Override
    public ApiResponse<List<LoanResponseDto>> getLoansByLender(Long lenderId) {
        List<Loan> loans = loanRepository.findByLenderId(lenderId);
        List<LoanResponseDto> responseList = new ArrayList<>();

        for (Loan loan : loans) {
            responseList.add(mapToResponseDto(loan));
        }

        return new ApiResponse<>(ApiStatus.SUCCESS, "Lender loans fetched successfully", responseList);
    }

    @Override
    public ApiResponse<String> closeLoan(Long loanId) {
        Optional<Loan> optionalLoan = loanRepository.findById(loanId);

        if (optionalLoan.isEmpty()) {
            return new ApiResponse<>(ApiStatus.FAILURE, "Loan not found", null);
        }

        Loan loan = optionalLoan.get();

        if (Boolean.TRUE.equals(loan.getIsClosed())) {
            return new ApiResponse<>(ApiStatus.FAILURE, "Loan is already closed", null);
        }

        loan.setIsClosed(true);
        loan.setRemainingAmount(0.0);
        loan.setPrincipalOutstanding(BigDecimal.ZERO);
        loan.setEndDate(LocalDate.now());

        loanRepository.save(loan);

        return new ApiResponse<>(ApiStatus.SUCCESS, "Loan closed successfully", "Loan closed successfully");
    }

    private LoanResponseDto mapToResponseDto(Loan loan) {
        LoanResponseDto dto = new LoanResponseDto();
        dto.setLoanId(loan.getLoanId());
        dto.setLoanApplicationId(loan.getLoanApplicationId());
        dto.setBorrowerId(loan.getBorrowerId());
        dto.setLenderId(loan.getLenderId());
        dto.setPlanId(loan.getPlanId());
        dto.setTotalAmount(loan.getTotalAmount());
        dto.setSanctionedAmount(loan.getSanctionedAmount());
        dto.setInterestPerDay(loan.getInterestPerDay());
        dto.setPenaltyAmount(loan.getPenaltyAmount());
        dto.setRemainingAmount(loan.getRemainingAmount());
        dto.setTenureDays(loan.getTenureDays());
        dto.setStartDate(loan.getStartDate());
        dto.setEndDate(loan.getEndDate());
        dto.setDailyEmi(loan.getDailyEmi());
        dto.setIsClosed(loan.getIsClosed());

        dto.setDisbursedAmount(loan.getDisbursedAmount() == null ? null : loan.getDisbursedAmount().doubleValue());
        dto.setTotalRepayableAmount(loan.getTotalRepayableAmount() == null ? null : loan.getTotalRepayableAmount().doubleValue());
        dto.setOverdueAmount(loan.getOverdueAmount() == null ? null : loan.getOverdueAmount().doubleValue());
        dto.setTotalPaidAmount(loan.getTotalPaidAmount() == null ? null : loan.getTotalPaidAmount().doubleValue());
        dto.setMissedDaysCount(loan.getMissedDaysCount());
        dto.setPartialDaysCount(loan.getPartialDaysCount());
        dto.setAdvancePaidDaysCount(loan.getAdvancePaidDaysCount());
        dto.setNextDueDate(loan.getNextDueDate());

        return dto;
    }
}