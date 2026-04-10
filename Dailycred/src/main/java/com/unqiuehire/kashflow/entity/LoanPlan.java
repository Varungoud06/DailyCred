package com.unqiuehire.kashflow.entity;

import com.unqiuehire.kashflow.constant.LoanPlanStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "loan_plan")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String planName;
    private Long lenderId;
    private double amount;
    private double interestPerDay;
    private double penaltyAmount;
    private int planDuration;
    private double maxRadius;
    private int minCibil;

    @Enumerated(EnumType.STRING)
    private LoanPlanStatus status;
}