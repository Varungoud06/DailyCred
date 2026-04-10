package com.unqiuehire.kashflow.controller;

import com.unqiuehire.kashflow.dto.requestdto.LoginRequest;
import com.unqiuehire.kashflow.dto.responsedto.LoginResponse;
import com.unqiuehire.kashflow.service.AuthService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/lender/login")
    public ResponseEntity<LoginResponse> lenderLogin(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request, "LENDER"));
    }

    @PostMapping("/borrower/login")
    public ResponseEntity<LoginResponse> borrowerLogin(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request, "BORROWER"));
    }
}