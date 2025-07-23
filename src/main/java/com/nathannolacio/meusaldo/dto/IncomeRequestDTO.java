package com.nathannolacio.meusaldo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record IncomeRequestDTO(
        @NotBlank(message = "A descrição deve ser preenchida")
        String description,

        @NotNull(message = "O valor deve ser preenchido")
        BigDecimal amount
) {}
