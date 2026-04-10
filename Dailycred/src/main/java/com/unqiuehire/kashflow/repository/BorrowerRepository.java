package com.unqiuehire.kashflow.repository;

import com.unqiuehire.kashflow.entity.Borrower;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BorrowerRepository extends JpaRepository<Borrower,Long> {
}
