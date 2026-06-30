package com.example.neobank.controller;

import com.example.neobank.dto.*;
import com.example.neobank.entity.*;
import com.example.neobank.repository.BankAccountRepository;
import com.example.neobank.repository.TransactionRepository;
import com.example.neobank.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final UserRepository userRepository;
    private final BankAccountRepository bankAccountRepository;
    private final TransactionRepository transactionRepository;

    // 🔹 BALANCE
    @GetMapping("/balance")
    public BigDecimal getBalance(Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        BankAccount account = bankAccountRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        return account.getBalance();
    }

    // 🔹 DEPOSIT
    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(
            @Valid @RequestBody DepositRequest request,
            Authentication authentication
    ) {
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        BankAccount account = bankAccountRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Bank account not found"));

        BigDecimal newBalance = account.getBalance().add(request.getAmount());
        account.setBalance(newBalance);
        bankAccountRepository.save(account);

        // 🧾 Transaction record
        transactionRepository.save(
                Transaction.builder()
                        .account(account)
                        .type(TransactionType.DEPOSIT)
                        .amount(request.getAmount())
                        .balanceAfter(newBalance)
                        .description("Deposit")
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        return ResponseEntity.ok(
                Map.of(
                        "message", "Deposit successful",
                        "balance", newBalance
                )
        );
    }

    // 🔹 WITHDRAW
    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(
            @Valid @RequestBody WithdrawRequest request,
            Authentication authentication
    ) {
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        BankAccount account = bankAccountRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        BigDecimal amount = request.getAmount();

        if (account.getBalance().compareTo(amount) < 0) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "Insufficient balance"));
        }

        BigDecimal newBalance = account.getBalance().subtract(amount);
        account.setBalance(newBalance);
        bankAccountRepository.save(account);

        // 🧾 Transaction record
        transactionRepository.save(
                Transaction.builder()
                        .account(account)
                        .type(TransactionType.WITHDRAW)
                        .amount(amount)
                        .balanceAfter(newBalance)
                        .description("Withdraw")
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        return ResponseEntity.ok(
                Map.of(
                        "message", "Withdrawal successful",
                        "balance", newBalance
                )
        );
    }

    // 🔹 TRANSFER
    @PostMapping("/transfer")
    @Transactional
    public ResponseEntity<?> transfer(
            @Valid @RequestBody TransferRequest request,
            Authentication authentication
    ) {
        String email = authentication.getName();

        User sender = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        BankAccount senderAccount = bankAccountRepository.findByUser(sender)
                .orElseThrow(() -> new RuntimeException("Sender account not found"));

        BankAccount receiverAccount = bankAccountRepository
                .findByAccountNumber(request.getToAccountNumber())
                .orElseThrow(() -> new RuntimeException("Receiver account not found"));

        if (senderAccount.getAccountNumber().equals(receiverAccount.getAccountNumber())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Cannot transfer to same account"));
        }

        BigDecimal amount = request.getAmount();

        if (senderAccount.getBalance().compareTo(amount) < 0) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Insufficient balance"));
        }

        // 💸 Update balances
        senderAccount.setBalance(senderAccount.getBalance().subtract(amount));
        receiverAccount.setBalance(receiverAccount.getBalance().add(amount));

        bankAccountRepository.save(senderAccount);
        bankAccountRepository.save(receiverAccount);

        // 🧾 Sender transaction
        transactionRepository.save(
                Transaction.builder()
                        .account(senderAccount)
                        .type(TransactionType.TRANSFER_SENT)
                        .amount(amount)
                        .balanceAfter(senderAccount.getBalance())
                        .description("Transferred to " + receiverAccount.getAccountNumber())
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        // 🧾 Receiver transaction
        transactionRepository.save(
                Transaction.builder()
                        .account(receiverAccount)
                        .type(TransactionType.TRANSFER_RECEIVED)
                        .amount(amount)
                        .balanceAfter(receiverAccount.getBalance())
                        .description("Received from " + senderAccount.getAccountNumber())
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        return ResponseEntity.ok(
                Map.of(
                        "message", "Transfer successful",
                        "balance", senderAccount.getBalance()
                )
        );
    }

    // 🔹 TRANSACTION HISTORY
    @GetMapping("/transactions")
    public ResponseEntity<?> getTransactions(Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        BankAccount account = bankAccountRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        List<TransactionResponse> responses =
                transactionRepository.findByAccountOrderByCreatedAtDesc(account)
                        .stream()
                        .map(tx -> TransactionResponse.builder()
                                .id(tx.getId())
                                .type(tx.getType())
                                .amount(tx.getAmount())
                                .balanceAfter(tx.getBalanceAfter())
                                .description(tx.getDescription())
                                .createdAt(tx.getCreatedAt())
                                .build()
                        )
                        .toList();

        return ResponseEntity.ok(responses);
    }
    @GetMapping("/details")
    public ResponseEntity<AccountDetailsResponse> getAccountDetails(Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        BankAccount account = bankAccountRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        AccountDetailsResponse response =
                new AccountDetailsResponse(
                        account.getAccountNumber(),
                        account.getBalance(),
                        user.getFullName()   // 👈 NOW SENDING NAME
                );

        return ResponseEntity.ok(response);
    }

}
