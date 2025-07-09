package com.nathannolacio.meusaldo.dto;

import com.nathannolacio.meusaldo.model.Account;
import com.nathannolacio.meusaldo.model.Transaction;
import com.nathannolacio.meusaldo.model.TransactionType;

import java.time.LocalDate;

public record TransactionResponseDTO(Long id, LocalDate date, String description, Double amount, String type, String accountName, String userName
) {
    public TransactionResponseDTO(Transaction transaction) {
        this(
                transaction.getId(),
                transaction.getDate(),
                transaction.getDescription(),
                transaction.getAmount(),
                transaction.getType().getLabel(),
                transaction.getAccount().getName(),
                transaction.getUser().getName());
    }
}
