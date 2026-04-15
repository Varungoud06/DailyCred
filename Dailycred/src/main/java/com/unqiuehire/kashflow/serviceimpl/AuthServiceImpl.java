package com.unqiuehire.kashflow.serviceimpl;
import java.util.Optional;

import com.unqiuehire.kashflow.constant.ApiStatus;
import com.unqiuehire.kashflow.constant.Role;
import com.unqiuehire.kashflow.dto.requestdto.LoginRequestDto;
import com.unqiuehire.kashflow.dto.responsedto.ApiResponse;
import com.unqiuehire.kashflow.dto.responsedto.LoginResponseDto;
import com.unqiuehire.kashflow.entity.Borrower;
import com.unqiuehire.kashflow.entity.Lender;
import com.unqiuehire.kashflow.repository.BorrowerRepository;
import com.unqiuehire.kashflow.repository.LenderRepository;
import com.unqiuehire.kashflow.security.JwtUtil;
import com.unqiuehire.kashflow.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final BorrowerRepository borrowerRepository;
    private final LenderRepository lenderRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(BorrowerRepository borrowerRepository,
                           LenderRepository lenderRepository,
                           JwtUtil jwtUtil,
                           PasswordEncoder passwordEncoder) {
        this.borrowerRepository = borrowerRepository;
        this.lenderRepository = lenderRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public ApiResponse<LoginResponseDto> borrowerLogin(LoginRequestDto dto) {
        Optional<Borrower> optionalBorrower = borrowerRepository.findByPhoneNumber(dto.getPhoneNumber());

        if (optionalBorrower.isEmpty()) {
            return new ApiResponse<>(ApiStatus.FAILURE, "Borrower not found", null);
        }

        Borrower borrower = optionalBorrower.get();

        if (!Boolean.TRUE.equals(borrower.getIsActive())) {
            return new ApiResponse<>(ApiStatus.FAILURE, "Borrower account is inactive", null);
        }

        if (!passwordEncoder.matches(dto.getPassword(), borrower.getPassword())) {
            return new ApiResponse<>(ApiStatus.FAILURE, "Invalid password", null);
        }

        String token = jwtUtil.generateToken(
                borrower.getBorrowerId(),
                borrower.getPhoneNumber(),
                Role.BORROWER.name()
        );

        LoginResponseDto responseDto = new LoginResponseDto();
        responseDto.setToken(token);
        responseDto.setRole(Role.BORROWER.name());
        responseDto.setUserId(borrower.getBorrowerId());
        responseDto.setPhoneNumber(borrower.getPhoneNumber());

        return new ApiResponse<>(ApiStatus.SUCCESS, "Borrower login successful", responseDto);
    }

    @Override
    public ApiResponse<LoginResponseDto> lenderLogin(LoginRequestDto dto) {
        Optional<Lender> optionalLender = lenderRepository.findByPhoneNumber(dto.getPhoneNumber());

        if (optionalLender.isEmpty()) {
            return new ApiResponse<>(ApiStatus.FAILURE, "Lender not found", null);
        }

        Lender lender = optionalLender.get();

        if (!Boolean.TRUE.equals(lender.getIsActive())) {
            return new ApiResponse<>(ApiStatus.FAILURE, "Lender account is inactive", null);
        }

        if (!passwordEncoder.matches(dto.getPassword(), lender.getPassword())) {
            return new ApiResponse<>(ApiStatus.FAILURE, "Invalid password", null);
        }

        String token = jwtUtil.generateToken(
                lender.getLenderId(),
                lender.getPhoneNumber(),
                Role.LENDER.name()
        );

        LoginResponseDto responseDto = new LoginResponseDto();
        responseDto.setToken(token);
        responseDto.setRole(Role.LENDER.name());
        responseDto.setUserId(lender.getLenderId());
        responseDto.setPhoneNumber(lender.getPhoneNumber());

        return new ApiResponse<>(ApiStatus.SUCCESS, "Lender login successful", responseDto);
    }
}