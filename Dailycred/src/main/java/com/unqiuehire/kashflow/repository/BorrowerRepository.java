package com.unqiuehire.kashflow.repository;

import com.unqiuehire.kashflow.entity.Borrower;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BorrowerRepository extends JpaRepository<Borrower,Long> {
    Optional<Borrower> findByPhoneNumber(String phoneNumber);
}
