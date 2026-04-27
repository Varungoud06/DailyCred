package com.unqiuehire.kashflow.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "lender")
@Getter
@Setter
public class Lender {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lender_id")
    private Long lenderId;

    @Column(name = "lender_name", nullable = false)
    private String lenderName;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "phone_number", nullable = false, unique = true)
    private String phoneNumber;

    @Column(name = "pincode", nullable = false)
    private String pincode;

    @Column(name = "address", nullable = false, length = 500)
    private String address;

    @Column(name = "loan_application", nullable = false)
    @OneToMany(mappedBy = "lender")
    private List<LoanApplication> loanApplications;

    //  ONE TO MANY
    @OneToMany(mappedBy = "lender", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LoanPlan> loanPlans;

    @Column(name = "aadhar_card_number",unique = true)
    private String aadharCardNumber;

    @Column(name = "pan_card_number",unique = true)
    private String panCardNumber;
}