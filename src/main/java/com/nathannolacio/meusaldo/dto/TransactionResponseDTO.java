package com.nathannolacio.meusaldo.dto;

import com.nathannolacio.meusaldo.model.Account;
import com.nathannolacio.meusaldo.model.Transaction;
import com.nathannolacio.meusaldo.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionResponseDTO(Long id, LocalDate date, String description, BigDecimal amount, String type, String accountName
) {
    public TransactionResponseDTO(Transaction transaction) {
        this(
                transaction.getId(),
                transaction.getDate(),
                transaction.getDescription(),
                transaction.getAmount(),
                transaction.getType().getLabel(),
                transaction.getAccount().getName()
        );
    }
}
