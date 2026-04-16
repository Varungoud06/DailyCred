package com.unqiuehire.kashflow.entity;

import com.unqiuehire.kashflow.entity.LoanApplication;
import com.unqiuehire.kashflow.constant.PaymentMode;
import com.unqiuehire.kashflow.constant.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Repayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double amountPaid;

    private LocalDate paymentDate;

    @Enumerated(EnumType.STRING)
    private PaymentMode paymentMode;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    private String transactionReference;

    // 🔗 Loan
    @ManyToOne
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    // 🔗 Loan Application
    @ManyToOne
    @JoinColumn(name = "loan_applications_id", nullable = false)
    private LoanApplication loanApplication;
}