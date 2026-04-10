package com.unqiuehire.kashflow.repository;


import com.unqiuehire.kashflow.entity.LoanDecision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoanDecisionRepository extends JpaRepository<LoanDecision, Long> {
    Optional<LoanDecision> findByLoanApplicationId(Long loanApplicationId);
    boolean existsByLoanApplicationId(Long loanApplicationId);
}
