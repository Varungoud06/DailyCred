package com.unqiuehire.kashflow.dto.responsedto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//public class RiskResultResponseDto {
//
//    private int score;
//    private String category;
//    private Map<String, Double> factors;
//    private List<String> recommendations;
//}



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RiskResultResponseDto {

    private int score;                      // 0–100 risk score
    private String category;               // LOW_RISK, MEDIUM_RISK, etc.

    // 🔥 NEW FIELDS
    private String eligibility;            // HIGH_LOAN, MEDIUM_LOAN, etc.
    private double eligibleLoanAmount;     // Suggested loan amount

    private Map<String, Double> factors;   // Factor breakdown
    private List<String> recommendations;  // Suggestions
}
