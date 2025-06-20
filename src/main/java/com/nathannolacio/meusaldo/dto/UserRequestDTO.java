package com.nathannolacio.meusaldo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequestDTO(
        @Schema(description = "User name", example = "Nathan Nolacio")
        @NotBlank(message = "O nome é um campo obrigatório")
        @Size(min = 5, message = "O nome deve ter pelo menos 5 caracteres")
        String name,

        @Schema(description = "User email", example = "nathan@gmail.com")
        @NotBlank(message = "O email é um campo obrigatório")
        @Email(message = "O email deve ser válido")
        String email,

        @Schema(description = "User password", example = "password123")
        @NotBlank(message = "A senha é um campo obrigatório")
        String password) {
}
