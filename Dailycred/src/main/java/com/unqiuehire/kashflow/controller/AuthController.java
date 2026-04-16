package com.unqiuehire.kashflow.controller;

import com.unqiuehire.kashflow.dto.requestdto.LoginRequestDto;
import com.unqiuehire.kashflow.dto.responsedto.ApiResponse;
import com.unqiuehire.kashflow.dto.responsedto.LoginResponseDto;
import com.unqiuehire.kashflow.service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/borrower/login")
    public ApiResponse<LoginResponseDto> borrowerLogin(@RequestBody LoginRequestDto dto) {
        return authService.borrowerLogin(dto);
    }

    @PostMapping("/lender/login")
    public ApiResponse<LoginResponseDto> lenderLogin(@RequestBody LoginRequestDto dto) {
        return authService.lenderLogin(dto);
    }
}