package com.unqiuehire.kashflow.repository;

import com.unqiuehire.kashflow.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface LoanRepository extends JpaRepository<Loan,Long> {
    List<Loan> findByBorrower_BorrowerId(Long borrowerId);

    List<Loan> findByLender_LenderId(Long lenderId);

//        boolean existsByLoanApplication_ApplicationId(Long applicationId);
//
//        Loan findByLoanApplication_ApplicationId(Long applicationId);
    }