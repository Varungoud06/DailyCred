package com.unqiuehire.kashflow.serviceimpl;

import com.unqiuehire.kashflow.constant.ApiStatus;
import com.unqiuehire.kashflow.constant.BorrowerConstants;
import com.unqiuehire.kashflow.dto.requestdto.BorrowerRequestDto;
import com.unqiuehire.kashflow.dto.responsedto.ApiResponse;
import com.unqiuehire.kashflow.dto.responsedto.BorrowerResponseDto;
import com.unqiuehire.kashflow.entity.Borrower;
import com.unqiuehire.kashflow.repository.BorrowerRepository;
import com.unqiuehire.kashflow.service.BorrowerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BorrowerServiceImpl implements BorrowerService {

    private final BorrowerRepository repo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ApiResponse<BorrowerResponseDto> createBorrower(BorrowerRequestDto borrowerRequestDto) {

        String aadhar = normalize(borrowerRequestDto.getAadharCardNumber());
        String pan = normalize(borrowerRequestDto.getPanCardNumber());
        String phone = normalize(borrowerRequestDto.getPhoneNumber());

        ApiResponse<BorrowerResponseDto> validationFailure = validateBorrowerRequest(borrowerRequestDto);
        if (validationFailure != null) {
            return validationFailure;
        }

        if (aadhar != null && repo.findByAadharCardNumber(aadhar).isPresent()) {
            return new ApiResponse<>(
                    ApiStatus.FAILURE,
                    "Aadhaar number already exists",
                    null
            );
        }

        if (pan != null && repo.findByPanCardNumber(pan).isPresent()) {
            return new ApiResponse<>(
                    ApiStatus.FAILURE,
                    "PAN number already exists",
                    null
            );
        }

        if (phone != null && repo.findByPhoneNumber(phone).isPresent()) {
            return new ApiResponse<>(
                    ApiStatus.FAILURE,
                    "Phone number already exists",
                    null
            );
        }

        Borrower borrower = mapToEntity(borrowerRequestDto);
        borrower.setAadharCardNumber(aadhar);
        borrower.setPanCardNumber(pan);
        borrower.setPhoneNumber(phone);

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

        return new ApiResponse<>(
                ApiStatus.SUCCESS,
                BorrowerConstants.BORROWER_FOUND.getMessage(),
                mapToResponse(optionalBorrower.get())
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

        ApiResponse<BorrowerResponseDto> validationFailure = validateBorrowerRequest(borrowerRequestDto);
        if (validationFailure != null) {
            return validationFailure;
        }

        Borrower existingBorrower = optionalBorrower.get();

        String aadhar = normalize(borrowerRequestDto.getAadharCardNumber());
        String pan = normalize(borrowerRequestDto.getPanCardNumber());
        String phone = normalize(borrowerRequestDto.getPhoneNumber());

        if (aadhar != null) {
            Optional<Borrower> existingAadhar = repo.findByAadharCardNumber(aadhar);
            if (existingAadhar.isPresent() && !existingAadhar.get().getBorrowerId().equals(borrowerId)) {
                return new ApiResponse<>(
                        ApiStatus.FAILURE,
                        "Aadhaar number already exists",
                        null
                );
            }
        }

        if (pan != null) {
            Optional<Borrower> existingPan = repo.findByPanCardNumber(pan);
            if (existingPan.isPresent() && !existingPan.get().getBorrowerId().equals(borrowerId)) {
                return new ApiResponse<>(
                        ApiStatus.FAILURE,
                        "PAN number already exists",
                        null
                );
            }
        }

        if (phone != null) {
            Optional<Borrower> existingPhone = repo.findByPhoneNumber(phone);
            if (existingPhone.isPresent() && !existingPhone.get().getBorrowerId().equals(borrowerId)) {
                return new ApiResponse<>(
                        ApiStatus.FAILURE,
                        "Phone number already exists",
                        null
                );
            }
        }

        existingBorrower.setBorrowerName(borrowerRequestDto.getBorrowerName().trim());
        existingBorrower.setDateOfBirth(LocalDate.parse(borrowerRequestDto.getDateOfBirth()));
        existingBorrower.setPassword(passwordEncoder.encode(borrowerRequestDto.getPassword().trim()));
        existingBorrower.setIsActive(borrowerRequestDto.getIsActive());
        existingBorrower.setPhoneNumber(phone);
        existingBorrower.setPincode(borrowerRequestDto.getPincode().trim());
        existingBorrower.setAddress(borrowerRequestDto.getAddress().trim());
        existingBorrower.setCibil(borrowerRequestDto.getCibil());
        existingBorrower.setAadharCardNumber(aadhar);
        existingBorrower.setPanCardNumber(pan);

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
        responseDto.setDateOfBirth(String.valueOf(borrower.getDateOfBirth()));
        responseDto.setIsActive(borrower.getIsActive());
        responseDto.setPhoneNumber(borrower.getPhoneNumber());
        responseDto.setPincode(borrower.getPincode());
        responseDto.setAddress(borrower.getAddress());
        responseDto.setCibil(borrower.getCibil());
        responseDto.setAadharCardNumber(borrower.getAadharCardNumber());
        responseDto.setPanCardNumber(borrower.getPanCardNumber());

        return responseDto;
    }

    private Borrower mapToEntity(BorrowerRequestDto borrowerRequestDto) {
        Borrower borrower = new Borrower();

        borrower.setBorrowerName(borrowerRequestDto.getBorrowerName().trim());
        borrower.setDateOfBirth(LocalDate.parse(borrowerRequestDto.getDateOfBirth()));
        borrower.setPassword(passwordEncoder.encode(borrowerRequestDto.getPassword().trim()));
        borrower.setIsActive(borrowerRequestDto.getIsActive());
        borrower.setPhoneNumber(normalize(borrowerRequestDto.getPhoneNumber()));
        borrower.setPincode(borrowerRequestDto.getPincode().trim());
        borrower.setAddress(borrowerRequestDto.getAddress().trim());
        borrower.setCibil(borrowerRequestDto.getCibil());
        borrower.setAadharCardNumber(normalize(borrowerRequestDto.getAadharCardNumber()));
        borrower.setPanCardNumber(normalize(borrowerRequestDto.getPanCardNumber()));

        return borrower;
    }

    private ApiResponse<BorrowerResponseDto> validateBorrowerRequest(BorrowerRequestDto borrowerRequestDto) {
        if (borrowerRequestDto == null) {
            return new ApiResponse<>(ApiStatus.FAILURE, "Borrower request cannot be null", null);
        }

        if (isBlank(borrowerRequestDto.getBorrowerName())) {
            return new ApiResponse<>(ApiStatus.FAILURE, "Borrower name is required", null);
        }

        if (isBlank(borrowerRequestDto.getDateOfBirth())) {
            return new ApiResponse<>(ApiStatus.FAILURE, "Date of birth is required", null);
        }

        if (isBlank(borrowerRequestDto.getPassword())) {
            return new ApiResponse<>(ApiStatus.FAILURE, "Password is required", null);
        }

        if (borrowerRequestDto.getIsActive() == null) {
            return new ApiResponse<>(ApiStatus.FAILURE, "Active status is required", null);
        }

        if (isBlank(borrowerRequestDto.getPhoneNumber())) {
            return new ApiResponse<>(ApiStatus.FAILURE, "Phone number is required", null);
        }

        if (isBlank(borrowerRequestDto.getPincode())) {
            return new ApiResponse<>(ApiStatus.FAILURE, "Pincode is required", null);
        }

        if (isBlank(borrowerRequestDto.getAddress())) {
            return new ApiResponse<>(ApiStatus.FAILURE, "Address is required", null);
        }

        if (borrowerRequestDto.getCibil() == null) {
            return new ApiResponse<>(ApiStatus.FAILURE, "CIBIL is required", null);
        }

        if (borrowerRequestDto.getCibil() < 300 || borrowerRequestDto.getCibil() > 900) {
            return new ApiResponse<>(ApiStatus.FAILURE, "CIBIL must be between 300 and 900", null);
        }

        String aadhar = normalize(borrowerRequestDto.getAadharCardNumber());
        String pan = normalize(borrowerRequestDto.getPanCardNumber());

        if ((aadhar == null || aadhar.isEmpty()) && (pan == null || pan.isEmpty())) {
            return new ApiResponse<>(ApiStatus.FAILURE, "Either Aadhaar or PAN must be provided", null);
        }

        return null;
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}