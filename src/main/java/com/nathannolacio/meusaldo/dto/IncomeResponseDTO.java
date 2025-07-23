package com.nathannolacio.meusaldo.dto;

import com.nathannolacio.meusaldo.model.Income;

import java.math.BigDecimal;

public record IncomeResponseDTO(Long id, String description, BigDecimal amount) {
    public IncomeResponseDTO(Income income) {
        this(
                income.getId(),
                income.getDescription(),
                income.getAmount()
        );
    }
}
