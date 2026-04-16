package com.unqiuehire.kashflow.serviceImpl;

import com.unqiuehire.kashflow.constant.ApiStatus;
import com.unqiuehire.kashflow.constant.ApplicationStatus;
import com.unqiuehire.kashflow.dto.requestdto.LoanApplicationRequestDto;
import com.unqiuehire.kashflow.dto.responsedto.ApiResponse;
import com.unqiuehire.kashflow.dto.responsedto.LoanApplicationResponseDto;
import com.unqiuehire.kashflow.entity.Borrower;
import com.unqiuehire.kashflow.entity.Lender;
import com.unqiuehire.kashflow.entity.LoanApplication;
import com.unqiuehire.kashflow.exception.ResourceNotFoundException;
import com.unqiuehire.kashflow.repository.BorrowerRepository;
import com.unqiuehire.kashflow.repository.LenderRepository;
import com.unqiuehire.kashflow.repository.LoanApplicationRepository;
import com.unqiuehire.kashflow.service.LoanApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

    @Service
    @RequiredArgsConstructor
    public class LoanApplicationServiceImpl implements LoanApplicationService {

        private final LoanApplicationRepository repository;
        private final BorrowerRepository repo;
        private final LenderRepository lenderRepo;
        @Override
        public ApiResponse<LoanApplicationResponseDto> createApplication(LoanApplicationRequestDto dto) {

            LoanApplication app = new LoanApplication();

            // Borrower mapping
            Borrower borrower = repo.findById(dto.getBorrowerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Borrower not found"));
            app.setBorrower(borrower);

            //Lender mapping (FIX)
            Lender lender = lenderRepo.findById(dto.getLenderId())
                    .orElseThrow(() -> new ResourceNotFoundException("Lender not found"));
            app.setLender(lender);

            app.setPlanId(dto.getPlanId());
            app.setLoanAmount(dto.getLoanAmount());

            LoanApplication saved = repository.save(app);

            return new ApiResponse<>(ApiStatus.SUCCESS, "Created", mapToDto(saved));
        }

        @Override
        public ApiResponse<LoanApplicationResponseDto> getById(Long id) {
            LoanApplication app = repository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Not found"));
            return new ApiResponse<>(ApiStatus.SUCCESS, "Fetched", map(app));
        }

        @Override
        public ApiResponse<String> cancelApplication(Long id) {
            LoanApplication app = repository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Not found"));

            app.setStatus(ApplicationStatus.CANCELLED);
            repository.save(app);

            return new ApiResponse<>(ApiStatus.SUCCESS, "Cancelled", "ID: " + id);
        }

        private LoanApplicationResponseDto map(LoanApplication app) {

            LoanApplicationResponseDto dto = new LoanApplicationResponseDto();

            dto.setApplicationId(app.getApplicationId());

            dto.setBorrowerId(
                    app.getBorrower() != null ? app.getBorrower().getBorrowerId() : null
            );

            // ✅ FIX HERE
            dto.setLenderId(
                    app.getLender() != null ? app.getLender().getLenderId() : null
            );

            dto.setPlanId(app.getPlanId());
            dto.setLoanAmount(app.getLoanAmount());
            dto.setStatus(app.getStatus().name());

            return dto;
        }
    @Override
    public ApiResponse<List<LoanApplicationResponseDto>> getByBorrower(Long borrowerId) {

        List<LoanApplicationResponseDto> list = repository.findByBorrower_BorrowerId(borrowerId)
                .stream()
                .map(this::mapToDto)
                .toList();

        return new ApiResponse<>(ApiStatus.SUCCESS, "Fetched", list);
    }

    @Override
    public ApiResponse<List<LoanApplicationResponseDto>> getByLender(Long lenderId) {

        List<LoanApplicationResponseDto> list = repository.findByLender_LenderId(lenderId)
                .stream()
                .map(this::mapToDto)
                .toList();

        return new ApiResponse<>(ApiStatus.SUCCESS, "Fetched", list);
    }

        private LoanApplicationResponseDto mapToDto(LoanApplication app) {
            LoanApplicationResponseDto dto = new LoanApplicationResponseDto();

            dto.setApplicationId(app.getApplicationId());

            dto.setBorrowerId(
                    app.getBorrower() != null ? app.getBorrower().getBorrowerId() : null
            );

            dto.setLenderId(
                    app.getLender() != null ? app.getLender().getLenderId() : null
            );

            dto.setPlanId(app.getPlanId());
            dto.setLoanAmount(app.getLoanAmount());
            dto.setStatus(app.getStatus().name());
            dto.setApplicationDate(
                    app.getApplicationDate() != null ? app.getApplicationDate().toString() : null
            );

            return dto;
        }
}