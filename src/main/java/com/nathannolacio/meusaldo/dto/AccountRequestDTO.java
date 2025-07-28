package com.nathannolacio.meusaldo.dto;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record AccountRequestDTO(
        @NotBlank(message = "O nome dever ser preenchido")
        String name,
        String description
) {}
