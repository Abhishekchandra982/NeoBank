package com.example.neobank.repository;

import com.example.neobank.entity.BankAccount;
import com.example.neobank.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findAllByOrderByCreatedAtDesc();

    List<Transaction> findByAccountOrderByCreatedAtDesc(BankAccount account);
}
