package com.nathannolacio.meusaldo.dto;

import com.nathannolacio.meusaldo.model.ExpenseType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ExpenseRequestDTO(
        @NotBlank(message = "A descrição não pode estar vazia")
        String description,

        @NotNull(message = "O tipo de despesa deve ser preenchido")
        ExpenseType type,

        @NotNull(message = "O valor da despesa deve ser preenchido")
        Double amount
) {
}
