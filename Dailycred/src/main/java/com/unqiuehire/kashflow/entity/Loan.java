package com.unqiuehire.kashflow.entity;

import com.unqiuehire.kashflow.constant.LoanStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "loan")
@Getter
@Setter
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long loanId;

    private Long loanApplicationId;
    private Long borrowerId;
    private Long lenderId;
    private Long planId;
    private Double totalAmount;
    private Double sanctionedAmount;
    private Double interestPerDay;
    private Double penaltyAmount;

    private Double remainingAmount;
    private Integer tenureDays;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double dailyEmi;

    @Enumerated(EnumType.STRING)
    private LoanStatus loanStatus;
}