package com.unqiuehire.kashflow.serviceImpl;

import com.unqiuehire.kashflow.constant.ApiStatus;
import com.unqiuehire.kashflow.constant.BorrowerConstants;
import com.unqiuehire.kashflow.dto.requestdto.BorrowerRequestDto;
import com.unqiuehire.kashflow.dto.responsedto.ApiResponse;
import com.unqiuehire.kashflow.dto.responsedto.BorrowerResponseDto;
import com.unqiuehire.kashflow.dto.responsedto.LoanApplicationResponseDto;
import com.unqiuehire.kashflow.entity.Borrower;
import com.unqiuehire.kashflow.entity.LoanApplication;
import com.unqiuehire.kashflow.exception.ResourceNotFoundException;
import com.unqiuehire.kashflow.repository.BorrowerRepository;
import com.unqiuehire.kashflow.service.BorrowerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BorrowerServiceImpl implements BorrowerService {

    private final BorrowerRepository repo;

    @Override
    public ApiResponse<BorrowerResponseDto> createBorrower(BorrowerRequestDto dto) {
        Borrower borrower = mapToEntity(dto);
        Borrower saved = repo.save(borrower);

        return new ApiResponse<>(ApiStatus.SUCCESS,
                BorrowerConstants.BORROWER_CREATED.getMessage(),
                mapToResponse(saved));
    }

    @Override
    public ApiResponse<BorrowerResponseDto> getBorrowerById(Long borrowerId) {
        Borrower borrower = repo.findById(borrowerId)
                .orElseThrow(() -> new ResourceNotFoundException("Borrower not found"));

        return new ApiResponse<>(ApiStatus.SUCCESS,
                BorrowerConstants.BORROWER_FOUND.getMessage(),
                mapToResponse(borrower));
    }

    @Override
    public ApiResponse<List<BorrowerResponseDto>> getAllBorrowers() {
        List<BorrowerResponseDto> list = repo.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();

        return new ApiResponse<>(ApiStatus.SUCCESS,
                BorrowerConstants.BORROWERS_FOUND.getMessage(),
                list);
    }

    @Override
    public ApiResponse<BorrowerResponseDto> updateBorrower(Long borrowerId, BorrowerRequestDto dto) {

        Borrower borrower = repo.findById(borrowerId)
                .orElseThrow(() -> new ResourceNotFoundException("Borrower not found"));

        borrower.setBorrowerName(dto.getBorrowerName());
        borrower.setCibil(dto.getCibil());
        borrower.setPhoneNumber(dto.getPhoneNumber());
        borrower.setPassword(dto.getPassword());
        borrower.setDateOfBirth(LocalDate.parse(dto.getDateOfBirth()));
        borrower.setAddress(dto.getAddress());
        borrower.setIsActive(dto.getIsActive());
        borrower.setPincode(dto.getPincode());

        Borrower updated = repo.save(borrower);

        return new ApiResponse<>(ApiStatus.SUCCESS,
                BorrowerConstants.BORROWER_UPDATED.getMessage(),
                mapToResponse(updated));
    }

    @Override
    public ApiResponse<String> deleteBorrower(Long borrowerId) {

        if (!repo.existsById(borrowerId)) {
            return new ApiResponse<>(ApiStatus.FAILURE,
                    BorrowerConstants.BORROWER_NOT_FOUND.getMessage(),
                    null);
        }

        repo.deleteById(borrowerId);

        return new ApiResponse<>(ApiStatus.SUCCESS,
                BorrowerConstants.BORROWER_DELETED.getMessage(),
                "Deleted borrower with id: " + borrowerId);
    }

    // Get Applications of Borrower
    @Override
    public ApiResponse<List<LoanApplicationResponseDto>> getApplications(Long borrowerId) {

        Borrower borrower = repo.findById(borrowerId)
                .orElseThrow(() -> new ResourceNotFoundException("Borrower not found"));

        List<LoanApplicationResponseDto> list = borrower.getApplications()
                .stream()
                .map(this::mapToApplicationDto)
                .toList();

        return new ApiResponse<>(ApiStatus.SUCCESS, "Fetched", list);
    }

    // ================= MAPPERS =================

    private Borrower mapToEntity(BorrowerRequestDto dto) {
        Borrower borrower = new Borrower();

        borrower.setBorrowerName(dto.getBorrowerName());
        borrower.setCibil(dto.getCibil());
        borrower.setPhoneNumber(dto.getPhoneNumber());
        borrower.setPassword(dto.getPassword());
        borrower.setDateOfBirth(LocalDate.parse(dto.getDateOfBirth()));
        borrower.setAddress(dto.getAddress());
        borrower.setIsActive(dto.getIsActive());
        borrower.setPincode(dto.getPincode());

        return borrower;
    }

    private BorrowerResponseDto mapToResponse(Borrower borrower) {
        BorrowerResponseDto dto = new BorrowerResponseDto();

        dto.setBorrowerId(borrower.getBorrowerId());
        dto.setBorrowerName(borrower.getBorrowerName());
        dto.setCibil(borrower.getCibil());
        dto.setPhoneNumber(borrower.getPhoneNumber());
        dto.setDateOfBirth(String.valueOf(borrower.getDateOfBirth()));
        dto.setAddress(borrower.getAddress());
        dto.setIsActive(borrower.getIsActive());
        dto.setPincode(borrower.getPincode());

        return dto;
    }

    private LoanApplicationResponseDto mapToApplicationDto(LoanApplication app) {
        LoanApplicationResponseDto dto = new LoanApplicationResponseDto();

        dto.setApplicationId(app.getApplicationId());

        dto.setBorrowerId(app.getBorrower().getBorrowerId());

        dto.setLenderId(app.getLender().getLenderId());

        dto.setPlanId(app.getPlanId());
        dto.setLoanAmount(app.getLoanAmount());
        dto.setStatus(app.getStatus().name());


        if (app.getApplicationDate() != null) {
            dto.setApplicationDate(app.getApplicationDate().toString());
        }

        return dto;
    }
}