package com.unqiuehire.kashflow.serviceimpl;

import com.unqiuehire.kashflow.constant.ApiStatus;
import com.unqiuehire.kashflow.constant.LenderConstants;
import com.unqiuehire.kashflow.dto.requestdto.LenderRequestDto;
import com.unqiuehire.kashflow.dto.responsedto.ApiResponse;
import com.unqiuehire.kashflow.dto.responsedto.LenderResponseDto;
import com.unqiuehire.kashflow.entity.Lender;
import com.unqiuehire.kashflow.repository.LenderRepository;
import com.unqiuehire.kashflow.service.LenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LenderServiceImpl implements LenderService {

    @Autowired
    private LenderRepository lenderRepository;

    private final PasswordEncoder passwordEncoder;

    public LenderServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public ApiResponse<LenderResponseDto> createLender(LenderRequestDto lenderRequestDto) {

        String aadhar = lenderRequestDto.getAadharCardNumber() != null
                ? lenderRequestDto.getAadharCardNumber().trim()
                : null;

        String pan = lenderRequestDto.getPanCardNumber() != null
                ? lenderRequestDto.getPanCardNumber().trim()
                : null;

        boolean hasAadhar = aadhar != null && !aadhar.isEmpty();
        boolean hasPan = pan != null && !pan.isEmpty();

        if (hasAadhar && hasPan) {
            return new ApiResponse<>(
                    ApiStatus.FAILURE,
                    "Please provide either Aadhar card or PAN card, not both.",
                    null
            );
        }

        if (!hasAadhar && !hasPan) {
            return new ApiResponse<>(
                    ApiStatus.FAILURE,
                    "Please provide either Aadhar card or PAN card.",
                    null
            );
        }

        if (hasAadhar && lenderRepository.existsByAadharCardNumber(aadhar)) {
            return new ApiResponse<>(
                    ApiStatus.FAILURE,
                    "Aadhar card already exists.",
                    null
            );
        }

        if (hasPan && lenderRepository.existsByPanCardNumber(pan)) {
            return new ApiResponse<>(
                    ApiStatus.FAILURE,
                    "PAN card already exists.",
                    null
            );
        }

        Lender lender = new Lender();
        lender.setLenderName(lenderRequestDto.getLenderName());
        lender.setDateOfBirth(LocalDate.parse(lenderRequestDto.getDateOfBirth()));
        lender.setPassword(passwordEncoder.encode(lenderRequestDto.getPassword()));
        lender.setIsActive(lenderRequestDto.getIsActive());
        lender.setPhoneNumber(lenderRequestDto.getPhoneNumber());
        lender.setPincode(lenderRequestDto.getPincode());
        lender.setAddress(lenderRequestDto.getAddress());

        if (hasAadhar) {
            lender.setAadharCardNumber(aadhar);
            lender.setPanCardNumber(null);
        } else {
            lender.setPanCardNumber(pan);
            lender.setAadharCardNumber(null);
        }

        Lender savedLender = lenderRepository.save(lender);
        LenderResponseDto responseDto = mapToResponseDto(savedLender);

        return new ApiResponse<>(
                ApiStatus.SUCCESS,
                LenderConstants.LENDER_CREATED.getMessage(),
                responseDto
        );
    }

    @Override
    public ApiResponse<LenderResponseDto> getLenderById(Long lenderId) {
        Optional<Lender> optionalLender = lenderRepository.findById(lenderId);

        if (optionalLender.isEmpty()) {
            return new ApiResponse<>(
                    ApiStatus.FAILURE,
                    LenderConstants.LENDER_NOT_FOUND.getMessage(),
                    null
            );
        }

        Lender lender = optionalLender.get();
        LenderResponseDto responseDto = mapToResponseDto(lender);

        return new ApiResponse<>(
                ApiStatus.SUCCESS,
                LenderConstants.LENDER_FOUND.getMessage(),
                responseDto
        );
    }

    @Override
    public ApiResponse<List<LenderResponseDto>> getAllLenders() {
        List<Lender> lenderList = lenderRepository.findAll();
        List<LenderResponseDto> responseDtoList = new ArrayList<>();

        for (Lender lender : lenderList) {
            responseDtoList.add(mapToResponseDto(lender));
        }

        return new ApiResponse<>(
                ApiStatus.SUCCESS,
                LenderConstants.LENDERS_FOUND.getMessage(),
                responseDtoList
        );
    }

    @Override
    public ApiResponse<LenderResponseDto> updateLender(Long lenderId, LenderRequestDto lenderRequestDto) {
        Optional<Lender> optionalLender = lenderRepository.findById(lenderId);

        if (optionalLender.isEmpty()) {
            return new ApiResponse<>(
                    ApiStatus.FAILURE,
                    LenderConstants.LENDER_NOT_FOUND.getMessage(),
                    null
            );
        }

        Lender lender = optionalLender.get();

        String aadhar = lenderRequestDto.getAadharCardNumber() != null
                ? lenderRequestDto.getAadharCardNumber().trim()
                : null;

        String pan = lenderRequestDto.getPanCardNumber() != null
                ? lenderRequestDto.getPanCardNumber().trim()
                : null;

        boolean hasAadhar = aadhar != null && !aadhar.isEmpty();
        boolean hasPan = pan != null && !pan.isEmpty();

        if (hasAadhar && hasPan) {
            return new ApiResponse<>(
                    ApiStatus.FAILURE,
                    "Please provide either Aadhar card or PAN card, not both.",
                    null
            );
        }

        if (!hasAadhar && !hasPan) {
            return new ApiResponse<>(
                    ApiStatus.FAILURE,
                    "Please provide either Aadhar card or PAN card.",
                    null
            );
        }

        if (hasAadhar
                && !aadhar.equals(lender.getAadharCardNumber())
                && lenderRepository.existsByAadharCardNumber(aadhar)) {
            return new ApiResponse<>(
                    ApiStatus.FAILURE,
                    "Aadhar card already exists.",
                    null
            );
        }

        if (hasPan
                && !pan.equals(lender.getPanCardNumber())
                && lenderRepository.existsByPanCardNumber(pan)) {
            return new ApiResponse<>(
                    ApiStatus.FAILURE,
                    "PAN card already exists.",
                    null
            );
        }

        lender.setLenderName(lenderRequestDto.getLenderName());
        lender.setDateOfBirth(LocalDate.parse(lenderRequestDto.getDateOfBirth()));
        lender.setPassword(passwordEncoder.encode(lenderRequestDto.getPassword()));
        lender.setIsActive(lenderRequestDto.getIsActive());
        lender.setPhoneNumber(lenderRequestDto.getPhoneNumber());
        lender.setPincode(lenderRequestDto.getPincode());
        lender.setAddress(lenderRequestDto.getAddress());

        if (hasAadhar) {
            lender.setAadharCardNumber(aadhar);
            lender.setPanCardNumber(null);
        } else {
            lender.setPanCardNumber(pan);
            lender.setAadharCardNumber(null);
        }

        Lender updatedLender = lenderRepository.save(lender);
        LenderResponseDto responseDto = mapToResponseDto(updatedLender);

        return new ApiResponse<>(
                ApiStatus.SUCCESS,
                LenderConstants.LENDER_UPDATED.getMessage(),
                responseDto
        );
    }

    @Override
    public ApiResponse<String> deleteLender(Long lenderId) {
        Optional<Lender> optionalLender = lenderRepository.findById(lenderId);

        if (optionalLender.isEmpty()) {
            return new ApiResponse<>(
                    ApiStatus.FAILURE,
                    LenderConstants.LENDER_NOT_FOUND.getMessage(),
                    null
            );
        }

        lenderRepository.deleteById(lenderId);

        return new ApiResponse<>(
                ApiStatus.SUCCESS,
                LenderConstants.LENDER_DELETED.getMessage(),
                "Deleted lender with id: " + lenderId
        );
    }

    private LenderResponseDto mapToResponseDto(Lender lender) {
        LenderResponseDto responseDto = new LenderResponseDto();
        responseDto.setLenderId(lender.getLenderId());
        responseDto.setLenderName(lender.getLenderName());
        responseDto.setDateOfBirth(String.valueOf(lender.getDateOfBirth()));
        responseDto.setIsActive(lender.getIsActive());
        responseDto.setPhoneNumber(lender.getPhoneNumber());
        responseDto.setPincode(lender.getPincode());
        responseDto.setAddress(lender.getAddress());
        responseDto.setAadharCardNumber(lender.getAadharCardNumber());
        responseDto.setPanCardNumber(lender.getPanCardNumber());
        return responseDto;
    }
}