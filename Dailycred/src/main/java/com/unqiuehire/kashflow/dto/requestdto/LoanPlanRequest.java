package com.unqiuehire.kashflow.dto.requestdto;

import lombok.Data;

@Data
public class LoanPlanRequest {

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