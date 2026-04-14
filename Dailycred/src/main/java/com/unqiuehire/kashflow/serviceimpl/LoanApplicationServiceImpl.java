package com.unqiuehire.kashflow.serviceImpl;

import com.unqiuehire.kashflow.constant.ApiStatus;
import com.unqiuehire.kashflow.constant.ApplicationStatus;
import com.unqiuehire.kashflow.dto.requestdto.LoanApplicationRequestDto;
import com.unqiuehire.kashflow.dto.responsedto.ApiResponse;
import com.unqiuehire.kashflow.dto.responsedto.LoanApplicationResponseDto;
import com.unqiuehire.kashflow.entity.LoanApplication;
import com.unqiuehire.kashflow.exception.ResourceNotFoundException;
import com.unqiuehire.kashflow.repository.LoanApplicationRepository;
import com.unqiuehire.kashflow.service.LoanApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanApplicationServiceImpl implements LoanApplicationService {

    private final LoanApplicationRepository repository;

    @Override
    public ApiResponse<LoanApplicationResponseDto> createApplication(LoanApplicationRequestDto dto) {

        LoanApplication app = new LoanApplication();

        app.setBorrowerId(dto.getBorrowerId());
        app.setLenderId(dto.getLenderId());
        app.setPlanId(dto.getPlanId());
        app.setLoanAmount(dto.getLoanAmount());

        LoanApplication saved = repository.save(app);

        return new ApiResponse<>(
                ApiStatus.SUCCESS,
                "Loan application created",
                mapToDto(saved)
        );
    }

    @Override
    public ApiResponse<LoanApplicationResponseDto> getById(Long id) {
        LoanApplication app = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        return new ApiResponse<>(ApiStatus.SUCCESS, "Fetched", mapToDto(app));
    }

    @Override
    public ApiResponse<List<LoanApplicationResponseDto>> getByBorrower(Long borrowerId) {

        List<LoanApplicationResponseDto> list = repository.findByBorrowerId(borrowerId)
                .stream()
                .map(this::mapToDto)
                .toList();

        return new ApiResponse<>(ApiStatus.SUCCESS, "Fetched", list);
    }

    @Override
    public ApiResponse<List<LoanApplicationResponseDto>> getByLender(Long lenderId) {

        List<LoanApplicationResponseDto> list = repository.findByLenderId(lenderId)
                .stream()
                .map(this::mapToDto)
                .toList();

        return new ApiResponse<>(ApiStatus.SUCCESS, "Fetched", list);
    }

    @Override
    public ApiResponse<String> cancelApplication(Long id) {

        LoanApplication app = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        app.setStatus(ApplicationStatus.CANCELLED);
        repository.save(app);

        return new ApiResponse<>(ApiStatus.SUCCESS, "Cancelled", "ID: " + id);
    }

    private LoanApplicationResponseDto mapToDto(LoanApplication app) {

        LoanApplicationResponseDto dto = new LoanApplicationResponseDto();

        dto.setApplicationId(app.getApplicationId());
        dto.setBorrowerId(app.getBorrowerId());
        dto.setLenderId(app.getLenderId());
        dto.setPlanId(app.getPlanId());
        dto.setLoanAmount(app.getLoanAmount());
        dto.setStatus(app.getStatus().name());
        dto.setApplicationDate(app.getApplicationDate().toString());

        return dto;
    }
}