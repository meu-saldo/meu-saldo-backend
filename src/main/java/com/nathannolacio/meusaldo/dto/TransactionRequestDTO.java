package com.nathannolacio.meusaldo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nathannolacio.meusaldo.model.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record TransactionRequestDTO(
        @JsonFormat(pattern = "yyyy-MM-dd")
        @NotNull(message = "A data não pode ser nula")
        LocalDate date,

        @NotBlank(message = "Descrição não pode estar vazia")
        String description,

        @NotNull(message = "O campo valor não pode estar vazio")
        Double amount,

        @NotNull(message = "O tipo da transação deve ser preenchido")
        TransactionType type,

        @NotNull(message = "É necessário passar o id da conta")
        Long accountId,

        @NotNull(message = "É necessário passar o id do usuário")
        Long userId
        ) {
}
