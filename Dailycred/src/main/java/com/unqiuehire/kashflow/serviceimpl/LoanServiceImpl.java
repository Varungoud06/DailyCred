package com.unqiuehire.kashflow.serviceImpl;
import com.unqiuehire.kashflow.constant.ApiStatus;
import com.unqiuehire.kashflow.dto.requestdto.LoanRequestDto;
import com.unqiuehire.kashflow.dto.responsedto.ApiResponse;
import com.unqiuehire.kashflow.dto.responsedto.LoanResponseDto;
import com.unqiuehire.kashflow.entity.Loan;
import com.unqiuehire.kashflow.repository.LoanRepository;
import com.unqiuehire.kashflow.service.LoanService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;

    public LoanServiceImpl(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    @Override
    public ApiResponse<LoanResponseDto> createLoan(LoanRequestDto dto) {

        Loan loan = new Loan();
        loan.setLoanApplicationId(dto.getLoanApplicationId());
        loan.setBorrowerId(dto.getBorrowerId());
        loan.setLenderId(dto.getLenderId());
        loan.setPlanId(dto.getPlanId());
        loan.setTotalAmount(dto.getTotalAmount());
        loan.setSanctionedAmount(dto.getSanctionedAmount());
        loan.setInterestPerDay(dto.getInterestPerDay());
        loan.setPenaltyAmount(dto.getPenaltyAmount());
        loan.setTenureDays(dto.getTenureDays());
        loan.setStartDate(dto.getStartDate());

        loan.setRemainingAmount(dto.getSanctionedAmount());
        loan.setEndDate(dto.getStartDate().plusDays(dto.getTenureDays()));
        loan.setDailyEmi(dto.getSanctionedAmount() / dto.getTenureDays());
        loan.setIsClosed(false);

        Loan savedLoan = loanRepository.save(loan);

        LoanResponseDto responseDto = mapToResponseDto(savedLoan);

        return new ApiResponse<>(ApiStatus.SUCCESS, "Loan created successfully", responseDto);
    }

    @Override
    public ApiResponse<LoanResponseDto> getLoanById(Long loanId) {
        Optional<Loan> optionalLoan = loanRepository.findById(loanId);

        if (optionalLoan.isEmpty()) {
            return new ApiResponse<>(ApiStatus.FAILURE, "Loan not found", null);
        }

        LoanResponseDto responseDto = mapToResponseDto(optionalLoan.get());
        return new ApiResponse<>(ApiStatus.SUCCESS, "Loan fetched successfully", responseDto);
    }

    @Override
    public ApiResponse<List<LoanResponseDto>> getLoansByBorrower(Long borrowerId) {
        List<Loan> loans = loanRepository.findByBorrowerId(borrowerId);

        if (loans.isEmpty()) {
            return new ApiResponse<>(ApiStatus.FAILURE, "No loans found for borrower", null);
        }

        List<LoanResponseDto> responseList = new ArrayList<>();
        for (Loan loan : loans) {
            responseList.add(mapToResponseDto(loan));
        }

        return new ApiResponse<>(ApiStatus.SUCCESS, "Borrower loans fetched successfully", responseList);
    }

    @Override
    public ApiResponse<List<LoanResponseDto>> getLoansByLender(Long lenderId) {
        List<Loan> loans = loanRepository.findByLenderId(lenderId);

        if (loans.isEmpty()) {
            return new ApiResponse<>(ApiStatus.FAILURE, "No loans found for lender", null);
        }

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
        return dto;
    }
}