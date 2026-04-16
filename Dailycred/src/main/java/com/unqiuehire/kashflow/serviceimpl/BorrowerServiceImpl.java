package com.unqiuehire.kashflow.serviceImpl;

import com.unqiuehire.kashflow.constant.ApiStatus;
import com.unqiuehire.kashflow.constant.BorrowerConstants;
import com.unqiuehire.kashflow.dto.requestdto.BorrowerRequestDto;
import com.unqiuehire.kashflow.dto.responsedto.ApiResponse;
import com.unqiuehire.kashflow.dto.responsedto.BorrowerResponseDto;
import com.unqiuehire.kashflow.entity.Borrower;
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
    public ApiResponse<BorrowerResponseDto> createBorrower(BorrowerRequestDto borrowerRequestDto) {

        String aadhar = borrowerRequestDto.getAadharCardNumber();
        String pan = borrowerRequestDto.getPanCardNumber();

        if ((aadhar == null || aadhar.trim().isEmpty()) &&
                (pan == null || pan.trim().isEmpty())) {
            return new ApiResponse<>(
                    ApiStatus.FAILURE,
                    "Either Aadhar or Pancard must be provided",
                    null
            );
        }

        if (aadhar != null && !aadhar.trim().isEmpty() &&
                pan != null && !pan.trim().isEmpty()) {
            return new ApiResponse<>(
                    ApiStatus.FAILURE,
                    "Borrower cannot upload both Aadhar and Pancard",
                    null
            );
        }

        if (aadhar != null && !aadhar.trim().isEmpty()) {
            Optional<Borrower> existingAadhar = repo.findByAadharCardNumber(aadhar.trim());
            if (existingAadhar.isPresent()) {
                return new ApiResponse<>(
                        ApiStatus.FAILURE,
                        "failed should be aadharcard already existed",
                        null
                );
            }
        }

        if (pan != null && !pan.trim().isEmpty()) {
            Optional<Borrower> existingPan = repo.findByPanCardNumber(pan.trim());
            if (existingPan.isPresent()) {
                return new ApiResponse<>(
                        ApiStatus.FAILURE,
                        "failed should be pancard already existed",
                        null
                );
            }
        }

        Borrower borrower = mapToEntity(borrowerRequestDto);
        borrower.setAadharCardNumber(aadhar != null ? aadhar.trim() : null);
        borrower.setPanCardNumber(pan != null ? pan.trim() : null);

        Borrower savedBorrower = repo.save(borrower);

        return new ApiResponse<>(
                ApiStatus.SUCCESS,
                BorrowerConstants.BORROWER_CREATED.getMessage(),
                mapToResponse(savedBorrower)
        );
    }

    @Override
    public ApiResponse<BorrowerResponseDto> getBorrowerById(Long borrowerId) {
        Optional<Borrower> optionalBorrower = repo.findById(borrowerId);

        if (optionalBorrower.isEmpty()) {
            return new ApiResponse<>(
                    ApiStatus.FAILURE,
                    BorrowerConstants.BORROWER_NOT_FOUND.getMessage(),
                    null
            );
        }

        Borrower borrower = optionalBorrower.get();

        return new ApiResponse<>(
                ApiStatus.SUCCESS,
                BorrowerConstants.BORROWER_FOUND.getMessage(),
                mapToResponse(borrower)
        );
    }

    @Override
    public ApiResponse<List<BorrowerResponseDto>> getAllBorrowers() {
        List<BorrowerResponseDto> borrowerList = repo.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return new ApiResponse<>(
                ApiStatus.SUCCESS,
                BorrowerConstants.BORROWERS_FOUND.getMessage(),
                borrowerList
        );
    }

    @Override
    public ApiResponse<BorrowerResponseDto> updateBorrower(Long borrowerId, BorrowerRequestDto borrowerRequestDto) {
        Optional<Borrower> optionalBorrower = repo.findById(borrowerId);

        if (optionalBorrower.isEmpty()) {
            return new ApiResponse<>(
                    ApiStatus.FAILURE,
                    BorrowerConstants.BORROWER_NOT_FOUND.getMessage(),
                    null
            );
        }

        Borrower existingBorrower = optionalBorrower.get();

        String aadhar = borrowerRequestDto.getAadharCardNumber();
        String pan = borrowerRequestDto.getPanCardNumber();

        if ((aadhar == null || aadhar.trim().isEmpty()) &&
                (pan == null || pan.trim().isEmpty())) {
            return new ApiResponse<>(
                    ApiStatus.FAILURE,
                    "Either Aadhar or Pancard must be provided",
                    null
            );
        }

        if (aadhar != null && !aadhar.trim().isEmpty() &&
                pan != null && !pan.trim().isEmpty()) {
            return new ApiResponse<>(
                    ApiStatus.FAILURE,
                    "Borrower cannot upload both Aadhar and Pancard",
                    null
            );
        }

        if (aadhar != null && !aadhar.trim().isEmpty()) {
            Optional<Borrower> existingAadhar = repo.findByAadharCardNumber(aadhar.trim());
            if (existingAadhar.isPresent() &&
                    !existingAadhar.get().getBorrowerId().equals(borrowerId)) {
                return new ApiResponse<>(
                        ApiStatus.FAILURE,
                        "failed should be aadharcard already existed",
                        null
                );
            }
        }

        if (pan != null && !pan.trim().isEmpty()) {
            Optional<Borrower> existingPan = repo.findByPanCardNumber(pan.trim());
            if (existingPan.isPresent() &&
                    !existingPan.get().getBorrowerId().equals(borrowerId)) {
                return new ApiResponse<>(
                        ApiStatus.FAILURE,
                        "failed should be pancard already existed",
                        null
                );
            }
        }

        existingBorrower.setBorrowerName(borrowerRequestDto.getBorrowerName());
        existingBorrower.setPhoneNumber(borrowerRequestDto.getPhoneNumber());
        existingBorrower.setPassword(borrowerRequestDto.getPassword());
        existingBorrower.setDateOfBirth(LocalDate.parse(borrowerRequestDto.getDateOfBirth()));
        existingBorrower.setAddress(borrowerRequestDto.getAddress());
        existingBorrower.setIsActive(borrowerRequestDto.getIsActive());
        existingBorrower.setPincode(borrowerRequestDto.getPincode());
        existingBorrower.setAadharCardNumber(aadhar != null ? aadhar.trim() : null);
        existingBorrower.setPanCardNumber(pan != null ? pan.trim() : null);

        Borrower updatedBorrower = repo.save(existingBorrower);

        return new ApiResponse<>(
                ApiStatus.SUCCESS,
                BorrowerConstants.BORROWER_UPDATED.getMessage(),
                mapToResponse(updatedBorrower)
        );
    }

    @Override
    public ApiResponse<String> deleteBorrower(Long borrowerId) {
        Optional<Borrower> optionalBorrower = repo.findById(borrowerId);

        if (optionalBorrower.isEmpty()) {
            return new ApiResponse<>(
                    ApiStatus.FAILURE,
                    BorrowerConstants.BORROWER_NOT_FOUND.getMessage(),
                    null
            );
        }

        repo.deleteById(borrowerId);

        return new ApiResponse<>(
                ApiStatus.SUCCESS,
                BorrowerConstants.BORROWER_DELETED.getMessage(),
                "Deleted borrower with id: " + borrowerId
        );
    }

    private BorrowerResponseDto mapToResponse(Borrower borrower) {
        BorrowerResponseDto responseDto = new BorrowerResponseDto();

        responseDto.setBorrowerId(borrower.getBorrowerId());
        responseDto.setBorrowerName(borrower.getBorrowerName());
        responseDto.setPhoneNumber(borrower.getPhoneNumber());
        responseDto.setDateOfBirth(String.valueOf(borrower.getDateOfBirth()));
        responseDto.setAddress(borrower.getAddress());
        responseDto.setIsActive(borrower.getIsActive());
        responseDto.setPincode(borrower.getPincode());
        responseDto.setAadharCardNumber(borrower.getAadharCardNumber());
        responseDto.setPanCardNumber(borrower.getPanCardNumber());

        return responseDto;
    }

    private Borrower mapToEntity(BorrowerRequestDto borrowerRequestDto) {
        Borrower borrower = new Borrower();

        borrower.setBorrowerName(borrowerRequestDto.getBorrowerName());
        borrower.setPhoneNumber(borrowerRequestDto.getPhoneNumber());
        borrower.setPassword(borrowerRequestDto.getPassword());
        borrower.setDateOfBirth(LocalDate.parse(borrowerRequestDto.getDateOfBirth()));
        borrower.setAddress(borrowerRequestDto.getAddress());
        borrower.setIsActive(borrowerRequestDto.getIsActive());
        borrower.setPincode(borrowerRequestDto.getPincode());

        return borrower;
    }
}