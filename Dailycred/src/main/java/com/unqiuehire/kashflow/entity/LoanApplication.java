package com.unqiuehire.kashflow.entity;

import com.unqiuehire.kashflow.constant.ApplicationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "loan_applications")
@Getter
@Setter
public class LoanApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long applicationId;

//    private Long borrowerId;
//    private Long lenderId;
    private Long planId;

    private Double loanAmount;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    private LocalDate applicationDate;
    private String rejectionReason;
    private Boolean isLoanCreated = false;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "loanApplication", cascade = CascadeType.ALL)
    private Loan loan;

    @OneToMany(mappedBy = "loanApplication", cascade = CascadeType.ALL)
    private List<LoanDecision> decisions;

    @ManyToOne
    @JoinColumn(name = "borrower_id")
    private Borrower borrower;

    @ManyToOne
    @JoinColumn(name = "lender_id")
    private Lender lender;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.applicationDate = LocalDate.now();
        this.status = ApplicationStatus.PENDING;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}