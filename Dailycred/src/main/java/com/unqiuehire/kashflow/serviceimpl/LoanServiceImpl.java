package com.unqiuehire.kashflow.serviceImpl;
import com.unqiuehire.kashflow.constant.ApiStatus;
import com.unqiuehire.kashflow.dto.responsedto.ApiResponse;
import com.unqiuehire.kashflow.dto.responsedto.LoanResponseDto;
import com.unqiuehire.kashflow.entity.Loan;
import com.unqiuehire.kashflow.entity.LoanApplication;
import com.unqiuehire.kashflow.exception.ResourceNotFoundException;
import com.unqiuehire.kashflow.repository.LoanRepository;
import com.unqiuehire.kashflow.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepo;

    public Loan createLoanFromApplication(LoanApplication app) {

        Loan loan = new Loan();

        loan.setLoanApplication(app);

        loan.setBorrower(app.getBorrower());
        loan.setLender(app.getLender());

        loan.setPlanId(app.getPlanId());
        loan.setSanctionedAmount(app.getLoanAmount());
        loan.setRemainingAmount(app.getLoanAmount());
        loan.setIsClosed(false);

        Loan saved = loanRepo.save(loan);

        app.setIsLoanCreated(true);

        return saved;
    }

    @Override
    public ApiResponse<LoanResponseDto> getLoanById(Long id) {
        Loan loan = loanRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found"));

        return new ApiResponse<>(ApiStatus.SUCCESS, "Fetched", mapToResponseDto(loan));
    }

    @Override
    public ApiResponse<String> closeLoan(Long id) {
        Loan loan = loanRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found"));

        if (Boolean.TRUE.equals(loan.getIsClosed())) {
            return new ApiResponse<>(ApiStatus.FAILURE, "Already closed", null);
        }

        loan.setIsClosed(true);
        loan.setRemainingAmount(0.0);

        loanRepo.save(loan);

        return new ApiResponse<>(ApiStatus.SUCCESS, "Closed", "Done");
    }

    @Override
    public ApiResponse<List<LoanResponseDto>> getLoansByBorrower(Long borrowerId) {

        List<Loan> loans = loanRepo.findByBorrower_BorrowerId(borrowerId);

        if (loans.isEmpty()) {
            return new ApiResponse<>(ApiStatus.FAILURE, "No loans found", null);
        }

        List<LoanResponseDto> list = new ArrayList<>();
        for (Loan loan : loans) {
            list.add(mapToResponseDto(loan));
        }

        return new ApiResponse<>(ApiStatus.SUCCESS, "Borrower loans fetched", list);
    }

    @Override
    public ApiResponse<List<LoanResponseDto>> getLoansByLender(Long lenderId) {

        List<Loan> loans = loanRepo.findByLender_LenderId(lenderId);

        if (loans.isEmpty()) {
            return new ApiResponse<>(ApiStatus.FAILURE, "No loans found", null);
        }

        List<LoanResponseDto> list = new ArrayList<>();
        for (Loan loan : loans) {
            list.add(mapToResponseDto(loan));
        }

        return new ApiResponse<>(ApiStatus.SUCCESS, "Lender loans fetched", list);
    }

    private LoanResponseDto mapToResponseDto(Loan loan) {

        LoanResponseDto dto = new LoanResponseDto();

        dto.setLoanId(loan.getLoanId());

        dto.setLoanApplicationId(
                loan.getLoanApplication() != null
                        ? loan.getLoanApplication().getApplicationId()
                        : null
        );

        dto.setBorrowerId(
                loan.getBorrower() != null
                        ? loan.getBorrower().getBorrowerId()
                        : null
        );

        dto.setLenderId(
                loan.getLender() != null
                        ? loan.getLender().getLenderId()
                        : null
        );

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

        return dto;
    }
}