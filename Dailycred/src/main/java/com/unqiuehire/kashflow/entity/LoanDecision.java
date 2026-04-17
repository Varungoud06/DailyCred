package com.unqiuehire.kashflow.entity;

import com.unqiuehire.kashflow.constant.LoanDecisionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table (name = "loan_decision")

@Data
@AllArgsConstructor
@NoArgsConstructor

public class LoanDecision {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "decision_id")
    private Long decisionId;

    @Column(name = "loan_application_id", nullable = false)
    private Long loanApplicationId;

    @Column(name = "loan_id", nullable = false)
    private Long loanId;

    @Column(name = "lender_id", nullable = false)
    private Long lenderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "decision", nullable = false)
    private LoanDecisionStatus decision;

    @Column(name = "reason")
    private String reason;

    @Column(name = "decided_at", nullable = false)
    private LocalDateTime decidedAt;
}
