package com.unqiuehire.kashflow.dto.responsedto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanPlanResponseDto {
    private Long id;
    private String planName;
    private Long lenderId;
    private double amount;
    private double interestPerDay;
    private double penaltyAmount;
    private int planDuration;
    private double maxRadius;
    private int minCibil;
    private String status;
}