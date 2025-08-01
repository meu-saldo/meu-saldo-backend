package com.nathannolacio.meusaldo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nathannolacio.meusaldo.model.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionRequestDTO(
        @JsonFormat(pattern = "yyyy-MM-dd")
        @NotNull(message = "A data não pode ser nula")
        LocalDate date,

        @NotBlank(message = "Descrição não pode estar vazia")
        String description,

        @DecimalMin(value = "0.01")
        @NotNull(message = "O campo valor não pode estar vazio")
        BigDecimal amount,

        @NotNull(message = "O tipo da transação deve ser preenchido")
        TransactionType type,

        @NotNull(message = "É necessário passar o id da conta")
        Long accountId
        ) {
}
