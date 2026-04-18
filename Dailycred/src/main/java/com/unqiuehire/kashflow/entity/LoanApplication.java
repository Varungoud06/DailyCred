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

    private Long borrowerId;

    private Double loanAmount;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    private LocalDate applicationDate;
    private String rejectionReason;
    private Boolean isLoanCreated = false;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    //  RELATION
    @ManyToOne
    @JoinColumn(name = "plan_id", nullable = false)
    private LoanPlan loanPlan;

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