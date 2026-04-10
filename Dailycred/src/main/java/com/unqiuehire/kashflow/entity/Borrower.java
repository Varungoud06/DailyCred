package com.unqiuehire.kashflow.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "borrower")
@Getter
@Setter
public class Borrower {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "borrower_id")
    private Long borrowerId;

    @Column(name = "borrower_name", nullable = false)
    private String borrowerName;

    @Column(name = "cibil", nullable = false)
    private Integer cibil;

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
}
