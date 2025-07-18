package com.nathannolacio.meusaldo.dto;

import com.nathannolacio.meusaldo.model.Expense;

public record ExpenseResponseDTO(Long id, String description, String type, Double amount) {
    public ExpenseResponseDTO(Expense expense) {
        this(
                expense.getId(),
                expense.getDescription(),
                expense.getType().getLabel(),
                expense.getAmount()
        );
    }
}
