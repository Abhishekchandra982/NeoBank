package com.example.neobank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class AdminAccountResponse {
    private String accountNumber;
    private BigDecimal balance;
    private String ownerName;
    private String ownerEmail;
}
