package com.nathannolacio.meusaldo.dto;

import com.nathannolacio.meusaldo.model.Expense;

public record ExpenseResponseDTO(Long id, String description, ExpenseTypeDTO type, Double amount) {
    public ExpenseResponseDTO(Expense expense) {
        this(
                expense.getId(),
                expense.getDescription(),
                new ExpenseTypeDTO(
                        expense.getType().name(),
                        expense.getType().getLabel()
                ),
                expense.getAmount()
        );
    }
}
