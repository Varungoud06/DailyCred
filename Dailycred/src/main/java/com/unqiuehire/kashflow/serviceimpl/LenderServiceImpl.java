package com.unqiuehire.kashflow.serviceImpl;
import com.unqiuehire.kashflow.constant.ApiStatus;
import com.unqiuehire.kashflow.constant.LenderConstants;
import com.unqiuehire.kashflow.dto.requestdto.LenderRequestDto;
import com.unqiuehire.kashflow.dto.responsedto.ApiResponse;
import com.unqiuehire.kashflow.dto.responsedto.LenderResponseDto;
import com.unqiuehire.kashflow.entity.Lender;
import com.unqiuehire.kashflow.repository.LenderRepository;
import com.unqiuehire.kashflow.service.LenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LenderServiceImpl implements LenderService {

    @Autowired
    private LenderRepository lenderRepository;

    @Override
    public ApiResponse<LenderResponseDto> createLender(LenderRequestDto lenderRequestDto) {
        Lender lender = new Lender();

        lender.setLenderName(lenderRequestDto.getLenderName());
        lender.setCibil(lenderRequestDto.getCibil());
        lender.setDateOfBirth(LocalDate.parse(lenderRequestDto.getDateOfBirth()));
        lender.setPassword(lenderRequestDto.getPassword());
        lender.setIsActive(lenderRequestDto.getIsActive());
        lender.setPhoneNumber(lenderRequestDto.getPhoneNumber());
        lender.setPincode(lenderRequestDto.getPincode());
        lender.setAddress(lenderRequestDto.getAddress());

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

        lender.setLenderName(lenderRequestDto.getLenderName());
        lender.setCibil(lenderRequestDto.getCibil());
        lender.setDateOfBirth(LocalDate.parse(lenderRequestDto.getDateOfBirth()));
        lender.setPassword(lenderRequestDto.getPassword());
        lender.setIsActive(lenderRequestDto.getIsActive());
        lender.setPhoneNumber(lenderRequestDto.getPhoneNumber());
        lender.setPincode(lenderRequestDto.getPincode());
        lender.setAddress(lenderRequestDto.getAddress());

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
        responseDto.setCibil(lender.getCibil());
        responseDto.setDateOfBirth(String.valueOf(lender.getDateOfBirth()));
        responseDto.setIsActive(lender.getIsActive());
        responseDto.setPhoneNumber(lender.getPhoneNumber());
        responseDto.setPincode(lender.getPincode());
        responseDto.setAddress(lender.getAddress());
        return responseDto;
    }
}