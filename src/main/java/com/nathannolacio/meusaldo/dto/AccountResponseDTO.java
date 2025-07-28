package com.nathannolacio.meusaldo.dto;

import com.nathannolacio.meusaldo.model.Account;

import java.math.BigDecimal;

public record AccountResponseDTO(Long id, String name, String description, BigDecimal balance) {
    public AccountResponseDTO(Account account) {
        this(
                account.getId(),
                account.getName(),
                account.getDescription(),
                account.getBalance()
        );
    }
}
