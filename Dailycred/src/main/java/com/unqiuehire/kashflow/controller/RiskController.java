package com.unqiuehire.kashflow.controller;

import com.unqiuehire.kashflow.dto.responsedto.RiskResultResponseDto;
import com.unqiuehire.kashflow.serviceImpl.RiskAnalysisServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/risk")
@RequiredArgsConstructor
public class RiskController {

    private final RiskAnalysisServiceImpl service;

    @GetMapping("/{borrowerId}")
    public ResponseEntity<RiskResultResponseDto> getRisk(@PathVariable Long borrowerId) {
        return ResponseEntity.ok(service.calculateRisk(borrowerId));
    }
}