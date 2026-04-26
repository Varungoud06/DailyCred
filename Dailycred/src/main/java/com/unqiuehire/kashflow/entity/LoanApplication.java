package com.unqiuehire.kashflow.entity;

import com.unqiuehire.kashflow.constant.ApplicationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "loan_applications")
@Getter
@Setter
public class LoanApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long applicationId;

    @ManyToOne
    @JoinColumn(name = "borrower_id", nullable = false)
    private Borrower borrower;

    @ManyToOne
    @JoinColumn(name = "plan_id", nullable = false)
    private LoanPlan loanPlan;

    @Column(nullable = false)
    private Double loanAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status;

    @Column(nullable = false)
    private LocalDate applicationDate;

    private String rejectionReason;

    @Column(nullable = false)
    private Boolean isLoanCreated = false;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.applicationDate = LocalDate.now();

        if (this.status == null) {
            this.status = ApplicationStatus.PENDING;
        }

        if (this.isLoanCreated == null) {
            this.isLoanCreated = false;
        }
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}