package com.unqiuehire.kashflow.dto.responsedto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoanPlanResponse {

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