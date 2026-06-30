package com.example.neobank.controller;

import com.example.neobank.dto.*;
import com.example.neobank.entity.*;
import com.example.neobank.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final BankAccountRepository bankAccountRepository;
    private final TransactionRepository transactionRepository;

    // 🔹 1. View all users
    @GetMapping("/users")
    public List<AdminUserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> new AdminUserResponse(
                        user.getId(),
                        user.getFullName(),
                        user.getEmail(),
                        user.getRole()
                ))
                .toList();
    }

    // 🔹 2. View all accounts
    @GetMapping("/accounts")
    public List<AdminAccountResponse> getAllAccounts() {
        return bankAccountRepository.findAll()
                .stream()
                .map(acc -> new AdminAccountResponse(
                        acc.getAccountNumber(),
                        acc.getBalance(),
                        acc.getUser().getFullName(),
                        acc.getUser().getEmail()
                ))
                .toList();
    }

    // 🔹 3. View all transactions (system-wide)
    @GetMapping("/transactions")
    public List<AdminTransactionResponse> getAllTransactions() {
        return transactionRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(tx -> new AdminTransactionResponse(
                        tx.getId(),
                        tx.getAccount().getAccountNumber(),
                        tx.getType(),
                        tx.getAmount(),
                        tx.getBalanceAfter(),
                        tx.getDescription(),
                        tx.getCreatedAt()
                ))
                .toList();
    }

    // 🔹 4. View transactions of a specific account
    @GetMapping("/accounts/{accountNumber}/transactions")
    public List<AdminTransactionResponse> getTransactionsByAccount(
            @PathVariable String accountNumber
    ) {
        BankAccount account = bankAccountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        return transactionRepository.findByAccountOrderByCreatedAtDesc(account)
                .stream()
                .map(tx -> new AdminTransactionResponse(
                        tx.getId(),
                        account.getAccountNumber(),
                        tx.getType(),
                        tx.getAmount(),
                        tx.getBalanceAfter(),
                        tx.getDescription(),
                        tx.getCreatedAt()
                ))
                .toList();
    }
}
