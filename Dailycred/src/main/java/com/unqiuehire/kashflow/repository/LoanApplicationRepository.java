package com.unqiuehire.kashflow.repository;


import com.unqiuehire.kashflow.entity.Loan;
import com.unqiuehire.kashflow.entity.LoanApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface LoanApplicationRepository extends JpaRepository<LoanApplication, Long> {

    List<LoanApplication> findByBorrower_BorrowerId(Long borrowerId);

    List<LoanApplication> findByLender_LenderId(Long lenderId);
//
//    boolean existsByLoanApplication_ApplicationId(Long applicationId);
}