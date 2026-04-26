package com.unqiuehire.kashflow.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "borrower",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_borrower_phone_number", columnNames = "phone_number"),
                @UniqueConstraint(name = "uk_borrower_aadhar_card_number", columnNames = "aadhar_card_number"),
                @UniqueConstraint(name = "uk_borrower_pan_card_number", columnNames = "pan_card_number")
        }
)
@Getter
@Setter
public class Borrower {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "borrower_id")
    private Long borrowerId;

    @Column(name = "borrower_name", nullable = false, length = 100)
    private String borrowerName;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "phone_number", nullable = false, unique = true, length = 20)
    private String phoneNumber;

    @Column(name = "pincode", nullable = false, length = 10)
    private String pincode;

    @Column(name = "address", nullable = false, length = 500)
    private String address;

    @Column(name = "cibil", nullable = false)
    private Integer cibil;

    @Column(name = "aadhar_card_number", unique = true, length = 20)
    private String aadharCardNumber;

    @Column(name = "pan_card_number", unique = true, length = 20)
    private String panCardNumber;

    // --------- RISK ANALYSIS READY FIELDS ---------
    @Column(name = "monthly_income")
    private BigDecimal monthlyIncome;

    @Column(name = "income_type", length = 50)
    private String incomeType;

    @Column(name = "employment_type", length = 50)
    private String employmentType;

    @Column(name = "years_in_current_work")
    private Integer yearsInCurrentWork;

    @Column(name = "dependents_count")
    private Integer dependentsCount;

    @Column(name = "house_owned")
    private Boolean houseOwned;

    @Column(name = "shop_owned")
    private Boolean shopOwned;

    @Column(name = "risk_score")
    private Integer riskScore = 50;

    @Column(name = "eligibility_score")
    private Integer eligibilityScore = 50;

    @Column(name = "total_loans_taken")
    private Integer totalLoansTaken = 0;

    @Column(name = "loans_closed_successfully")
    private Integer loansClosedSuccessfully = 0;

    @Column(name = "loans_closed_early")
    private Integer loansClosedEarly = 0;

    @Column(name = "total_missed_days")
    private Integer totalMissedDays = 0;

    @Column(name = "total_partial_days")
    private Integer totalPartialDays = 0;

    @Column(name = "total_advance_days")
    private Integer totalAdvanceDays = 0;

    @OneToMany(mappedBy = "borrower", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<LoanApplication> loanApplications = new ArrayList<>();
}