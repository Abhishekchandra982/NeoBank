package com.example.neobank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class AccountDetailsResponse {
    private String accountNumber;
    private BigDecimal balance;
}
