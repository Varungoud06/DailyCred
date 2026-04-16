package com.unqiuehire.kashflow.service;
import com.unqiuehire.kashflow.dto.requestdto.LoginRequestDto;
import com.unqiuehire.kashflow.dto.responsedto.ApiResponse;
import com.unqiuehire.kashflow.dto.responsedto.LoginResponseDto;

public interface AuthService {

    ApiResponse<LoginResponseDto> borrowerLogin(LoginRequestDto dto);

    ApiResponse<LoginResponseDto> lenderLogin(LoginRequestDto dto);
}